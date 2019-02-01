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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class MenuRegistry {
	
	protected static ArrayList<IMenu> menus = new ArrayList();
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
	 * yes I am fixing mods music
	 */
	protected static void doModSupport() throws Throwable
	{
		if(Loader.isModLoaded("thebetweenlands"))
		{
			Class s = Class.forName("thebetweenlands.client.handler.MusicHandler");
			Object instance = ReflectionUtil.getObject(null, s, "INSTANCE");
			ReflectionUtil.setObject(instance, false, s, "hasBlMainMenu");//sets it to false to garentee it will not play till the next cik
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
		if(ConfigMenu.menuWhiteList)
		{
			ArrayList<IMenu> newMenus = new ArrayList();
			for(ResourceLocation loc : ConfigMenu.whiteListConfig)
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
		
		if(ConfigMenu.menusConfig.isEmpty())
		{
			//error catch
			System.out.print("[MenuLib/ERR] Returning Menu Size Configured List 0\nReturning And Repairing Config:\n");
			ConfigMenu.saveMenus(getIds());
			return;
		}
		else if(menus.size() != ConfigMenu.menusConfig.size())
		{
			//error catch sizes must be the same
			System.out.print("[MenuLib/ERR] Size Not Same As Codded Switch WhiteList on if your removing GUIS\nRepairing Config!\n");
			ConfigMenu.saveMenus(getIds());
			return;
		}
		else if(!checkMenus())
		{
			System.out.print("[MenuLib/ERR] Config content isn't the same as coded saving ids");
			ConfigMenu.saveMenus(getIds());
			return;
		}
		else
		{
			//default ordering system from config
			ArrayList newMenus = new ArrayList();
			for(int i=0;i<menus.size();i++)
			{
				ResourceLocation loc = ConfigMenu.menusConfig.get(i);
				IMenu menu = getMenu(loc);
				if(menu == null)
				{
					LoaderMain.logger.log(Level.FATAL, "[MenuLib/ERR] Null Menu For Resource Location: " + loc + "\nInput Not Accepted Skipping Menu List Reparing Config");
					ConfigMenu.saveMenus(getIds());
					return;
				}
				newMenus.add(menu);
			}
			menus = newMenus;
		}
	}
	/**
	 * returns true if the check succeeded in matching all coded locs with all configured ones
	 */
	public static boolean checkMenus() 
	{
		for(IMenu menu : menus)
		{
			ResourceLocation menuLoc = menu.getId();
			if(!ConfigMenu.menusConfig.contains(menuLoc))
				return false;
		}
		return true;
	}
	
	public static List<ResourceLocation> getIds() 
	{
		ArrayList<ResourceLocation> locs = new ArrayList();
		for(IMenu menu : menus)
			locs.add(menu.getId());
		return locs;
	}
	public static IMenu getMenu(ResourceLocation loc) 
	{
		for(IMenu menu : menus)
			if(menu.getId().equals(loc))
				return menu;
		return null;
	}

	public static ArrayList<IMenu> getMenus() 
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
			LoaderMain.logger.log(Level.ERROR,"[MenuLib/ERR] Unable to find Current Index for Requested Menu");
		indexMenu = index;
		currentMenu = menu;
	}
	
	/**
	 * Reorder menus or if client overrides using whitelist do only the whitelist
	 */
	public static void loadInit() 
	{	
		//register user registered menus
		File f = new File(ConfigMenu.cfgmenu.getParent(),"menulib.cfg");
		List<String> comments = JavaUtil.asStringList(new String[]{"Menu Lib Configuration File. Register Other Mod's Main Menus That refuse to do it themselves :(","Format is: \"modid:mainmenu\" = \"class.full.name\""});
		ConfigBase cfg = new ConfigLine(f,comments);
		cfg.loadConfig();
		
		if(Loader.isModLoaded("thebetweenlands"))
		{
			cfg.addLine(new LineArray("\"thebetweenlands:mainmenu\" = \"thebetweenlands.client.gui.menu.GuiBLMainMenu\""));
			cfg.saveConfig(false, false, true);
		}
		for(ILine line : cfg.lines)
		{
			ILineHead head = (ILineHead)line;
			try
			{
				MenuRegistry.registerGuiMenu((Class<? extends GuiScreen>) Class.forName(head.getString()), line.getResourceLocation());
			}
			catch(Throwable t)
			{
				System.out.print("[MenuLib/ERR] Unable to Locate class skipping menu registration for:" + line + "\n");
			}
		}
		
		MenuRegistry.reOrder();
		MenuRegistry.setCurrentMenu(ConfigMenu.currentMenuIndex);
	}

}
