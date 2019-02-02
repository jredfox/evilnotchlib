package com.evilnotch.lib.minecraft.proxy;

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
	
}
