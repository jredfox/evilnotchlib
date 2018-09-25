package com.evilnotch.menulib;

import java.io.File;
import java.util.List;

import com.evilnotch.menulib.eventhandler.GuiEventHandler;
import com.evilnotch.menulib.menu.MenuRegistry;
import com.evilnotch.menulib.test.MainMenuAetherTest;
import com.evilnotch.menulib.test.MenuCMM;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MenuLib.MODID,name = MenuLib.NAME, version = MenuLib.VERSION, clientSideOnly = true,dependencies = "after:custommainmenu")
public class MenuLib {
	
	public static final String MODID = "menulib";
	public static final String NAME = "Menu Lib";
	public static final String VERSION = "0.9";
	/**
	 * is cmm installed yes(true) or no(false)?
	 */
	public static boolean cmm = false;
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		ConfigMenu.loadMenuLib(event.getModConfigurationDirectory());
		cmm =Loader.isModLoaded("custommainmenu");
		//don't add vanilla menu to list if cmm is installed
		if(!cmm)
			MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
		else
		{
			MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
			MenuRegistry.registerIMenu(new MenuCMM());
		}
		MenuRegistry.registerGuiMenu(MainMenuAetherTest.class, new ResourceLocation("menulib:aether_test"));
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		MenuRegistry.loadInit();
	}

}
