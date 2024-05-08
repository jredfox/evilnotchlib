package com.evilnotch.lib.minecraft.event.client;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MessageEvent extends Event {
	
	public ITextComponent text;
	
	public MessageEvent(ITextComponent txt)
	{
		this.text = txt;
	}
	
	/**
	 * Fires Before IChatListeners
	 */
	public static class Add extends MessageEvent
	{
		public Add(ITextComponent txt)
		{
			super(txt);
		}
	}
	
	/**
	 * Fires When a Chat Overlay Msg Happens. Example "Bed Already Occupied"
	 */
	public static class Overlay extends MessageEvent
	{
		public Overlay(ITextComponent txt)
		{
			super(txt);
		}
	}
	
	/**
	 * Fires when a MSG prints
	 */
	public static class Print extends MessageEvent
	{
		public int indent;
		
		public Print(ITextComponent txt)
		{
			this(txt, 0);
		}
		
		public Print(ITextComponent txt, int i)
		{
			super(txt);
			this.indent = i;
		}
	}

}
