package com.evilnotch.lib.minecraft.proxy;

import com.evilnotch.lib.main.eventhandler.TickServerEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy {
	
	/**
	 * called before anything else
	 */
	public void proxyStart() {}
	public void preinit(FMLPreInitializationEvent e){}
	
	public void initMod() 
	{
		MinecraftForge.EVENT_BUS.register(new TickServerEvent());
	}
	/**
	 * generate lan files and inject here
	 */
	public void postinit(){}
	
}
