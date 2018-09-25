package com.evilnotch.menulib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.Line;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigMenu {
	
	//menu lib
	public static List<ResourceLocation> menusConfig = new ArrayList();
	public static List<ResourceLocation> whiteListConfig = new ArrayList();
	public static boolean fancyPage = false;
	public static boolean menuWhiteList = false;
	public static ResourceLocation currentMenuIndex = null;
	public static File cfgmenu = null;
	
	/**
	 * load all configurations for menu lib
	 */
	public static void loadMenuLib(File d) 
	{
		if(cfgmenu == null)
			cfgmenu = new File(d,"menulib/config.cfg");
		
		Configuration config = new Configuration(cfgmenu);
		
		config.load();
		
		if(MainJava.isClient)
		{
			fancyPage = config.get("menulib","FancyMenuPage",false).getBoolean();
			menuWhiteList = config.get("menulib", "useWhiteList", false).getBoolean();
			currentMenuIndex = new ResourceLocation(config.get("menulib", "CurrentMenuIndex", "minecraft:mainmenu").getString());
			
			String[] order = config.get("menulib", "menu_order", new String[]{""},"Enable and use WhiteList for sizes less then this list").getStringList();
			for(String s : order)
			{
				if(s == null || JavaUtil.toWhiteSpaced(s).equals("") || JavaUtil.toWhiteSpaced(s).indexOf('#') == 0)
					continue;
					menusConfig.add(new Line(s).getResourceLocation());
			}
			
			String[] wlist = config.get("menulib","menu_whitelist",new String[]{""}).getStringList();
			for(String s : wlist)
			{
				String wspaced = JavaUtil.toWhiteSpaced(s);
				if(s == null || wspaced.equals("") || wspaced.indexOf('#') == 0)
					continue;
				whiteListConfig.add(new Line(s).getResourceLocation());
			}
		}
		
		config.save();
	}

	public static void saveMenus(List<ResourceLocation> locs) 
	{
		Configuration config = new Configuration(cfgmenu);
		config.load();
		
		String[] list = JavaUtil.toStaticStringArray(locs);
		Property prop = config.get("menulib", "menu_order", list,"Enable White List to Use order if You Delete Any Gui Menus");
		prop.set(list);
		
		config.save();
	}

	public static void saveMenuIndex() {
		long stamp = System.currentTimeMillis();
		Configuration config = new Configuration(cfgmenu);
		config.load();
		Property prop = config.get("menulib", "CurrentMenuIndex", "minecraft:mainmenu");
		ResourceLocation loc = MenuRegistry.getCurrentMenu().getId();
		prop.set(loc.toString());
		currentMenuIndex = loc;
		config.save();
		JavaUtil.printTime(stamp, "Saved Current Menu:");
	}

}
