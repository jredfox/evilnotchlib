package com.evilnotch.lib.main.skin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SkinEvent extends Event {
	
	/**
	 * Fires on the Main thread before the selected skin is about to be refreshed
	 */
	public static class User extends SkinEvent
	{
		public final String org_username;
		public String       username;
		
		public User(String username)
		{
			this.org_username = username;
			this.username = username;
		}
		
		public static String fire(String s) 
		{
			User cap = new User(s);
			MinecraftForge.EVENT_BUS.post(cap);
			return cap.username;
		}
	}
	
	/**
	 * Fires on the SkinCache Downloading thread and the Skin is the selected skin
	 * Use this to update capes and skin urls and override the player model (slim or default). 
	 * Call {@link SkinCache#INSTANCE#getOrDownload(String, boolean)} to get a SkinEntry during this event
	 */
	public static class Capability extends SkinEvent
	{
		public final SkinEntry org_skin;
		public SkinEntry skin;
		public boolean selected;
		
		public Capability(SkinEntry s)
		{
			this.org_skin = s; 
			this.skin = s;
		}

		public static SkinEntry fire(SkinEntry s, String user) 
		{
			//sync skin with username
			s = s.copy();
			s.user = user;
			
			Capability cap = new Capability(s);
			MinecraftForge.EVENT_BUS.post(cap);
			return cap.skin;
		}
	}
	
	public static class Mouse extends SkinEvent
	{
		public boolean ears;
		public EntityPlayer player;
		
		public Mouse(EntityPlayer p)
		{
			this.player = p;
		}
	}
	
	public static class Dinnerbone extends SkinEvent
	{
		public boolean dinnerbone;
		public EntityPlayer player;
		
		public Dinnerbone(EntityPlayer p)
		{
			this.player = p;
		}
	}
	
	public static boolean fireMouse(EntityPlayer player)
	{
		SkinEvent.Mouse event = new SkinEvent.Mouse(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.ears;
	}

	public static boolean fireDinnerbone(EntityPlayer p)
	{
		SkinEvent.Dinnerbone event = new SkinEvent.Dinnerbone(p);
		MinecraftForge.EVENT_BUS.post(event);
		return event.dinnerbone;
	}
	
	public static void useDinnerbone(EntityPlayer p)
	{
		if(fireDinnerbone(p))
		{
            GlStateManager.translate(0.0F, p.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		}
	}

}
