package com.EvilNotch.lib.minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.EvilNotch.lib.minecraft.content.SkinData;
import com.EvilNotch.lib.minecraft.events.SkinFixEvent;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SkinUpdater {
	
	public static List<SkinData> data = new ArrayList<SkinData>();
	
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
	
	private static void updateSkinPackets(EntityPlayerMP p) {
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
	      
	      for (EntityPlayer pOnlines : p.getServerWorld().playerEntities)
	      {
	    	  EntityPlayerMP pOnline = (EntityPlayerMP)pOnlines;
	    	  NetHandlerPlayServer con = pOnline.connection;
	        if (pOnline.equals(p))
	        {
	          con.sendPacket(removeInfo);
	          con.sendPacket(addInfo);
	          con.sendPacket(respawn);
	          
	          p.connection.setPlayerLocation(p.posX, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
	          p.interactionManager.setWorld(p.getServerWorld());
	          p.connection.sendPacket(new SPacketPlayerAbilities(p.capabilities));    
	        }
	        else 
	        {
	          con.sendPacket(removeEntity);
	          con.sendPacket(removeInfo);
	          con.sendPacket(addInfo);
	          con.sendPacket(addNamed);
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
