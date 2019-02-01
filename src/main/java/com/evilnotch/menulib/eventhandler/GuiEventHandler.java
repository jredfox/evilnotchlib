package com.evilnotch.menulib.eventhandler;

import java.util.List;

import com.evilnotch.lib.minecraft.content.client.gui.GuiFakeMenu;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.menulib.ConfigMenu;
import com.evilnotch.menulib.event.MenuMusicEvent;
import com.evilnotch.menulib.menu.IMenu;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiEventHandler {
	
	@SubscribeEvent
	public void canPlayMusic(MenuMusicEvent e)
	{
		for(Class c : ConfigMenu.musicDeny)
		{
			if(JavaUtil.isClassExtending(c, e.gui.getClass()))
			{
				e.canPlay = false;
				return;
			}
		}
		for(Class c : ConfigMenu.musicAllow)
		{
			if(JavaUtil.isClassExtending(c, e.gui.getClass()))
			{
				e.canPlay = true;
				break;
			}
		}
	}
	
	/**
	 * set the gui to something mods are never going to be looking at
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGuiOpenPre(GuiOpenEvent e)
	{
		GuiScreen gui = e.getGui();
		if(gui == null)
			return;
//		System.out.println(e.getGui().getClass());
		if(!(gui instanceof GuiMainMenu) && !MenuRegistry.containsMenu(gui.getClass() ) )
		{
			return;
		}
		e.setGui(new GuiFakeMenu());
	}
	/**
	 * set gui after mods are stopping looking for the main screen
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onGuiOpen(GuiOpenEvent e)
	{
		GuiScreen gui = e.getGui();
		if(gui == null)
			return;
		if(!(gui instanceof GuiFakeMenu))
		{
			return;
		}
		e.setGui(MenuRegistry.getCurrentGui());
	}
	@SubscribeEvent
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post e)
	{
		GuiScreen gui = e.getGui();
		if(gui == null)
			return;
		if(MenuRegistry.getMenuSize() > 1)
		{
			Class clazz = gui.getClass();
			if(!MenuRegistry.containsMenu(clazz) )
			{
				return;
			}
			IMenu menu = MenuRegistry.getCurrentMenu();
			if(menu.allowButtonOverlay())
			{
				List<GuiButton> li = e.getButtonList();
				li.add(menu.getLeftButton());
				li.add(menu.getRightButton());
			}
		}
	}
	@SubscribeEvent
	public void guiButtonClick(GuiScreenEvent.ActionPerformedEvent.Pre e)
	{
		GuiScreen gui = e.getGui();
		if(gui == null)
			return;
		Class clazz = gui.getClass();
		if(!MenuRegistry.containsMenu(clazz))
			return;

		if(e.getButton().id == 498)
		{
			MenuRegistry.advancePreviousMenu();
			Minecraft.getMinecraft().displayGuiScreen(MenuRegistry.getCurrentGui());
			ConfigMenu.saveMenuIndex();
		}
		else if(e.getButton().id == 499)
		{
			MenuRegistry.advanceNextMenu();
			Minecraft.getMinecraft().displayGuiScreen(MenuRegistry.getCurrentGui());
			ConfigMenu.saveMenuIndex();
		}
	}

}
