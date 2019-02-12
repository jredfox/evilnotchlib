package com.evilnotch.menulib.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.menulib.ConfigMenu;
import com.evilnotch.menulib.compat.ProxyMod;
import com.evilnotch.menulib.eventhandler.MusicEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class MenuRegistry {
	
	protected static List<IMenu> menus = new ArrayList();
	public static int indexMenu = 0;
	protected static IMenu currentMenu = null;
	
	public static void registerIMenu(IMenu menu)
	{
		menus.add(menu);
		ConfigMenu.saveMenuToConfig(menu.getId());
	}
	
	/**
	 * Returns menu so you can manipulate data after adding one
	 * to make an IMenu method use register IMenu
	 * or simply make a new Menu instance(Menu implements IMenu) and override what you need 
	 */
	public static IMenu registerGuiMenu(Class<? extends GuiScreen> guiClazz,ResourceLocation id)
	{
		IMenu menu = new Menu(guiClazz,id);
		menus.add(menu);
		ConfigMenu.saveMenuToConfig(id);
		return menu;
	}
	
	public static void deleteGui(ResourceLocation loc)
	{
		Iterator<IMenu> it = menus.iterator();
		while(it.hasNext())
		{
			IMenu menu = it.next();
			if(menu.getId().equals(loc))
			{
				it.remove();
				break;
			}
		}
	}
	
	/**
	 * used by button guis to advanced current gui to the next gui
	 */
	public static void advanceNextMenu()
	{
		getCurrentMenu().onClose();	
		indexMenu = getNext(indexMenu);
		currentMenu = menus.get(indexMenu);
		ProxyMod.menuChange();
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
		currentMenu.onOpen();
		Minecraft.getMinecraft().displayGuiScreen(MenuRegistry.createCurrentGui());
	}
	/**
	 * used by button guis to advanced current gui to the next gui
	 */
	public static void advancePreviousMenu()
	{
		getCurrentMenu().onClose();
		indexMenu = getPrevious(indexMenu);
		currentMenu = menus.get(indexMenu);
		ProxyMod.menuChange();
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
		currentMenu.onOpen();
		Minecraft.getMinecraft().displayGuiScreen(MenuRegistry.createCurrentGui());
	}
	
	protected static int getNext(int index) 
	{
		if(index + 1 == MenuRegistry.getMenus().size() )
			return 0;
		index++;
		return index;
	}
	
	protected static int getPrevious(int index) 
	{
		if( (index -1) == -1)
			return menus.size()-1;
		index--;
		return index;
	}
	/**
	 * creates a new gui from the current IMenu
	 */
	public static GuiScreen createCurrentGui()
	{
		IMenu menu = getCurrentMenu();
		GuiScreen screen = menu.createGui();
		return screen;
	}
	/**
	 * gets the current gui from the current IMenu without creating a new one for custom stuffs
	 */
	public static GuiScreen getCurrentGui()
	{
		IMenu menu = getCurrentMenu();
		GuiScreen screen = menu.getGui();
		return screen;
	}
	
	public static IMenu getCurrentMenu()
	{
		return currentMenu;
	}
	
	public static List<IMenu> getMenus() 
	{
		return menus;
	}
	
	public static int getMenuSize() 
	{
		return menus.size();
	}
	
	public static boolean containsMenu(Class clazz) 
	{
		for(int i=0;i<menus.size();i++)
		{
			if(menus.get(i).getGuiClass().equals(clazz))
				return true;
		}
		return false;
	}
	
	/**
	 * re-order the menus list also skip any menus that are disabled
	 */
	public static void init() 
	{
		reorderLists();
		checkConfig();
		setConfigIndex();
	}

	public static void setConfigIndex() 
	{
		setMenu(ConfigMenu.currentMenuIndex);
	}

	public static void setMenu(int i) 
	{
		indexMenu = i;
		currentMenu = menus.get(i);
	}

	public static boolean setMenu(ResourceLocation loc) 
	{
		int index = getIndex(loc);
		if(index == -1)
		{
			System.out.println("null menu when trying to set index:" + ConfigMenu.currentMenuIndex);
			return false;
		}
		setMenu(index);
		return true;
	}

	public static void checkConfig() 
	{
		if(ConfigMenu.isDirty)
		{
			if(ConfigMenu.displayNewMenu && ConfigMenu.addedMenus)
			{
				ResourceLocation loc = ConfigMenu.mainMenus.get(ConfigMenu.mainMenus.size()-1).getResourceLocation();//when adding a new menu display it
				ConfigMenu.currentMenuIndex = loc;
			}
			ConfigMenu.saveMenusAndIndex();
		}
	}

	public static void reorderLists() 
	{
		List<IMenu> list = new ArrayList<IMenu>();
		Iterator<LineArray> it = ConfigMenu.mainMenus.iterator();
		while(it.hasNext())
		{
			LineArray line = it.next();
			if(!line.getBoolean())
			{
				continue;
			}
			ResourceLocation loc = line.getResourceLocation();
			if(line.hasStringMeta())
			{
				Class c = ReflectionUtil.classForName(line.getMetaString());
				if(c == null)
				{
					System.out.println("null class when parsing menu for:" + line.getMetaString());
					it.remove();
					ConfigMenu.isDirty = true;
					continue;
				}
				IMenu menu = new Menu(c,loc);
				list.add(menu);
			}
			else
			{
				IMenu menu = getMenu(loc);
				if(menu == null)
				{
					System.out.println("null menu when parsing found for:" + loc);
					it.remove();
					ConfigMenu.isDirty = true;
					continue;
				}
				list.add(menu);
			}
		}
		menus = list;
		
		//more optimized then setting then saving the config twice
		if(!ConfigMenu.hasMenu(ConfigMenu.currentMenuIndex))
		{
			ResourceLocation loc = ConfigMenu.mainMenus.get(0).getResourceLocation();
			System.out.println("null currentIndex found:" + ConfigMenu.currentMenuIndex + " setting currentIndex to 0:" + loc);
			ConfigMenu.currentMenuIndex = loc;
			ConfigMenu.isDirty = true;
		}
	}

	public static IMenu getMenu(ResourceLocation loc) 
	{
		for(IMenu menu : menus)
		{
			if(menu.getId().equals(loc))
				return menu;
		}
		return null;
	}

	public static int getIndex(ResourceLocation loc) 
	{
		for(int i=0;i<menus.size();i++)
		{
			IMenu menu = menus.get(i);
			if(menu.getId().equals(loc))
				return i;
		}
		return -1;
	}
}
