package com.evilnotch.menulib.eventhandler;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.menulib.ConfigMenu;
import com.evilnotch.menulib.event.MenuMusicEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MusicEventHandler {
	
	@SubscribeEvent
	public void canPlayMusic(MenuMusicEvent e)
	{
		if(!(e.gui instanceof GuiMainMenu))
		{
			return;
		}
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

}
