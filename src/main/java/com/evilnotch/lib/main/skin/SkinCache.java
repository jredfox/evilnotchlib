package com.evilnotch.lib.main.skin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParser;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.util.JavaUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SkinCache {
	
	public HashMap<String, SkinEntry> skins = new HashMap(100);
	public Set<String> refreshque = new HashSet();
	public File skinCacheLoc = new File(System.getProperty("user.dir"), "skinCacher.json");
	public boolean dirty = false;
	public static final SkinEntry EMPTY = new SkinEntry("", "", System.currentTimeMillis(), "", "", "", "");
	
	public void load()
	{
		//Update the White listed domains
		ReflectionUtil.setFinalObject(null, Config.skinDomains, YggdrasilMinecraftSessionService.class, "WHITELISTED_DOMAINS");
		
		boolean isOnline = JavaUtil.isOnline(null);
		JSONArray arr = skinCacheLoc.exists() ? JavaUtil.getJsonArray(skinCacheLoc) : new JSONArray();
		int i = 0;
		for(Object o : arr)
		{
			if(!(o instanceof JSONObject))
				continue;
			
			JSONObject j = (JSONObject) o;
			SkinEntry data = new SkinEntry(j);
			if(isOnline && hasExpired(data))
			{
				System.out.println("removing expired skin from cache:" + data.user + " uuid:" + data.uuid);
				continue;
			}
			skins.put(data.user, data);
			i++;
			if(i >= Config.skinCacheMax)
				break;
		}
	}
	
	public void save()
	{
		JSONArray arr = new JSONArray();
		for(SkinEntry skin : skins.values())
		{
			if(!skin.isEmpty)
				arr.add(skin.serialize());
		}
		JavaUtil.saveJSONArray(arr, skinCacheLoc);
	}
	
	public SkinEntry getSkinEntry(String user)
	{
		user = user.toLowerCase();
		return refreshque.contains(user) ? EMPTY : skins.containsKey(user) ? skins.get(user) : cacheSkin(user);
	}

	public SkinEntry refresh(String user, boolean force)
	{
		user = user.toLowerCase();
		if(force || shouldRefresh(user, true))
		{
			System.out.println("Refreshing Skin:" + user);
			return cacheSkin(user);
		}
		return skins.get(user);
	}

	public boolean shouldRefresh(String user, boolean checkHashes) 
	{
		user = user.toLowerCase();
		SkinEntry entry = skins.get(user);
		//if it doesn't exist or has expired cache it
		if(entry == null || entry.isEmpty || hasExpired(entry))
			return true;
		
		//Uses Crafatar.com as a hash checker if they don't match sync with mojang
		if(checkHashes && JavaUtil.isOnline("crafatar.com"))
		{
			return !entry.skinhash.equals(getCrafatarHash(entry.uuid, false)) || !entry.capehash.equals(getCrafatarHash(entry.uuid, true));
		}
		return false;
	}
	
	public boolean hasExpired(SkinEntry entry) 
	{
		return System.currentTimeMillis() >= (entry.cacheTime + ( ( (Config.skinCacheHours * 60L) * 60L) * 1000L) );
	}

	public static String getCrafatarHash(String uuid, boolean isCape)
	{
		return JavaUtil.getOnlinePNGMD5(isCape ? ("https://crafatar.com/capes/" + uuid) : ("https://crafatar.com/skins/" + uuid));
	}

	public boolean playerdb = false;
	public SkinEntry cacheSkin(String user)
	{
		//is player db online
		if(playerdb || JavaUtil.isOnline("playerdb.co"))
			playerdb = true;
		
		//fetches the real uuid of the player
		JSONObject dbjson = playerdb ? getPlayerDBJSON(user) : null;
		String uuid = dbjson != null && dbjson.containsKey("id") ? dbjson.getString("id").replace("-", "") : getMojangUUID(user);
		
		//Error occured fetching the UUID
		if(uuid == null)
		{
			System.err.println("Error Unable to get UUID of player:" + user);
			return EMPTY;
		}
		
		//downloads the skin data and if it fails it will get added to the skin refresher
		JSONObject json = getMojangProfile(uuid);
		if(json == null)
		{
			System.err.println("Error Unable to get Mojang profile:" + user);
			refreshque.add(user);
			return EMPTY;
		}
		
		String base64payload = json.getJSONArray("properties").getJSONObject(0).getString("value");
		JSONObject decoded = JavaUtil.toJsonFrom64(base64payload);
		JSONObject textures = decoded.getJSONObject("textures");
		String skin = textures.getJSONObject("SKIN").getString("url");
		String cape = "";
		String skinhash = JavaUtil.getOnlinePNGMD5(skin);
		String capehash = "";
		
		if(textures.containsKey("CAPE"))
		{
			cape = textures.getJSONObject("CAPE").getString("url");
			capehash = JavaUtil.getOnlinePNGMD5(cape);
		}
		
		SkinEntry entry = new SkinEntry(uuid, user, System.currentTimeMillis(), skin, cape, skinhash, capehash);
		skins.put(user, entry);
		this.refreshque.remove(user);
		this.dirty = true;
		this.save();//TODO: remove to save every 2s when going multi-threaded
		return entry;
	}
	
	/**
	 * unlike mojang will not return an error code 429 (too many requests) when obtaining the uuid of the player
	 */
	public static JSONObject getPlayerDBJSON(String username) 
	{
		BufferedReader stream = null;
		URLConnection con = null;
		try {
			URL url = new URL("https://playerdb.co/api/player/minecraft/" + username);
			con = url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla");
			con.setConnectTimeout(3500);
			stream = new BufferedReader(new InputStreamReader(con.getInputStream()));

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			JSONObject data = json.getJSONObject("data");
			JSONObject player = data.getJSONObject("player");
			return player;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
			if (con instanceof HttpURLConnection)
				((HttpURLConnection) con).disconnect();
		}
	}
	
	public static JSONObject getMojangProfile(String uuid) {
		BufferedReader stream = null;
		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			stream = new BufferedReader(new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	public volatile boolean lockedUUID = false;
	public String getMojangUUID(String username) 
	{
		if(lockedUUID)
		{
			JavaUtil.sleep(5000L);
			this.lockedUUID = false;
		}
		
		BufferedReader stream = null;
		URLConnection con = null;
		try 
		{
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			con = url.openConnection();
			con.setConnectTimeout(3500);
			stream = new BufferedReader(new InputStreamReader(con.getInputStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			String id = json.getString("id").replace("-", "");
			return id;
		}
		catch (IOException e)
		{
			//if error code isn't 429 aka too many requests assume it's a bad username
			if(JavaUtil.isOnline("api.mojang.com"))
			{
				int response = getCode(con);
				System.out.println("code:" + response);
				if(response == 429)
				{
					this.lockedUUID = true;
					this.refreshque.add(username);
				}
				else if(response == 404)
				{
					this.refreshque.remove(username);
				}
				else
					e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		finally 
		{
			IOUtils.closeQuietly(stream);
		}
		return null;
	}

	public int getCode(URLConnection con)
	{
		if(con instanceof HttpURLConnection)
		{
			try 
			{
				return ((HttpURLConnection)con).getResponseCode();
			} 
			catch(UnknownServiceException um)
			{
				return 415;//unsupported media
			}
			catch(UnknownHostException uh)
			{
				return 10001;//Custom Defined error code to specify unkown host exception
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static SkinCache INSTANCE;
	public static void init() 
	{
		System.out.println("Loading Skin Cache");
		long ms = System.currentTimeMillis();
		INSTANCE = new SkinCache();
		INSTANCE.load();
		INSTANCE.save();
		JavaUtil.printTime(ms, "Skin Cache Took:");
	}

	public void refreshClientSkin() 
	{
		Minecraft mc = Minecraft.getMinecraft();
		Session session = mc.getSession();
		GameProfile profile = session.getProfile();
		String username = profile.getName();
		SkinEntry skin = INSTANCE.refresh(username, false).copy();
		skin.user = username;
		skin.uuid = profile.getId().toString().replace("-", "");
		
		//Fix the skin if it's not erroring
		if(skin != null)
		{
			String base64payload = skin.encode();
			PropertyMap properties = new PropertyMap();
			properties.removeAll("textures");
			properties.put("textures", new EvilProperty("textures", base64payload));
			
			ReflectionUtil.setObject(session, null, Session.class, "properties");
			ReflectionUtil.setFinalObject(mc, properties, Minecraft.class,
					new MCPSidedString("profileProperties", "field_181038_N").toString());
			session.setProperties(properties);
		}
	}
	
	public static class EvilProperty extends Property
	{
		public EvilProperty(String name, String value)
		{
			super(name, value, null);
		}
		
//		@Override
//		public boolean isSignatureValid(final PublicKey publicKey)
//		{
//			return true;
//		}
	}

}
