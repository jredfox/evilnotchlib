package com.evilnotch.menulib.event;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * use this event to stop vanilla music from playing when it's not your gui menu
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

}
