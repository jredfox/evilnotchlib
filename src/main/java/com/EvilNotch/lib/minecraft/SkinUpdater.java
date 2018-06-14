package com.EvilNotch.lib.minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.SkinData;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityReg;
import com.EvilNotch.lib.minecraft.events.CapeFixEvent;
import com.EvilNotch.lib.minecraft.events.SkinFixEvent;
import com.EvilNotch.lib.util.JavaUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.realmsclient.dto.PlayerInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class SkinUpdater {
	
	public static List<SkinData> data = new ArrayList<SkinData>();
	public static HashMap<String,String> uuids = new HashMap();
	
	public static void updateSkin(String username,EntityPlayerMP player,boolean packets) throws WrongUsageException
	{
		updateSkin(username,player.getGameProfile(),player);
		if(packets)
		{
			SkinUpdater.updateSkinPackets(player);
		}
	}
	public static void updateSkin(String username,GameProfile profile,EntityPlayer sender) throws WrongUsageException
	{
		updateSkin(username,profile.getProperties(),sender);
	}
	public static void updateSkin(String username,PropertyMap pm,EntityPlayer sender) throws WrongUsageException
	{
		SkinData skin = getSkinData(username);
		String value = skin.value;

		//cape compatibility
		JSONObject json = skin.getJSON();
		JSONObject textures = (JSONObject) json.get("textures");
		boolean recompile = false;
		
		if(sender != null)
		{
			CapeFixEvent cape = new CapeFixEvent(sender);
			MinecraftForge.EVENT_BUS.post(cape);
			
			if(cape.overrideCape)
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
		if(!json.containsKey("signatureRequired") || !((Boolean)json.get("signatureRequired")) )
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
			System.out.println("couldn't lookup properties for:" + username);
			return null;
		}
		return new SkinData(uuid,args,username);
	}
	private static SkinData getSkin(String name) {
		for(SkinData s : data)
			if(s.username.toLowerCase().equals(name))
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
	      	   p.setGameType(p.interactionManager.getGameType());
	      	   p.mcServer.getPlayerList().updatePermissionLevel(p);
	      	   p.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(p, (WorldServer) p.world);
	      	   p.world.updateAllPlayersSleepingFlag();
	      	  
	           //prevent the moved too quickly message
	      	   p.setRotationYawHead(p.rotationYawHead);
	           p.connection.setPlayerLocation(p.posX, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
	           //trigger update exp
	           p.connection.sendPacket(new SPacketSetExperience(p.experience, p.experienceTotal, p.experienceLevel));

	           //triggers updateAbilities
	           p.sendPlayerAbilities();
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
		return url;
	}

	public static void fireSkinEvent(EntityPlayer p,boolean usePackets) 
	{
		SkinFixEvent event = new SkinFixEvent(p);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.newSkin != null)
		{
			try
			{
				EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
				//only update if forceUpdate or names are not right
//				if(forceUpdate || props.size() == 0 || props.get(0) == null || !props.get(0).hasSignature() || !player.getName().equals(event.newSkin) || capeFlag)
//				{
					System.out.println("UPDATING SKIN:" + player.getName() + " > " + event.newSkin);
					SkinUpdater.updateSkin(event.newSkin, (EntityPlayerMP) p, usePackets);
//				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
