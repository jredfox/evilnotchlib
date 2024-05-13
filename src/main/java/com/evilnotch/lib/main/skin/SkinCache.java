package com.evilnotch.lib.main.skin;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
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

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SkinCache {
	
	public HashMap<String, SkinEntry> skins = new HashMap(Config.fixSkins ? 100 : 1);
	public File skinCacheLoc = new File(System.getProperty("user.dir"), "skinCache.json");
	
	public void load()
	{
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
			arr.add(skin.serialize());
		}
		JavaUtil.saveJSONArray(arr, skinCacheLoc);
	}
	
	public SkinEntry getSkinEntry(String user)
	{
		user = user.toLowerCase();
		return skins.containsKey(user) ? skins.get(user) : cacheSkin(user);
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
		//if it doesn't exist cache it
		if(entry == null)
			return true;
		
		//check if it's expired
		if(hasExpired(entry))
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
			return null;
		}
		
		//downloads the skin data and if it fails it will get added to the skin refresher
		JSONObject json = getMojangProfile(uuid);
		if(json == null)
		{
			System.err.println("Error Unable to get Mojang profile:" + user);
			return null;
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
	
	public static String getMojangUUID(String username) {
		BufferedReader stream = null;
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			stream = new BufferedReader(new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			String id = json.getString("id").replace("-", "");
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	public static SkinCache INSTANCE;
	public static void start() 
	{
		System.out.println("Loading Skin Fix");
		long ms = System.currentTimeMillis();
		INSTANCE = new SkinCache();
		INSTANCE.load();
		INSTANCE.refreshClientSkin();
		INSTANCE.save();
		JavaUtil.printTime(ms, "Skin Fix Took:");
	}

	public void refreshClientSkin() 
	{
		Minecraft mc = Minecraft.getMinecraft();
		Session session = mc.getSession();
		GameProfile profile = session.getProfile();
		String username = Config.skin.isEmpty() ? profile.getName() : Config.skin;
		SkinEntry skin = INSTANCE.refresh(username, false).copy();
		skin.user = profile.getName();
		
		//overide the cape to what the client has sent
		if(!Config.cape.isEmpty())
			skin.cape = Config.cape;
		
		//Fix the skin if it's not erroring
		if(skin != null)
		{
			String base64payload = skin.encode();
			PropertyMap properties = new PropertyMap();
			properties.removeAll("textures");
			properties.put("textures", new Property("textures", base64payload));
			
			ReflectionUtil.setObject(session, null, Session.class, "properties");
			ReflectionUtil.setFinalObject(mc, properties, Minecraft.class,
					new MCPSidedString("profileProperties", "field_181038_N").toString());
			session.setProperties(properties);
		}
	}

}
