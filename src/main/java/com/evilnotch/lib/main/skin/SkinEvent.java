package com.evilnotch.lib.main.skin;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SkinEvent extends Event {
	
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
	 * Fires on the SkinCache Downloading thread use with CAUTION
	 * Use this to update capes and skin urls. Call {@link SkinCache#INSTANCE#getSkinEntry(String cap)} and if Empty Call {@link SkinCache#INSTANCE#downloadSkin(String, SkinEntry)} Directly to get another player's cape or skin
	 * Set {@link Capability#skin} to change the SkinEntry for the SkinCache Downloading Thread
	 */
	public static class Capability extends SkinEvent
	{
		public final SkinEntry org_skin;
		public SkinEntry skin;
		
		public Capability(SkinEntry s)
		{
			this.org_skin = s; 
			this.skin = s;
		}

		public static SkinEntry fire(SkinEntry s) 
		{
			Capability cap = new Capability(s);
			MinecraftForge.EVENT_BUS.post(cap);
			return cap.skin;
		}
	}

}
