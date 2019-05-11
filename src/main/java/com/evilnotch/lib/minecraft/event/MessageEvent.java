package com.evilnotch.lib.minecraft.event;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
@Cancelable
public class MessageEvent extends EntityEvent{
	
	public MessageEvent(Entity e)
	{
		super(e);
	}
	
	public World getWorld()
	{
		return this.getEntity().world;
	}
	
	/**
	 * use this for generic message events
	 */
	public static class SendMessage extends MessageEvent
	{
		public SendMessage(Entity e)
		{
			super(e);
		}
	}
	
	/**
	 * this fires only when status messages are fired for a player like a bossbar or simply regular messages
	 */
	public static class PlayerStatusEvent extends MessageEvent
	{
		public boolean actionBar;
		public PlayerStatusEvent(Entity e, boolean actionBar)
		{
			super(e);
			this.actionBar = actionBar;
		}
	}

}
