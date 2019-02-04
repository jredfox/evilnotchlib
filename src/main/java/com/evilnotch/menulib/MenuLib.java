package com.evilnotch.menulib;

import com.evilnotch.menulib.compat.ProxyMod;
import com.evilnotch.menulib.compat.menu.MenuCMM;
import com.evilnotch.menulib.eventhandler.GuiEventHandler;
import com.evilnotch.menulib.menu.MenuRegistry;
import com.evilnotch.menulib.test.MainMenuAetherTest;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MenuLib.MODID,name = MenuLib.NAME, version = MenuLib.VERSION, clientSideOnly = true,dependencies = "after:custommainmenu")
public class MenuLib {
	
	public static final String MODID = "menulib";
	public static final String NAME = "Menu Lib";
	public static final String VERSION = "0.9";
	/**
	 * is cmm installed
	 */
	public static boolean cmm = false;
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		ProxyMod.isModsLoaded();
		ConfigMenu.loadMenuLib(event.getModConfigurationDirectory());
		registerMenus();
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		MenuRegistry.init();
	}
	
	private static void registerMenus() 
	{
		ProxyMod.register();
		if(!ProxyMod.cmm)
		{
			MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
		}
	}

}
