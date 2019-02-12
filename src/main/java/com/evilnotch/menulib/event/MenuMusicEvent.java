package com.evilnotch.menulib.event;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * used to determine if music can play. Currently only supports vanilla however modders can fire this for their main menu music
 * @author jredfox
 */
public class MenuMusicEvent extends Event {
	
	public GuiScreen gui;
	public boolean canPlay;
	
	public MenuMusicEvent(GuiScreen screen)
	{
		this.gui = screen;
		this.canPlay = this.gui instanceof GuiMainMenu;
	}
	
	/**
	 * use this to fire the event
	 */
	public static MenuMusicEvent fireMusicEvent(GuiScreen screen)
	{
		MenuMusicEvent e = new MenuMusicEvent(screen);
		MinecraftForge.EVENT_BUS.post(e);
		return e;
	}

}
