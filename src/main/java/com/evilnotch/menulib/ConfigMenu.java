package com.evilnotch.menulib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigLine;
import com.evilnotch.menulib.menu.IMenu;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigMenu {
	
	public static List<LineArray> mainMenus = new ArrayList();
	public static List<Class> musicAllow = new ArrayList();
	public static List<Class> musicDeny = new ArrayList();
	public static boolean fancyPage = false;
	public static boolean displayNewMenu = true;
	public static ResourceLocation currentMenuIndex = null;
	public static File cfgmenu = null;
	
	public static int leftButtonId = 498;
	public static int rightButtonId = 499;
	
	private static final String menu_comment = "format of menus is \"modid:mainmenu <class> = true/false\" changeing the menu order will change it in game";
	
	/**
	 * load all configurations for menu lib
	 */
	public static void loadMenuLib(File d) 
	{
		cfgmenu = d;
		
		Configuration config = new Configuration(cfgmenu);
		
		config.load();
		fancyPage = config.get("menulib","fancyMenuPage",false).getBoolean();
		displayNewMenu = config.get("menulib","displayNewMenu",true).getBoolean();
		currentMenuIndex = new ResourceLocation(config.get("menulib", "currentMenuIndex", "").getString());
		leftButtonId = config.get("menulib","buttonLeftId",leftButtonId).getInt();
		rightButtonId = config.get("menulib","buttonRightId",rightButtonId).getInt();

		String[] order = config.get("menulib", "menus", new String[]{""},menu_comment).getStringList();
		resetMenus(order);
		
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
	
	private static void resetMenus(String[] order) 
	{
		mainMenus.clear();
		for(String s : order)
		{
			if(s == null || JavaUtil.toWhiteSpaced(s).equals(""))
				continue;
			LineArray line = new LineArray(s);
			if(!line.hasHead())
				line.setHead(true);
			mainMenus.add(line);
		}
	}
	/**
	 * does the config need to save
	 */
	public static boolean isDirty = false;
	/**
	 * does the config have a new menu
	 */
	public static boolean addedMenus = false;
	public static void saveMenusAndIndex() 
	{
		Configuration config = new Configuration(cfgmenu);
		config.load();
		
		//set the initial index to the first menu when generating the config
		if(currentMenuIndex.getResourcePath().isEmpty())
		{
			currentMenuIndex = mainMenus.get(0).getResourceLocation();
			Property prop = config.get("menulib", "currentMenuIndex", "");
			prop.set(currentMenuIndex.toString());
		}
		
		List<String> list = new ArrayList();
		for(LineArray line : mainMenus)
		{
			if(!list.contains(line.getResourceLocation().toString()))
			{
				if(!line.getBoolean())
					list.add(line.toString());
				else
				{
					String s = line.getMetaString();
					if(!s.isEmpty())
						s = " <" + s + ">";
					list.add(line.getResourceLocation() + s);
				}
			}
		}
		
		setConfigIndex(config,currentMenuIndex);
		
		//fix comment dissapearing
		String[] strlist = JavaUtil.toStaticStringArray(list);
		Property prop = config.get("menulib", "menus", strlist,menu_comment);
		prop.set(strlist);
		
		config.save();
		isDirty = false;
	}

	private static void setConfigIndex(Configuration config,ResourceLocation loc) 
	{
		Property prop_index = config.get("menulib", "currentMenuIndex", "");
		prop_index.set(loc.toString());
	}

	public static void saveMenuIndex()
	{
		saveMenuIndex(MenuRegistry.getCurrentMenu().getId());
	}
	
	public static void saveMenuIndex(ResourceLocation loc) 
	{
		long stamp = System.currentTimeMillis();
		Configuration config = new Configuration(cfgmenu);
		config.load();
		setConfigIndex(config,loc);
		currentMenuIndex = loc;
		config.get("menulib", "menus", new String[]{""},menu_comment);
		config.save();
		if(Config.debug)
		{
			JavaUtil.printTime(stamp, "Saved Current Menu:");
		}
	}
	
	/**
	 * don't call this till after the config has loaded
	 */
	public static void saveMenuToConfig(ResourceLocation loc) 
	{
		if(!hasMenu(loc))
		{
			mainMenus.add(new LineArray(loc.toString() + " = " + true));
			isDirty = true;
			addedMenus = true;
		}
	}
	/**
	 *  don't call this till after the config is loaded. Use this method if you choose not to register the IMenu directly. 
	 *  Currently only in use for the betweenlands to show people they can input custom entries
	 */
	public static void saveMenuToConfig(ResourceLocation loc, String clazz, boolean enabled) 
	{
		if(!hasMenu(loc))
		{
			mainMenus.add(new LineArray(loc + " <" + clazz + ">" + " = " + enabled));
			isDirty = true;
			addedMenus = true;
		}
	}
	
	public static boolean hasMenu(ResourceLocation loc) 
	{
		for(LineArray line : ConfigMenu.mainMenus)
			if(line.getResourceLocation().equals(loc))
				return true;
		return false;
	}


}
