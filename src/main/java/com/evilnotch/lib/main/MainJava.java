package com.evilnotch.lib.main;

import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.proxy.ServerProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = MainJava.MODID, name = MainJava.NAME, version = MainJava.VERSION, dependencies = "before:foamfix")
public class MainJava {
	
	public static final String MODID =  "evilnotchlib";
	public static final String VERSION = "1.2.4";//SNAPSHOT_STAGE
	public static final String NAME = "Evil Notch Lib";
	public static final String max_version = "4.0.0.0.0";//allows for 5 places in lib version
	@SidedProxy(clientSide = "com.evilnotch.lib.minecraft.proxy.ClientProxy", serverSide = "com.evilnotch.lib.minecraft.proxy.ServerProxy")
	public static ServerProxy proxy;
	
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		LoaderMain.loadpreinit(e, this.getClass().getClassLoader());
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		LoaderMain.loadInit(e);
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
		LoaderMain.loadPostInit(e);
	}
	
	@Mod.EventHandler
	public void complete(FMLLoadCompleteEvent e)
	{
		LoaderMain.loadComplete(e, this.getClass().getClassLoader());
	}
	
	/**
	 * register all commands
	 */
	@Mod.EventHandler
	public void serverStart(FMLServerStartingEvent e)
	{
		LoaderMain.serverStart(e);
	}
	
	/**
	 * clear various data from memory
	 */
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{	
		LoaderMain.serverStopping();
	}
	
}
