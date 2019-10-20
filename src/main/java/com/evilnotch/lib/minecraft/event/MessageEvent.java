package com.evilnotch.lib.minecraft.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
@Cancelable
public class MessageEvent extends EntityEvent{
	
	public ITextComponent text;
	public MessageEvent(Entity e, ITextComponent txt)
	{
		super(e);
		this.text = txt;
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
		public SendMessage(Entity e, ITextComponent txt)
		{
			super(e, txt);
		}
	}
	
	/**
	 * this fires only when status messages are fired for a player like a bossbar or simply regular messages
	 */
	public static class PlayerStatusEvent extends MessageEvent
	{
		public boolean actionBar;
		public PlayerStatusEvent(Entity e, ITextComponent txt, boolean actionBar)
		{
			super(e, txt);
			this.actionBar = actionBar;
		}
	}

}
