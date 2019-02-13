package com.evilnotch.menulib;

import java.io.File;

import com.evilnotch.menulib.compat.ProxyMod;
import com.evilnotch.menulib.eventhandler.GuiEventHandler;
import com.evilnotch.menulib.eventhandler.MusicEventHandler;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MenuLib.MODID,name = MenuLib.NAME, version = MenuLib.VERSION, clientSideOnly = true, dependencies = "required-after:evilnotchlib;after:custommainmenu")
public class MenuLib {
	
	public static final String MODID = "menulib";
	public static final String NAME = "Menu Lib";
	public static final String VERSION = "1.0";
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{	
		ConfigMenu.loadMenuLib(event.getSuggestedConfigurationFile());
		registerMenus();
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
		MinecraftForge.EVENT_BUS.register(new MusicEventHandler());
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		MenuRegistry.init();
	}
	
	private static void registerMenus() 
	{
		ProxyMod.isModsLoaded();
		
		if(!ProxyMod.cmm)
		{
			MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
		}
		
		ProxyMod.register();
	}

}
