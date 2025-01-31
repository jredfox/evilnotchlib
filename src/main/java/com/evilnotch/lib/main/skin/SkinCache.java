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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParser;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.capability.CapRegDefaultHandler;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketSkinChange;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class SkinCache {
	
	public static final SkinEntry EMPTY = new SkinEntry("", "", System.currentTimeMillis(), "", "", "", "");
	
	public Map<String, SkinEntry> skins = new ConcurrentHashMap(25);
	public Map<String, Boolean> refreshque = new HashMap<>();
	public Map<String, Boolean> offlineRefreshQue = new ConcurrentHashMap(5);//Boolean is Unused
	public File skinCacheLoc = new File(System.getProperty("user.dir"), "skinCacher.json");
	/**
	 * The Currently Selected SkinEntry. This isn't thread safe and should be accessed only on SkinEvent.User or SkinEvent.Capability
	 */
	public SkinEntry selected = EMPTY;
	/**
	 * Keep Track of the last original user's name
	 */
	private String lu;
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
		//Sanity Check
		if(arr == null)
		{
			skinCacheLoc.delete();
			arr = new JSONArray();
		}
		
		for(Object o : arr)
		{
			if(!(o instanceof JSONObject))
				continue;
			
			JSONObject j = (JSONObject) o;
			try
			{
				SkinEntry data = new SkinEntry(j);
				if(isOnline && hasExpired(data))
				{
					System.out.println("removing expired skin from cache:" + data.user + " uuid:" + data.uuid);
					continue;
				}
				skins.put(data.user, data);
			}
			catch(Exception e)
			{
				System.err.println("Error Parsing SkinEntry:" + j);
				if(Config.debug)
					e.printStackTrace();
			}
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
	
	public void saveSafely() 
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			this.save();
		});
	}
	
	/**
	 * gets the current cached skin will be empty if skins doesn't contain the user
	 */
	public SkinEntry getSkinEntry(String user)
	{
		user = user.toLowerCase();
		return skins.containsKey(user) ? skins.get(user) : EMPTY;
	}

	/**
	 * returns cached skin if it exists and adds the skin to the refresh cache
	 */
	public SkinEntry refresh(String user, boolean select)
	{
		SkinEntry current = this.getSkinEntry(user);
		//Download the offline SkinEntry from the cache if applicable
		if(select && !this.isMojangOnline())
			this.offlineRefreshQue.put(user, true);
		this.addQue(user, select);
		return current;
	}
	
	/**
	 * returns cached skin if it exists and adds the skin to the refresh cache
	 */
	public SkinEntry refreshClientSkin() 
	{
		return this.refresh(Minecraft.getMinecraft().getSession().getProfile().getName(), true);
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
	
	public void addQue(String user, boolean select)
	{
		synchronized (this.refreshque)
		{
			this.refreshque.put(user.toLowerCase(), select);
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
				while(running && Minecraft.getMinecraft().running)
				{
					//copy the que because downloading while the que is locked will lag the main thread
					Map<String, Boolean> que = new HashMap();
					synchronized (this.refreshque)
					{
						que.putAll(this.refreshque);
					}
					
					if(this.isMojangOnline())
					{
						if(!this.offlineRefreshQue.isEmpty())
							this.offlineRefreshQue.clear();
						
						if(!que.isEmpty())
						{
							for(Map.Entry<String, Boolean> m : que.entrySet())
							{
								if(!this.running || !Minecraft.getMinecraft().running)
									break;
								
								boolean selected = m.getValue();
								String user_org = m.getKey();
								this.lu = user_org;
								String user = selected ? SkinEvent.User.fire(user_org) : user_org;
								SkinEntry cached = this.getSkinEntry(user);
	
								SkinEntry dl = this.downloadSkin(user, cached);
								dl = dl.isEmpty ? cached : dl;//if dl fails rely on the cache
								SkinEntry dl2 = selected ? SkinEvent.Capability.fire(dl) : dl;
								
								if(!dl.isEmpty)
								{
									this.skins.put(user, dl);
									this.removeQue(user_org);
								}
								
								Minecraft.getMinecraft().addScheduledTask(()->
								{
									if(selected)
										this.refreshSelected(dl2);
								});
							}
							this.saveSafely();
						}
					}
					//Since the offline cache will take a few MS compared to online it's safe to fully lock the offlineRefreshQue map till it completes it's cycle
					else if(!this.offlineRefreshQue.isEmpty())
					{
						Iterator<String> i = this.offlineRefreshQue.keySet().iterator();
						while(i.hasNext())
						{
							if(!this.running || !Minecraft.getMinecraft().running)
								break;
							
							String user = i.next();
							String u = SkinEvent.User.fire(user);
							SkinEntry dl = this.getSkinEntry(u);
							SkinEntry dl2 = SkinEvent.Capability.fire(dl);
							
							Minecraft.getMinecraft().addScheduledTask(()->
							{
								this.refreshSelected(dl2);
							});
							
							i.remove();
						}
						this.saveSafely();
					}
					if(this.running)
						JavaUtil.sleep(Config.skinCacheMs);
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
	public SkinEntry getOrDownload(SkinEntry skin, String user)
	{
		user = user.toLowerCase();
		
		//Don't redownload or fetched outdated cached skin if we know that the downloaded skin is the skin we are trying to use
		if(user.isEmpty() || skin.user.equals(user) )
			return skin;
		
		SkinEntry cached = this.getSkinEntry(user);
		boolean shouldDL = cached.isEmpty || this.isOnline && this.hasExpiredFast(cached);
		if(shouldDL)
		{
			SkinEntry dl = this.downloadSkin(user, EMPTY);
			if(!dl.isEmpty)
			{
				this.skins.put(user, dl);
				return dl;
			}
		}
		return cached;
	}

	public volatile boolean playerdb = false;
	public SkinEntry downloadSkin(String user, SkinEntry current)
	{
		//is player db online
		if(!playerdb)
			playerdb = JavaUtil.isOnline("playerdb.co");
		
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
		catch (UnknownHostException | java.net.NoRouteToHostException e)
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
		catch (UnknownHostException | java.net.NoRouteToHostException e) {
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
		catch (UnknownHostException | java.net.NoRouteToHostException e) {
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
					this.removeQue(this.lu);
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
			catch(UnknownHostException | java.net.NoRouteToHostException uh)
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
		long ms = System.currentTimeMillis();
		INSTANCE = new SkinCache();
		INSTANCE.load();
		INSTANCE.save();
		INSTANCE.start();
		INSTANCE.refreshClientSkin();
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
		return s.encode();
	}

	public static String getEncode(PropertyMap map) 
	{
		if(map.isEmpty())
			return null;
		Property p = ((Property)JavaUtil.getFirst(map.get("textures")));
		return p == null ? null : p.getValue();
	}
	
	/**
	 * Safe Method for Setting the SkinData in case the SkinData is null or empty
	 */
	public static void setSkin(PropertyMap props, String skindata) 
	{
		if(!SkinCache.isSkinEmpty(skindata))
			setEncode(props, skindata);
	}
	
	public static void setEncode(PropertyMap props, String skindata) 
	{
		props.removeAll("textures");
		props.put("textures", new EvilProperty("textures", skindata));
	}
	
	public static boolean isSkinEmpty(String payload) 
	{
		return payload == null || payload.isEmpty();
	}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation fileSteve = new ResourceLocation("minecraft:skins/$steve");
	private static final ResourceLocation fileAlex = new ResourceLocation("minecraft:skins/$alex");
	public static ResourceLocation patchSkinResource(ResourceLocation resource) 
	{
		if(resource.equals(fileSteve))
		{
			MainJava.proxy.bindTexture(PlayerUtil.STEVE);//enforce steve is loaded so SkinManager doesn't try and download the skin
			return PlayerUtil.STEVE;
		}
		else if(resource.equals(fileAlex))
		{
			MainJava.proxy.bindTexture(PlayerUtil.ALEX);//enforce alex is loaded so SkinManager doesn't try and download the skin
			return PlayerUtil.ALEX;
		}
		return resource;
	}

}