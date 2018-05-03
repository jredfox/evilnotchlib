package com.EvilNotch.lib.main;

import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.util.registry.GeneralRegistry;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = MainJava.MODID,name = MainJava.NAME, version = MainJava.VERSION,acceptableRemoteVersions = "*")
public class MainJava {
	public static boolean isDeObfuscated = true;
	public static final String MODID =  "evilnotchlib";
	public static final String VERSION = "1.2.02";
	public static final String NAME = "Evil Notch Lib";

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
	  	MCPMappings.cacheMCPApplicable(e.getModConfigurationDirectory());
		isDeObfuscated = isDeObfucscated();
		Config.loadConfig(e.getModConfigurationDirectory());
	}
	@Mod.EventHandler
	public void commandRegister(FMLServerStartingEvent e)
	{
		for(ICommand cmd : GeneralRegistry.getCmdList())
			e.registerServerCommand(cmd);
	}

	public static boolean isDeObfucscated()
    {
    	try{
    		ReflectionHelper.findField(Block.class, MCPMappings.getFieldOb(Block.class,"blockHardness"));
    		return false;//return false since obfuscated field had no exceptions
    	}
    	catch(Exception e){return true;}
    }

}