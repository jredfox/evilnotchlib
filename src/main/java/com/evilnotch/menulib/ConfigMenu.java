package com.evilnotch.menulib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.menulib.menu.IMenu;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigMenu {
	
	//menu lib
	public static List<LineArray> mainMenus = new ArrayList();
	public static List<Class> musicAllow = new ArrayList();
	public static List<Class> musicDeny = new ArrayList();
	public static boolean fancyPage = false;
	public static ResourceLocation currentMenuIndex = null;
	public static File cfgmenu = null;
	
	/**
	 * load all configurations for menu lib
	 */
	public static void loadMenuLib(File d) 
	{
		if(cfgmenu == null)
		{
			cfgmenu = new File(d,"menulib/config.cfg");
		}
		
		Configuration config = new Configuration(cfgmenu);
		
		config.load();
		fancyPage = config.get("menulib","fancyMenuPage",false).getBoolean();
		currentMenuIndex = new ResourceLocation(config.get("menulib", "currentMenuIndex", "minecraft:mainmenu").getString());
		
		String[] order = config.get("menulib", "menus", new String[]{""},"to disable menu append equals false at the end of it. The order of the list will be the order of the menus").getStringList();
		for(String s : order)
		{
			if(s == null || JavaUtil.toWhiteSpaced(s).equals(""))
				continue;
			LineArray line = new LineArray(s);
			if(!line.hasHead())
				line.setHead(true);
			mainMenus.add(line);
		}
		
		String[] clList = config.getStringList("classes_allowed", "music", new String[]{"lumien.custommainmenu.gui.GuiCustom"}, "this is a whitelist of menus not extending GuiMainMenu that require vanilla music");
		for(String s : clList)
		{
			if(JavaUtil.toWhiteSpaced(s).isEmpty())
				continue;
			Class c = ReflectionUtil.classForName(s);
			if(c != null)
				musicAllow.add(c);
		}
		
		String[] clDenyList = config.getStringList("classes_deny", "music", new String[]{""}, "this is a blacklist of menus that extend GuiMainMenu but, have their own music");
		for(String s : clDenyList)
		{
			if(JavaUtil.toWhiteSpaced(s).isEmpty())
				continue;
			Class c = ReflectionUtil.classForName(s);
			if(c != null)
				musicDeny.add(c);
		}
		
		config.save();
	}

	public static void saveMenus(List<IMenu> menus) 
	{
		Configuration config = new Configuration(cfgmenu);
		config.load();
		
		String[] list = new String[menus.size()];
		for(int i=0;i<menus.size();i++)
		{
			list[i] = menus.get(i).getId().toString();
		}
		Property prop = config.get("menulib", "menu_order", list,"to disable menu append equals false at the end of it. The order of the list will be the order of the menus");
		prop.set(list);
		
		config.save();
	}

	public static void saveMenuIndex() 
	{
		long stamp = System.currentTimeMillis();
		Configuration config = new Configuration(cfgmenu);
		config.load();
		Property prop = config.get("menulib", "currentMenuIndex", "minecraft:mainmenu");
		ResourceLocation loc = MenuRegistry.getCurrentMenu().getId();
		prop.set(loc.toString());
		currentMenuIndex = loc;
		config.save();
		if(Config.debug)
		{
			JavaUtil.printTime(stamp, "Saved Current Menu:");
		}
	}

}
