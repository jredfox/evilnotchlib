package com.evilnotch.lib.minecraft.event.client;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MessageEvent extends Event {
	
	public ITextComponent text;
	
	public MessageEvent(ITextComponent txt)
	{
		this.text = txt;
	}
	
	public MessageEvent(String txt)
	{
		this.text = new TextComponentString(txt == null ? "" : txt);
	}
	
	/**
	 * Fires When adding a MSG to the command history
	 */
	public static class Add extends MessageEvent
	{
		public Add(String msg)
		{
			super(msg);
		}
	}
	
	/**
	 * Fires When a Chat Overlay Msg Happens. Example "Bed Already Occupied"
	 */
	public static class Overlay extends MessageEvent
	{
		boolean animateColor;
		
		public Overlay(String txt, boolean color)
		{
			super(txt);
			this.animateColor = color;
		}
		
		public Overlay(ITextComponent txt, boolean color)
		{
			super(txt);
			this.animateColor = color;
		}
	}
	
	/**
	 * Fires when a MSG prints to the screen
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
	
	/**
	 * Fires when the client tries to send a msg to the server
	 */
	public static class Send extends MessageEvent
	{	
		public Send(String txt) 
		{
			super(txt);
		}
	}
	
	/**
	 * Fired (GuiIngame#addChatMessage) when a Chat is Received or is Called from a Client to add it to the screen. Narrator will fire from this if it's enabled
	 */
	public static class Say extends MessageEvent
	{
		ChatType type;
		
		public Say(ChatType chatTypeIn, ITextComponent txt) 
		{
			super(txt);
			this.type = chatTypeIn;
		}
	}

	public static String Send(String message) 
	{
		MessageEvent.Send event = new MessageEvent.Send(message);
		if(MinecraftForge.EVENT_BUS.post(event))
			return null;
		return event.text.getUnformattedText();
	}

}
