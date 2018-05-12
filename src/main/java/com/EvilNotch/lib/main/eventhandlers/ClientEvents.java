package com.EvilNotch.lib.main.eventhandlers;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.content.client.gui.GuiFakeMenu;
import com.EvilNotch.lib.minecraft.content.client.gui.IMenu;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {
	
	@SubscribeEvent
	public void onGuiDisconnect(GuiOpenEvent e)
	{
		if(e.getGui() == null || !(e.getGui() instanceof GuiDisconnected) || EntityUtil.msgShutdown == null)
			return;
		GuiDisconnected old = (GuiDisconnected)e.getGui();
		e.setGui(new GuiDisconnected(new GuiMainMenu(),"disconnect.lost", EntityUtil.msgShutdown) );
		EntityUtil.msgShutdown = null;
	}
	
	/**
	 * set the gui to something mods are never going to be looking at
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onGuiOpenPre(GuiOpenEvent e)
	{
		if(e.getGui() == null)
			return;
//		System.out.println(e.getGui().getClass());
		if(!(e.getGui() instanceof GuiMainMenu))
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
		if(e.getGui() == null)
			return;
		if(!(e.getGui() instanceof GuiFakeMenu))
		{
			return;
		}
		e.setGui(MenuRegistry.getCurrentGui());
	}
	@SubscribeEvent
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post e)
	{
		if(e.getGui() == null)
			return;
		if(MenuRegistry.getMenuSize() > 1)
		{
			Class clazz = e.getGui().getClass();
			if(!MenuRegistry.containsMenu(clazz))
			{
				return;
			}
			IMenu menu = MenuRegistry.getCurrentMenu();
			if(menu.allowButtonOverlay())
			{
				e.getButtonList().add(menu.getButton(true));
				e.getButtonList().add(menu.getButton(false));
			}
		}
	}
	@SubscribeEvent
	public void guiButtonClick(GuiScreenEvent.ActionPerformedEvent.Pre e)
	{
		if(e.getGui() == null)
			return;
		Class clazz = e.getGui().getClass();
		if(!MenuRegistry.containsMenu(clazz))
			return;
		if(e.getButton().id == 498)
		{
			MenuRegistry.advancePreviousMenu();
			Minecraft.getMinecraft().displayGuiScreen(MenuRegistry.getCurrentGui());
			Config.saveMenuIndex();
		}
		else if(e.getButton().id == 499)
		{
			MenuRegistry.advanceNextMenu();
			Minecraft.getMinecraft().displayGuiScreen(MenuRegistry.getCurrentGui());
			Config.saveMenuIndex();
		}
	}
	
}
