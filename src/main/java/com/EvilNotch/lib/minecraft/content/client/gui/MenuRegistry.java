package com.EvilNotch.lib.minecraft.content.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class MenuRegistry {
	
	protected static ArrayList<IMenu> menus = new ArrayList();
	public static int indexMenu = 0;
	protected static IMenu currentMenu = null;
	
	public static void registerIMenu(IMenu menu){
		menus.add(menu);
	}
	/**
	 * Returns menu so you can manipulate data after adding one
	 * to make an IMenu method use register IMenu
	 * or simply make a new Menu instance(Menu implements IMenu) and override what you need 
	 */
	public static IMenu registerGuiMenu(Class<? extends GuiScreen> guiClazz,ResourceLocation id){
		IMenu menu = new Menu(guiClazz,id);
		menus.add(menu);
		return menu;
	}
	/**
	 * Returns menu so you can manipulate data after adding one
	 * to make an IMenu method use register IMenu
	 * or simply make a new Menu instance(Menu implements IMenu) and override what you need 
	 */
	public static IMenu registerGuiMenu(int index,Class<? extends GuiScreen> guiClazz,ResourceLocation id){
		IMenu menu = new Menu(guiClazz,id);
		menus.add(index,menu);
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
		try
		{
			doModSupport();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
		currentMenu.onOpen();
	}
	
	/**
	 * yes I am fixing another broken mod since it just happens to be popular
	 */
	protected static void doModSupport() throws Throwable
	{
		if(Loader.isModLoaded("thebetweenlands"))
		{
			Class shitty = Class.forName("thebetweenlands.client.handler.MusicHandler");
			Object instance = ReflectionUtil.getObject(null, shitty, "INSTANCE");
			ReflectionUtil.setObject(instance, false, shitty, "hasBlMainMenu");//sets it to false to garentee it will not play till the next cik
		}
	}
	public static void advancePreviousMenu()
	{
		getCurrentMenu().onClose();
		indexMenu = getPrevious(indexMenu);
		currentMenu = menus.get(indexMenu);
		try
		{
			doModSupport();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
		currentMenu.onOpen();
	}
	
	public static int getNext(int index) 
	{
		if(index + 1 == MenuRegistry.getMenus().size() )
			return 0;
		index++;
		return index;
	}
	public static int getPrevious(int index) 
	{
		if( (index -1) == -1)
			return menus.size()-1;
		index--;
		return index;
	}

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
	
	/**
	 * re-orders menus based on configured results rather then coders overrides
	 */
	public static void reOrder()
	{
		if(Config.menuWhiteList)
		{
			ArrayList<IMenu> newMenus = new ArrayList();
			for(ResourceLocation loc : Config.whiteListConfig)
			{
				IMenu menu = MenuRegistry.getMenu(loc);
				if(menu == null)
					System.out.print("[MenuLib/ERR] Null Menu Skipping:" + loc + "\n");
				else
					newMenus.add(menu);
			}
			menus = newMenus;
			return;
		}
		
		if(Config.menusConfig.size() == 0)
		{
			//error catch
			System.out.print("[MenuLib/ERR] Returning Menu Size Configured List 0\nReturning And Repairing Config:\n");
			Config.saveMenus(getIds());
			return;
		}
		else if(menus.size() != Config.menusConfig.size()){
			//error catch sizes must be the same
			System.out.print("[MenuLib/ERR] Size Not Same As Codded Switch WhiteList on if your removing GUIS\nRepairing Config!\n");
			Config.saveMenus(getIds());
			return;
		}
		else{
			//default ordering system from config
			ArrayList newMenus = new ArrayList();
			for(int i=0;i<menus.size();i++)
			{
				ResourceLocation loc = Config.menusConfig.get(i);
				IMenu menu = getMenu(loc);
				if(menu == null)
				{
					MainJava.logger.log(Level.FATAL, "[MenuLib/ERR] Null Menu For Resource Location: " + loc + "\nInput Not Accepted Skipping Menu List Reparing Config");
					Config.saveMenus(getIds());
					return;
				}
				newMenus.add(menu);
			}
			menus = newMenus;
		}
	}

	public static List<ResourceLocation> getIds() {
		ArrayList<ResourceLocation> locs = new ArrayList();
		for(IMenu menu : menus)
			locs.add(menu.getId());
		return locs;
	}
	public static IMenu getMenu(ResourceLocation loc) {
		for(IMenu menu : menus)
			if(menu.getId().equals(loc))
				return menu;
		return null;
	}

	public static ArrayList<IMenu> getMenus() {
		return menus;
	}

	public static int getMenuSize() {
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
	public static void setCurrentMenu(ResourceLocation loc)
	{
		IMenu menu = null;
		int index = 0;
		for(int i=0;i<menus.size();i++)
		{
			IMenu m = menus.get(i);
			if(m.getId().equals(loc))
			{
				menu = m;
				index = i;
			}
		}
		if(menu == null)
			MainJava.logger.log(Level.ERROR,"[MenuLib/ERR] Unable to find Current Index for Requested Menu");
		indexMenu = index;
		currentMenu = menu;
	}

}
