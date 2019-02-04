package com.evilnotch.menulib.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.ILineHead;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigLine;
import com.evilnotch.menulib.ConfigMenu;
import com.evilnotch.menulib.compat.ProxyMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class MenuRegistry {
	
	protected static List<IMenu> menus = new ArrayList();
	public static int indexMenu = 0;
	protected static IMenu currentMenu = null;
	
	public static void registerIMenu(IMenu menu)
	{
		menus.add(menu);
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
		if(currentMenu == null)
			currentMenu = menus.get(indexMenu);
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
		if(checkLists())
		{
			reorderLists();
		}
		else
		{
			System.out.println("MainMenus Config Resseting Checks failed");
			ConfigMenu.saveMenus(menus);
		}
	}

	private static boolean checkLists() 
	{
		if(menus.size() != ConfigMenu.mainMenus.size())
			return false;
		for(IMenu menu : menus)
		{
			ResourceLocation menuloc = menu.getId();
			if(configHasMenu(menuloc))
				return false;
		}
		return true;
	}

	private static void reorderLists() 
	{
		List<IMenu> list = new ArrayList<IMenu>();
		for(LineArray line : ConfigMenu.mainMenus)
		{
			//if disabled return
			if(!line.getBoolean())
				continue;
			ResourceLocation loc = line.getResourceLocation();
			if(line.hasStringMeta())
			{
				IMenu menu = new Menu(ReflectionUtil.classForName(line.getMetaString()),loc);
				list.add(menu);
			}
			else
			{
				IMenu menu = getMenu(loc);
				list.add(menu);
			}
		}
		menus = list;
	}
	
	public static boolean configHasMenu(ResourceLocation loc) 
	{
		for(LineArray line : ConfigMenu.mainMenus)
			if(line.getResourceLocation().equals(loc))
				return true;
		return false;
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

}
