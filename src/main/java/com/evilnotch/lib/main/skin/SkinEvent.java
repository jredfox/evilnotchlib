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
	 * Use this to update capes and skin urls. Call {@link SkinCache#INSTANCE#getOrDownload(String, boolean)}
	 */
	public static class Capability extends SkinEvent
	{
		public final SkinEntry org_skin;
		public SkinEntry skin;
		public boolean selected;
		
		public Capability(SkinEntry s, boolean selected)
		{
			this.org_skin = s; 
			this.skin = s;
			this.selected = selected;
		}

		public static SkinEntry fire(SkinEntry s, String user, boolean selected) 
		{
			//sync skin with username
			s = s.copy();
			s.user = user;
			
			Capability cap = new Capability(s, selected);
			MinecraftForge.EVENT_BUS.post(cap);
			return cap.skin;
		}
	}

}
