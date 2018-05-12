package com.EvilNotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.client.gui.IMenu;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	public static boolean debug = false;
	public static File cfg = null;
	public static ArrayList<ResourceLocation> cmdBlacklist = new ArrayList();
	public static List<ResourceLocation> menusConfig = new ArrayList();
	public static List<ResourceLocation> whiteListConfig = new ArrayList();
	
	public static int slimeInventorySize = -1;
	public static boolean isDev = false;
	public static boolean fancyPage = false;
	public static boolean menuWhiteList = false;
	public static ResourceLocation currentMenuIndex = null;
	public static boolean playerOwnerAlwaysFix = true;
	public static boolean playerDataFixOptimized = true;
	
	public static void loadConfig(File d){
		if(cfg == null)
			cfg = new File(d,MainJava.MODID + "/" + MainJava.MODID + ".cfg");
		Configuration config = new Configuration(cfg);
		config.load();
		debug = config.get("general", "Debug", false).getBoolean();
		slimeInventorySize = config.get("entity", "slimeSizeInCache", 2).getInt();
		isDev = config.get("general", "isDev", false).getBoolean();
		fancyPage = config.get("menulib","FancyMenuPage",false).getBoolean();
		menuWhiteList = config.get("menulib", "useWhiteList", false).getBoolean();
		playerDataFixOptimized = config.get("vanilla_fixer", "playerDataFixerOptimized", true).getBoolean();
		playerOwnerAlwaysFix = config.get("vanilla_fixer", "playerOwnerSwapAlwaysFix", true).getBoolean();
				
		String[] vars = config.getStringList("blacklistCMDNames", "entity", new String[]{"\"modid:mobname\""}, "Blacklist for command sender names so it always uses general when translating input with quotes \"modid:mobname\" ");
		for(String s : vars)
		{
			if(!LineBase.toWhiteSpaced(s).equals(""))
				cmdBlacklist.add((new LineBase(s)).getResourceLocation() );
		}
		if(MainJava.isClient)
		{
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
			currentMenuIndex = new ResourceLocation(config.get("menulib", "CurrentMenuIndex", "minecraft:mainmenu").getString());
		}
		config.save();
	}

	public static void saveMenus(List<ResourceLocation> locs) 
	{
		Configuration config = new Configuration(cfg);
		config.load();
		
		String[] list = JavaUtil.toStaticStringArray(locs);
		Property prop = config.get("menulib", "menu_order", list,"Enable White List to Use order if You Delete Any Gui Menus");
		prop.set(list);
		
		config.save();
	}

	public static void saveMenuIndex() {
		long stamp = System.currentTimeMillis();
		Configuration config = new Configuration(cfg);
		config.load();
		Property prop = config.get("menulib", "CurrentMenuIndex", "minecraft:mainmenu");
		ResourceLocation loc = MenuRegistry.getCurrentMenu().getId();
		prop.set(loc.toString());
		currentMenuIndex = loc;
		config.save();
		System.out.println("herezzzzzzzzzzzzzzzzzzzzzzzzzz");
		JavaUtil.printTime(stamp, "Saved Current Menu:");
	}

}
