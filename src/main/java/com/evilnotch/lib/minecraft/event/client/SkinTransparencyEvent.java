package com.evilnotch.lib.minecraft.event.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SkinTransparencyEvent extends Event {
	
	public int[] imageData;
	public boolean allowTrans;
	
	public SkinTransparencyEvent(int[] imageData)
	{
		this.imageData = imageData;
		this.allowTrans = false;
	}

	public static boolean allow(int[] imageData)
	{
		SkinTransparencyEvent event = new SkinTransparencyEvent(imageData);
		MinecraftForge.EVENT_BUS.post(event);
		return event.allowTrans;
	}

}
