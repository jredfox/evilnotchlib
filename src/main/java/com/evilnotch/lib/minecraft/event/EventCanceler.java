package com.evilnotch.lib.minecraft.event;

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
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
