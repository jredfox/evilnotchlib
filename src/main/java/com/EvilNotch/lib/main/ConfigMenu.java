package com.EvilNotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.LineBase;

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
				if(s == null || LineBase.toWhiteSpaced(s).equals("") || LineBase.toWhiteSpaced(s).indexOf('#') == 0)
					continue;
					menusConfig.add(new LineBase(s).getResourceLocation());
			}
			
			String[] wlist = config.get("menulib","menu_whitelist",new String[]{""}).getStringList();
			for(String s : wlist)
			{
				if(s == null || LineBase.toWhiteSpaced(s).equals("") || LineBase.toWhiteSpaced(s).indexOf('#') == 0)
					continue;
				whiteListConfig.add(new LineBase(s).getResourceLocation());
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
