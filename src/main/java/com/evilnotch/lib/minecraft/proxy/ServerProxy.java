package com.evilnotch.lib.minecraft.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy {
	
	public void preinit(FMLPreInitializationEvent e){}
	
	/**
	 * generate lan files and inject here
	 */
	public void postinit(){}
	
	/**
	 * called before anything else
	 */
	public void proxyStart() {}
	public void initMod() {}

	//client only methods
	public void lang() {}
	public void jsonGen() throws Exception {}

}
