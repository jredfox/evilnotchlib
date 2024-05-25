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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParser;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.capability.CapRegDefaultHandler;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketSkinChange;
import com.evilnotch.lib.minecraft.util.UUIDPatcher;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairObj;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SkinCache {
	
	public static final SkinEntry EMPTY = new SkinEntry("", "", System.currentTimeMillis(), "", "", "");
	
	public Map<String, SkinEntry> skins = new ConcurrentHashMap(25);
	public Map<String, PairObj<SkinEntry, Boolean>> refreshque = new HashMap<>();
	public File skinCacheLoc = new File(System.getProperty("user.dir"), "skinCacher.json");
	public SkinEntry selected = EMPTY;
	public volatile boolean isOnline = false;
	
	public boolean isMojangOnline()
	{
		if(!this.isOnline)
			this.isOnline = JavaUtil.isOnline("sessionserver.mojang.com");
		return this.isOnline;
	}
	
	public void load()
	{
		if(!skins.isEmpty())
		{
			skins.clear();
			synchronized (this.refreshque)
			{
				refreshque.clear();
			}
		}
		
		//Update the White listed domains
		ReflectionUtil.setFinalObject(null, Config.skinDomains, YggdrasilMinecraftSessionService.class, "WHITELISTED_DOMAINS");
		
		boolean isOnline = this.isMojangOnline();
		JSONArray arr = skinCacheLoc.exists() ? JavaUtil.getJsonArray(skinCacheLoc) : new JSONArray();
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
	
	/**
	 * gets the current cached skin will be empty if skins doesn't contain the user
	 */
	public SkinEntry getSkinEntry(String user)
	{
		user = user.toLowerCase();
		return skins.containsKey(user) ? skins.get(user) : EMPTY;
	}
	
	public void select(SkinEntry s)
	{
		this.selected = s;
	}

	/**
	 * returns cached skin if it exists and adds the skin to the refresh cache
	 */
	public SkinEntry refresh(String user, boolean select)
	{
		user = select ? SkinEvent.User.fire(user.toLowerCase()) : user;
		SkinEntry current = getSkinEntry(user);
		if(select)
			this.select(current);
		this.addQue(user, new PairObj<SkinEntry, Boolean>(current, select));
		return current;
	}
	
	public void refreshClientSkin() 
	{
		Minecraft mc = Minecraft.getMinecraft();
		GameProfile profile = mc.getSession().getProfile();
		String username = profile.getName();
		SkinEntry skin = this.refresh(username, true);
	}

	/**
	 * fired after a SkinEntry is downloaded successfully
	 * syncs the selected user with skins#get(user) and updates skin packets if in game
	 */
	public void refreshSelected(SkinEntry dl)
	{
		//update the encoding to send to the server
		this.selected = dl;
		
		//if player is already in the world send a packet
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player != null && mc.player.connection != null && ((CapBoolean) CapabilityRegistry.getCapability(mc.player, CapRegDefaultHandler.addedToWorld)).value)
		{
			NetWorkHandler.INSTANCE.sendToServer(new PacketSkinChange(this.selected));
		}
	}
	
	public void addQue(String user, PairObj<SkinEntry, Boolean> skin)
	{
		synchronized (this.refreshque)
		{
			this.refreshque.put(user.toLowerCase(), skin);
		}
	}
	
	public void removeQue(String user)
	{
		synchronized (this.refreshque)
		{
			this.refreshque.remove(user.toLowerCase());
		}
	}
	
	public void containsQue(String user)
	{
		synchronized (this.refreshque)
		{
			this.refreshque.containsKey(user.toLowerCase());
		}
	}
	
	public boolean hasExpired(SkinEntry entry) 
	{
		return System.currentTimeMillis() >= (entry.cacheTime + ( ( (Config.skinCacheHours * 60L) * 60L) * 1000L) );
	}
	
	/**
	 * Used for Skin Capabilities to see if they should redownload a cached skin in minuets
	 */
	public boolean hasExpiredFast(SkinEntry entry)
	{
		return System.currentTimeMillis() >= (entry.cacheTime + ( (Config.skinCacheFast * 60L) * 1000L) );
	}
	
	public Thread refreshThread = null;
	public volatile boolean running;
	public void start()
	{
		if(this.refreshThread == null)
		{
			this.running = true;
			this.refreshThread = new Thread(()-> 
			{
				while(running)
				{
					//copy the que because downloading while the que is locked will lag the main thread
					Map<String, PairObj<SkinEntry, Boolean>> que = new HashMap();
					boolean flag = false;
					synchronized (this.refreshque)
					{
						flag = this.refreshque.isEmpty();
						que.putAll(this.refreshque);
					}
					
					if(this.isMojangOnline())
					{
						for(Map.Entry<String, PairObj<SkinEntry, Boolean>> m : que.entrySet())
						{
							if(!this.running)
								break;
							String user = m.getKey();
							PairObj<SkinEntry, Boolean> pair = m.getValue();
							SkinEntry current = pair.obj1;
							boolean selected = pair.obj2;
							
							SkinEntry dl = this.downloadSkin(user, current);
							SkinEntry dl2 = selected ? SkinEvent.Capability.fire(dl, user) : dl;
							if(!dl.isEmpty)
							{
								this.removeQue(user);
								Minecraft.getMinecraft().addScheduledTask(()->
								{
									this.skins.put(user, dl);
									if(selected)
										this.refreshSelected(dl2);
									this.save();
								});
							}
							else if(selected)
							{
								Minecraft.getMinecraft().addScheduledTask(()->
								{
									this.refreshSelected(dl2);
									this.save();
								});
							}
						}
					}
					if(this.running)
						JavaUtil.sleep(2500);
				}
				this.refreshThread = null;
			});
			this.refreshThread.setPriority(4);//set's it below normal thread priority so it doesn't interfere with the game
			this.refreshThread.start();
		}
	}
	
	/**
	 * Call this when SkinEvent.Capability Fires
	 */
	public SkinEntry getOrDownload(SkinEntry skin, String user, boolean selected)
	{
		user = user.toLowerCase();
		
		//Don't redownload or fetched outdated cached skin if we know that the downloaded skin is the skin we are trying to use
		if(user.isEmpty() || skin.user.equals(user) )
			return skin;
		
		SkinEntry cached = this.getSkinEntry(user);
		boolean shouldDL = cached.isEmpty || this.hasExpired(cached) || selected && this.hasExpiredFast(cached);
		if(shouldDL)
		{
			SkinEntry dl = this.downloadSkin(user, EMPTY);
			this.skins.put(user, dl);
			return dl;
		}
		return cached;
	}

	public volatile boolean playerdb = false;
	public SkinEntry downloadSkin(String user, SkinEntry current)
	{
		//is player db online
		if(playerdb || JavaUtil.isOnline("playerdb.co"))
			playerdb = true;
		
		String uuid = current.isEmpty || this.hasExpired(current) ? getUUID(user) : current.uuid;//grab the cached uuid when possible
		
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
			return EMPTY;
		}
		
		String base64payload = json.getJSONArray("properties").getJSONObject(0).getString("value");
		return SkinEntry.fromPayload(uuid, user, base64payload);
	}

	/**
	 * gets the real uuid for the player using playerdb when possible to prevent mojang's 429 error
	 */
	public String getUUID(String user)
	{
		JSONObject dbjson = playerdb ? getPlayerDBJSON(user) : null;
		return dbjson != null && dbjson.containsKey("id") ? dbjson.getString("id").replace("-", "") : getMojangUUID(user);
	}

	/**
	 * unlike mojang will not return an error code 429 (too many requests) when obtaining the uuid of the player
	 */
	public JSONObject getPlayerDBJSON(String username) 
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
		} 
		catch (UnknownHostException e)
		{
			playerdb = false;
			System.err.println("playerdb is offline!");
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
			if (con instanceof HttpURLConnection)
				((HttpURLConnection) con).disconnect();
		}
	}
	
	public JSONObject getMojangProfile(String uuid) 
	{
		BufferedReader stream = null;
		try 
		{
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			stream = new BufferedReader(new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			return json;
		} 
		catch (UnknownHostException e) {
			this.isOnline = false;
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return null;
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
		catch (UnknownHostException e) {
			this.isOnline = false;
		}
		catch (IOException e)
		{
			//if error code isn't 429 aka too many requests assume it's a bad username
			if(JavaUtil.isOnline("api.mojang.com"))
			{
				int response = getCode(con);
				//If too many requests lock the refresh thread
				if(response == 429)
				{
					this.lockedUUID = true;
				}
				//           Not Found      Bad Request       Forbidden
				else if(response == 404 || response == 400 || response == 403)
				{
					this.removeQue(username);
				}
				else
					System.err.println("Unexpected HTTPS Error Code:" + response);
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
		if(!Config.skinCache)
			return;
		System.out.println("Loading Skin Cache");
		long ms = System.currentTimeMillis();
		INSTANCE = new SkinCache();
		INSTANCE.load();
		INSTANCE.save();
		INSTANCE.start();
		long ms2 = System.currentTimeMillis();
		INSTANCE.refreshClientSkin();
		JavaUtil.printTime(ms2, "Skin Fetch From Cache took:");
		JavaUtil.printTime(ms, "Skin Cache Took:");
	}
	
	public static class EvilProperty extends Property
	{
		public EvilProperty(String name, String value)
		{
			super(name, value, null);
		}
	}

	public static SkinCache getInstance() 
	{
		if(SkinCache.INSTANCE == null)
			SkinCache.init();
		
		return SkinCache.INSTANCE;
	}

	public static String getEncode(SkinEntry s)
	{
		if(s.isEmpty)
			return s.encode();
		
		//sync skins with client
		s = s.copy();
		Session session = Minecraft.getMinecraft().getSession();
		s.user = session.getUsername();
		
		//encode the skin into a usable payload
		return s.encode();
	}

}
