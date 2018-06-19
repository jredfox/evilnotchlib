package com.EvilNotch.lib.minecraft;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.EvilNotch.lanessentials.Reference;
import com.EvilNotch.lanessentials.capabilities.CapCape;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.main.eventhandlers.LibEvents;
import com.EvilNotch.lib.minecraft.content.SkinData;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityReg;
import com.EvilNotch.lib.minecraft.events.CapeFixEvent;
import com.EvilNotch.lib.minecraft.events.SkinFixEvent;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.number.IntObj;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class SkinUpdater {
	
	public static List<SkinData> data = new ArrayList<SkinData>();
	public static HashMap<String,String> uuids = new HashMap();
	
	public static void updateSkin(String username,EntityPlayerMP player,boolean packets,boolean alexURL) throws WrongUsageException
	{
		updateSkin(username,player.getGameProfile(),player,alexURL);
		if(packets)
		{
			SkinUpdater.updateSkinPackets(player);
		}
	}
	public static void updateSkin(String username,GameProfile profile,EntityPlayerMP sender,boolean alexURL) throws WrongUsageException
	{
		updateSkin(username,profile.getProperties(),sender,alexURL);
	}
	public static void updateSkin(String username,PropertyMap pm,EntityPlayerMP sender,boolean alexURL) throws WrongUsageException
	{
		if(SkinUpdater.data.size() > Config.maxSkinCache)
		{
			SkinUpdater.data.clear();
		}
		//direct skin to url support
		String url = username;
		if(JavaUtil.isURL(url))
		{
			PropertyMap map = sender.getGameProfile().getProperties();
			ArrayList<Property> props = JavaUtil.toArray(map.get("textures"));
			String encoded = null;
			if(props.size() == 0)
			{
				//if property not set create one from scratch
				JSONObject json = new JSONObject();
				json.put("timestamp", System.currentTimeMillis());
				json.put("profileId", sender.getUniqueID().toString());//uuid of sender
				json.put("profileName", sender.getName());
				json.put("signatureRequired", false);
				
				JSONObject textures = new JSONObject();
				JSONObject jk = new JSONObject();
				textures.put("SKIN", jk);
				//no need for else statment since this json get's manually filled here
				if(alexURL)
				{
					JSONObject meta = new JSONObject();
					meta.put("model", "slim");
					jk.put("metadata", meta);
				}
				jk.put("url", url);
				json.put("textures", textures);
				updateCape(sender,textures,true);
				
				byte[] bytes = Base64.encodeBase64(json.toJSONString().getBytes());
				encoded = new String(bytes,StandardCharsets.UTF_8);
			}
			for(Property p : props)
			{
				JSONObject json = (JSONObject) JavaUtil.toJsonFrom64(p.getValue());
				JSONObject textures = (JSONObject) json.get("textures");
				JSONObject s = (JSONObject) textures.get("SKIN");
				if(s == null)
				{
					s = new JSONObject();
					textures.put("SKIN", s);
				}
				if(alexURL)
				{
					JSONObject meta = new JSONObject();
					meta.put("model", "slim");
					s.put("metadata", meta);
				}
				else
				{
					JSONObject meta = (JSONObject) s.get("metadata");
					if(meta != null)
					{
						meta.remove("model");
						if(meta.isEmpty())
						{
							s.remove("metadata");
						}
					}
				}
				s.put("url", url);
				updateCape(sender,textures,true);
				json.put("signatureRequired", false);
				byte[] bytes = Base64.encodeBase64(json.toJSONString().getBytes());
				encoded = new String(bytes,StandardCharsets.UTF_8);
			}
			map.removeAll("textures");
			map.put("textures", new TestProps("textures",encoded,""));
			return;
		}
		username = username.toLowerCase();
		SkinData skin = getSkinData(username);
		String value = skin.value;

		//cape compatibility
		JSONObject json = skin.getJSON();
		JSONObject textures = (JSONObject) json.get("textures");
		boolean recompile = false;
		
		if(sender != null)
		{
			recompile = updateCape(sender,textures,false);
		}
		if(!json.containsKey("signatureRequired") || !((Boolean)json.get("signatureRequired")))
		{
			json.put("signatureRequired", false);
			recompile = true;
		}
		if(recompile)
		{
			value = Base64.encodeBase64String(json.toJSONString().getBytes());
		}
		pm.removeAll("textures");
		pm.put("textures", new TestProps("textures", value,skin.signature));
	}

	/**
	 * url is used as a boolean for !hasAccountName to detect if user has cape same with event force update
	 */
	public static boolean updateCape(EntityPlayer sender,JSONObject textures,boolean url) 
	{
		boolean recompile = false;
		if(sender != null)
		{
			CapeFixEvent cape = new CapeFixEvent(sender);
			MinecraftForge.EVENT_BUS.post(cape);

			if(url || cape.overrideCape)
			{
				if(textures.containsKey("CAPE"))
				{
					textures.remove("CAPE");
					recompile = true;
				}
			}
			boolean noCape = !textures.containsKey("CAPE");
			
			if(noCape && !cape.url.equals(""))
			{
				JSONObject jcape = new JSONObject();
				jcape.put("url", cape.url);
				textures.put("CAPE", jcape);
				recompile = true;
			}
		}
		return recompile;
	}
	public static SkinData getSkinData(String username) throws WrongUsageException 
	{
		SkinData skin = getSkin(username);
		boolean cache = skin != null;
		long time = System.currentTimeMillis();
		String uuid = cache ? skin.uuid : getUUID(username);
		if(uuid == null)
		{
			System.out.println("non mojang skin detected returning:" + username);
			throw new WrongUsageException("non mojang skin detected:" + username,new Object[0]);
		}
		JavaUtil.printTime(time, "UUIDMS:");
		SkinData props = cache ? skin : getDev(username,uuid);

		if(props == null)
		{
			System.out.println("couldn't grab skin for:" + username);
			throw new WrongUsageException("couldn't grab skin for:" + username,new Object[0]);
		}
		if(!cache)
			data.add(props);
		return props;
	}
	private static SkinData getDev(String username, String uuid) throws WrongUsageException 
	{
		String[] args = getProperties(uuid);
		if(args == null || args.length != 2)
		{
			return null;
		}
		return new SkinData(uuid,args,username);
	}
	public static SkinData getSkin(String name) {
		for(SkinData s : data)
			if(s.username.equals(name))
				return s;
		return null;
	}
	public static String getUUID(String username)
	{
		try
		{
			String cached = uuids.get(username);
			if(cached != null)
				return cached;
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			InputStream urlStream = url.openStream();
			JSONParser parser = new JSONParser();
			InputStreamReader stream = new InputStreamReader(urlStream,"UTF-8");
			JSONObject json = (JSONObject) parser.parse(stream);
			String id = (String) json.get("id");
			uuids.put(username,id);
			stream.close();
			urlStream.close();
			return id;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	/**
	 * add support for
	 * for older versions for when and if they show up unkown if it's suppose to be uuid or username
	   http://skins.minecraft.net/MinecraftSkins/
	   http://skins.minecraft.net/MinecraftCloaks/
	 */
	@Deprecated
	public static String[] getProperties(String uuid)
	{
		try
		{
			String site = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
			URL url = new URL(site);
			JSONParser parser = new JSONParser();
			InputStream urlStream = url.openStream();
			InputStreamReader stream = new InputStreamReader(urlStream,"UTF-8");
			JSONObject json = (JSONObject) parser.parse(stream);
			JSONArray arr = (JSONArray) json.get("properties");
			JSONObject ajson = (JSONObject) arr.get(0);
			
			String value = (String) ajson.get("value");
			String signature = (String) ajson.get("signature");
			stream.close();
			urlStream.close();
			return new String[]{value,signature};
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			/*
			else
			{
				System.out.println("using backup crafatar");
				String site = "https://crafatar.com/skins/" + uuid;
				
				JSONObject json = new JSONObject();
				json.put("timestamp", System.currentTimeMillis());
				json.put("profileId", uuid);
				json.put("profileName", sender);
				json.put("signatureRequired", false);
				
				JSONObject textures = new JSONObject();
				JSONObject jk = new JSONObject();
				JSONObject jc = new JSONObject();
				jk.put("url", site);
				textures.put("SKIN", jk);
				json.put("textures", textures);
				try
				{
					URL cape = new URL("https://crafatar.com/capes/" + uuid);
					URLConnection obj = cape.openConnection();
					jc.put("url", "https://crafatar.com/capes/" + uuid);
					textures.put("CAPE", jc);
					updateCape(sender,uuid,textures);
				}
				catch(Exception e)
				{
					System.out.println("skin has no cape:" + "https://crafatar.com/capes/" + uuid);
				}
				
				byte[] bytes = Base64.encodeBase64(json.toJSONString().getBytes());
				String encoded = new String(bytes,StandardCharsets.UTF_8);
				return new String[] {encoded,""};
			}
			 */
		}
		return null;
	}
	
    public static void updateSkinPackets(EntityPlayerMP p)
    {
		SPacketPlayerListItem removeInfo;
		SPacketDestroyEntities removeEntity;
		SPacketSpawnPlayer addNamed;
	    SPacketPlayerListItem addInfo;
	    SPacketRespawn respawn;
	    try
	    {
	      int entId = p.getEntityId();
	      removeInfo = new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER,p);
	      removeEntity = new SPacketDestroyEntities(new int[] { entId });
	      addNamed = new SPacketSpawnPlayer(p);
	      addInfo = new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER,p);
	      respawn = new SPacketRespawn(p.dimension, p.getServerWorld().getDifficulty(), p.getServerWorld().getWorldType(), p.getServer().getGameType());
	      
	     for (EntityPlayer pOnlines : p.mcServer.getPlayerList().getPlayers())
	     {
	        EntityPlayerMP pOnline = (EntityPlayerMP)pOnlines;
	        NetHandlerPlayServer con = pOnline.connection;
	        if (pOnline.equals(p))
	        {
		       con.sendPacket(removeInfo);
		       con.sendPacket(respawn);
		       con.sendPacket(addInfo);
			       
	      	  //gamemode packet
	      	   EntityUtil.setGameTypeSafley(p,p.interactionManager.getGameType());
	      	   p.mcServer.getPlayerList().updatePermissionLevel(p);
	      	   p.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(p, (WorldServer) p.world);
	      	   p.world.updateAllPlayersSleepingFlag();
	      	  
	           //prevent the moved too quickly message
	      	   p.setRotationYawHead(p.rotationYawHead);
	           p.connection.setPlayerLocation(p.posX, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
	           //trigger update exp
	           p.connection.sendPacket(new SPacketSetExperience(p.experience, p.experienceTotal, p.experienceLevel));

	           //send the current inventory - otherwise player would have an empty inventory
	           p.sendContainerToPlayer(p.inventoryContainer);
	           p.setPlayerHealthUpdated();
	           p.setPrimaryHand(p.getPrimaryHand());
	           p.connection.sendPacket(new SPacketHeldItemChange(p.inventory.currentItem));

	           InventoryPlayer inventory = p.inventory;
	           p.setHeldItem(EnumHand.MAIN_HAND, p.getHeldItemMainhand());
	           p.setHeldItem(EnumHand.OFF_HAND, p.getHeldItemOffhand());

	           //health && food
	           p.setHealth(p.getHealth());
	           FoodStats fs = p.getFoodStats();
	           fs.setFoodLevel(fs.getFoodLevel());
	           fs.setFoodSaturationLevel(fs.getSaturationLevel());
	           p.interactionManager.setWorld(p.getServerWorld()); 
	          
	           con.sendPacket(new SPacketSpawnPosition(p.getPosition()));
	           
	           boolean end = true;
	           p.copyFrom(p, end);
	           net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerRespawnEvent(p, end);
	         }
	         else 
	         {
	           con.sendPacket(removeEntity);
	           con.sendPacket(removeInfo);
	           con.sendPacket(addInfo);
	           con.sendPacket(addNamed);
	          
	           //hide player
		       pOnline.getServerWorld().getEntityTracker().removePlayerFromTrackers(p);
		       pOnline.getServerWorld().getEntityTracker().untrack(p);
		       //show player
		       pOnline.getServerWorld().getEntityTracker().track(p);
	         }
	      }
	    }
	    catch (Exception localException) {}
    }
    
	public static String getCapeURL(SkinData skin,String username) throws WrongUsageException 
	{
		if(skin == null)
			throw new WrongUsageException("unable to fetch cape url for:" + username,new Object[0]);
		JSONObject json = skin.getJSON();
		if(!json.containsKey("textures"))
			throw new WrongUsageException("unable to fetch cape url for:" + username,new Object[0]);
		JSONObject textures = (JSONObject) json.get("textures");
		if(!textures.containsKey("CAPE"))
			throw new WrongUsageException("unable to fetch cape url for:" + username,new Object[0]);
		JSONObject cape = (JSONObject) textures.get("CAPE");
		String url = (String)cape.get("url");
		if(url == null)
			throw new WrongUsageException("unable to fetch cape url for:" + username,new Object[0]);
		return url;
	}
	public static String getSkinURL(SkinData skin,String username) throws WrongUsageException 
	{
		if(skin == null)
			throw new WrongUsageException("unable to fetch skin url for1: " + username,new Object[0]);
		JSONObject json = skin.getJSON();
		if(!json.containsKey("textures"))
			throw new WrongUsageException("unable to fetch skin url for2: " + username,new Object[0]);
		JSONObject textures = (JSONObject) json.get("textures");
		if(!textures.containsKey("SKIN"))
			throw new WrongUsageException("unable to fetch skin url as it is blank: " + username,new Object[0]);
		JSONObject jskin = (JSONObject) textures.get("SKIN");
		String url = (String)jskin.get("url");
		if(url == null)
			throw new WrongUsageException("url is null report this to mod author scrwing up skin data: " + username,new Object[0]);
		return url;
	}

	/**
	 * returns whether or not it succeeded
	 */
	public static boolean fireSkinEvent(EntityPlayer p,boolean usePackets) 
	{
		SkinFixEvent event = new SkinFixEvent(p);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.newSkin != null)
		{
			try
			{
				//only update if forceUpdate or names are not right
				System.out.println("UPDATING SKIN:" + p.getName() + " > " + event.newSkin);
				SkinUpdater.updateSkin(event.newSkin, (EntityPlayerMP) p, usePackets,event.isAlexURL);
				return true;
			}
			catch (WrongUsageException e)
			{
				e.printStackTrace();
				boolean noSkin = e.getMessage().contains("couldn't grab skin for:");
				if(noSkin)
				{
					addSkinDelay(p,event.newSkin);
					if( ((EntityPlayerMP) p).connection != null) 
						EntityUtil.printChat(p, EnumChatFormatting.RED, "", "Couldn't grab skin for:" + EnumChatFormatting.AQUA + event.newSkin);
				}
			}
		}
		return false;
	}
	/**
	 * for when skin can't get fetched try again in 32 seconds
	 */
	public static void addSkinDelay(EntityPlayer p,String skin) 
	{
		if(!LibEvents.noSkins.containsKey(p.getName()) )//&& JavaUtil.isOnline(url))
		{
			//make sure user can connect before constantly trying to add it back
			if(JavaUtil.isOnline("api.mojang.com"))
				LibEvents.noSkins.put(p.getName(),new IntObj(0));
			else
				System.out.println("no internet on adding?");
		}
		else
		{
			if(JavaUtil.isOnline("api.mojang.com"))
				LibEvents.noSkins.get(p.getName()).integer = 0;
			else
				System.out.println("no internet on setting?");
		}
	}
	public static void removeUser(String name) 
	{
		 SkinUpdater.uuids.remove(name);
		 int index = 0;
		 for(SkinData d : data)
		 {
			 if(d.username.equals(name))
			 {
				 data.remove(index);
				 break;
			 }
			 index++;
		 }
	}
	public static void parseSkinCache() 
	{
		if(MainJava.skinCache.exists())
		{
			try
			{
				JSONParser parser = new JSONParser();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(MainJava.skinCache),StandardCharsets.UTF_8) );
				JSONArray arr = (JSONArray) parser.parse(reader);
				Iterator<JSONObject> it = arr.iterator();
				while(it.hasNext())
				{
					JSONObject obj = it.next();
					String name = (String)obj.get("name");
					String uuid = (String)obj.get("uuid");
					String signature = "";
					JSONObject valueJson = (JSONObject) obj.get("value");
					valueJson.put("signatureRequired", false);
					valueJson.put("timestamp", System.currentTimeMillis());
					String value =  new String(Base64.encodeBase64(valueJson.toJSONString().getBytes()),StandardCharsets.UTF_8);
					SkinData data = new SkinData(uuid,value,signature,name,valueJson);
					
					SkinUpdater.uuids.put(name,uuid);
					SkinUpdater.data.add(data);
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	public static void saveSkinCache() 
	{
		if(SkinUpdater.data.size() > Config.maxSkinCache)
		{
			SkinUpdater.data.clear();
		}
		JSONArray arr = new JSONArray();
		for(SkinData d : SkinUpdater.data)
		{
			JSONObject json = new JSONObject();
			json.put("name", d.username);
			json.put("uuid", d.uuid);
			JSONObject valueJson = d.getJSON();
			valueJson.put("signatureRequired", false);
			json.put("value", valueJson);
			arr.add(json);
		}
		if(!MainJava.skinCache.exists())
		{
			try 
			{
				MainJava.skinCache.createNewFile();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		JavaUtil.saveFileLines(JavaUtil.asArray(new String[]{arr.toJSONString()}), MainJava.skinCache, true);
	}
	public static void setCape(EntityPlayer player, String url,boolean packets) throws WrongUsageException 
	{
		ArrayList<Property> props = JavaUtil.toArray(player.getGameProfile().getProperties().get("textures"));
		if(props.size() == 0)
		{
			System.out.println("patching player textures:{}");
			PropertyMap map = player.getGameProfile().getProperties();
			map.removeAll("textures");
			
			JSONObject json = new JSONObject();
			json.put("timestamp", System.currentTimeMillis());
			json.put("profileId", player.getUniqueID().toString());//uuid of sender
			json.put("profileName", player.getName());
			json.put("signatureRequired", false);
			
			JSONObject textures = new JSONObject();
			json.put("textures", textures);
			byte[] bytes = Base64.encodeBase64(json.toJSONString().getBytes());
			String encoded = new String(bytes,StandardCharsets.UTF_8);
			map.put("textures", new TestProps("textures", encoded, "") );
			props = JavaUtil.toArray(player.getGameProfile().getProperties().get("textures"));
		}
		for(Property p : props)
		{
			String base64 = p.getValue();
			JSONObject obj = JavaUtil.toJsonFrom64(base64);
			if(!obj.containsKey("textures"))
				throw new WrongUsageException("player skin not set! set skin before setting a cape",new Object[0]);
			JSONObject obj2 = (JSONObject) obj.get("textures");
			JSONObject json = (JSONObject)obj2.get("CAPE");
			if(json == null)
			{
				json = new JSONObject();
				obj2.put("CAPE", json);
			}
			if(url.equals(""))
				obj2.remove("CAPE");//for when args == 0
			
			obj.put("signatureRequired", false);
			json.put("url",url);
			String str = obj.toJSONString();
			byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes());
			ReflectionUtil.setObject(p, new String(bytes,StandardCharsets.UTF_8), Property.class, "value");
		}
		CapCape cape = (CapCape) CapabilityReg.getCapabilityConatainer(player).getCapability(new ResourceLocation(Reference.MODID + ":" + "cape"));
		cape.url = url;	
		if(packets)
			SkinUpdater.updateSkinPackets((EntityPlayerMP) player);
	}

}
