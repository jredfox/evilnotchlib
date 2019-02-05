package com.evilnotch.menulib.compat;

import java.lang.reflect.Method;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.menulib.ConfigMenu;
import com.evilnotch.menulib.compat.menu.MenuCMM;
import com.evilnotch.menulib.menu.MenuRegistry;
import com.evilnotch.menulib.test.MainMenuAetherTest;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class ProxyMod {
	
	public static boolean cmm;
	public static boolean thebetweenlands;
	
	public static Class tbl_musicHandler = null;
	public static Object tbl_instance = null;
	
	public static void isModsLoaded()
	{
		cmm = Loader.isModLoaded("custommainmenu");
		thebetweenlands = Loader.isModLoaded("thebetweenlands");
		cacheData();
	}

	public static void register()
	{	
		if(cmm)
			MenuRegistry.registerIMenu(new MenuCMM());
		if(thebetweenlands)
		{
			ConfigMenu.saveMenuToConfig(new ResourceLocation("thebetweenlands:mainmenu"), "thebetweenlands.client.gui.menu.GuiBLMainMenu", true);
		}
		MenuRegistry.registerGuiMenu(MainMenuAetherTest.class, new ResourceLocation("test"));
	}
	
	/**
	 * yes I am fixing mods music
	 */
	public static void menuChange()
	{
		if(thebetweenlands)
		{
			ReflectionUtil.setObject(tbl_instance, false, tbl_musicHandler, "hasBlMainMenu");//sets it to false to garentee it will not play till the next cik
		}
	}
	
	private static void cacheData() 
	{
		if(thebetweenlands)
		{
			tbl_musicHandler =  ReflectionUtil.classForName("thebetweenlands.client.handler.MusicHandler");
			tbl_instance = ReflectionUtil.getObject(null, tbl_musicHandler, "INSTANCE");
		}
	}

}
