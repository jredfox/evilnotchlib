package com.evilnotch.lib.minecraft.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class EventCanceler {
	
	public Class clazz;
	public boolean setIsCanceled;
	public Side side;
	public Event toIgnore;
	
	public EventCanceler(Event ignore,Class clazz, boolean setCanceled, Side side)
	{
		this.toIgnore = ignore;
		this.clazz = clazz;
		this.setIsCanceled = setCanceled;
		this.side = side;
	}

}
