package com.evilnotch.lib.minecraft.proxy;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.main.eventhandler.TickServerEvent;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;
import net.minecraftforge.common.MinecraftForge;
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
	
	
	public static void clearServerData()
	{
		TickRegistry.garbageCollectServer();	
		VanillaBugFixes.worlDir = null;
		VanillaBugFixes.playerDataDir = null;
		VanillaBugFixes.playerDataNames = null;
	}
	
	public void handleUUIDChange(PacketUUID message) 
	{
		
	}
	
}
