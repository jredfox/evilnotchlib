package com.EvilNotch.lib.minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.content.SkinData;
import com.EvilNotch.lib.minecraft.events.SkinFixEvent;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.realmsclient.dto.PlayerInfo;

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
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class SkinUpdater {
	
	public static List<SkinData> data = new ArrayList<SkinData>();
	private static ArrayList<UUID> hiddenPlayers = new ArrayList();
	
	public static void updateSkin(String username,EntityPlayerMP player,boolean packets) throws WrongUsageException
	{
		PropertyMap pm = player.getGameProfile().getProperties();
		SkinData skin = getSkin(username);
		boolean cache = skin != null;
		String uuid = cache ? skin.uuid : getUUID(username);
		if(uuid == null)
		{
			System.out.println("non mojang skin detected returning:" + username);
			throw new WrongUsageException("non mojang skin detected:" + username,new Object[0]);
		}
		SkinData props = cache ? skin : new SkinData(uuid,getProperties(uuid),username);

		if(props == null)
		{
			System.out.println("couldn't grab skin for:" + username);
			throw new WrongUsageException("couldn't grab skin for:" + username,new Object[0]);
		}
		pm.removeAll("textures");
		pm.put("textures", new Property("textures", props.value,props.signature));
		if(!cache)
			data.add(props);
		if(packets)
		{
			SkinUpdater.updateSkinPackets(player);
		}
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
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			InputStream urlStream = url.openStream();
			JSONParser parser = new JSONParser();
			InputStreamReader stream = new InputStreamReader(urlStream,"UTF-8");
			JSONObject json = (JSONObject) parser.parse(stream);
			String id = (String) json.get("id");
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
	          net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerRespawnEvent(p, true);
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

	public static void fireSkinEvent(EntityPlayer p,boolean usePackets) 
	{
		SkinFixEvent event = new SkinFixEvent(p);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.newSkin != null)
		{
			try 
			{
				SkinUpdater.updateSkin(event.newSkin, (EntityPlayerMP) p, usePackets);
			} 
			catch (WrongUsageException e) 
			{
				e.printStackTrace();
			}
		}
	}

}
