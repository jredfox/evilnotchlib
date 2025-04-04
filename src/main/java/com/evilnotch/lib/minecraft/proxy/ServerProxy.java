package com.evilnotch.lib.minecraft.proxy;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy {
	
	/**
	 * called before anything else
	 */
	public void proxyStart() {}
	public void preinit(FMLPreInitializationEvent e){}
	
	public void initMod() {}
	/**
	 * generate lan files and inject here
	 */
	public void postinit(){}
	/**
	 * Client only event
	 */
	public void handleUUIDChange(PacketUUID message) {}
	/**
	 * fix Minecraft#profileProperties and syncs it with forge
	 */
	public void fixMcProfileProperties() {}
	
	
	public static void clearServerData()
	{
		TickRegistry.garbageCollectServer();	
		VanillaBugFixes.worlDir = null;
		VanillaBugFixes.playerDataDir = null;
		VanillaBugFixes.playerDataNames = null;
	}
	
	public void setFoodSaturationLevel(FoodStats fs, float saturationLevel)
	{
		fs.foodSaturationLevel = saturationLevel;
	}
	
	public void addScheduledTask(Runnable run) 
	{
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(run);
	}
	
	public int getServerPort(MinecraftServer server	)
	{
		return server != null ? server.getServerPort() : 0;
	}
	
	public void bindTexture(ResourceLocation steve) {}
	public void deleteTexture(ResourceLocation r) {}
	public void noSkin(Map map, Object skinAvailableCallback) {}
	public void noSkin(Object callback, Type typeIn, ResourceLocation skinLoc, Object skinTexture) {}
	public void noSkin(int responseCode, Object callback, Type typeIn, ResourceLocation skinLoc, Object skinTexture) {}
	public void skinElytra(Object skinManager, Map map, Object skinAvailableCallback) {}
	/**
	 * @return Minecraft#running on Client and true on Dedicated Server
	 */
	public boolean running() {return true;}
	public String getUsername() {return null;}
	public boolean isClient() {return false;}
	public boolean isClient(EntityPlayer p) { return false; }
	public void d(float height) {}
	public boolean isClient(UUID id) {return false;}
	public PropertyMap getProperties() {return new PropertyMap();}
	public void loadComplete() {}
	
	public void dlHook(HttpURLConnection con) 
	{
		if(con.getURL() == null) 
			return;
		
		String host = con.getURL().getHost();
		
		//only apply User-Agent for non Mojang Skin Domains
		if(!host.endsWith(".minecraft.net") && !host.endsWith(".mojang.com"))
			con.setRequestProperty("User-Agent", "Mozilla");
	}
	
	
}
