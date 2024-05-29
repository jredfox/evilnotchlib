package com.evilnotch.lib.minecraft.proxy;

import java.util.Map;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
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
	
	public void bindTexture(ResourceLocation steve) {}
	public void deleteTexture(ResourceLocation r) {}
	public void noSkin(Map map, Object skinAvailableCallback) {}
	public void noSkin(Object callback, Type typeIn, ResourceLocation skinLoc, Object skinTexture) {}
	public void noSkin(int responseCode, Object callback, Type typeIn, ResourceLocation skinLoc, Object skinTexture) {}
	

	
}
