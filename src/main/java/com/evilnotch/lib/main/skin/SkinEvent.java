package com.evilnotch.lib.main.skin;

import javax.annotation.Nullable;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinEvent extends Event {
	
	/**
	 * Fires On The Server When a GameProfile Get's Patched from the UUIDPatcher V2 at login or when PacketSkinChange Fires
	 * Please Don't Download Any Skins Directly as it's on the Main Server Thread
	 * @author jredfox
	 */
	public static class GameProfileEvent extends Event
	{
		/**
		 * The Skin We are replacing the GameProfile with if {@link SkinEntry#isEmpty} it will not get applied
		 */
		public SkinEntry skin;
		/**
		 * The GameProfile's Properties the {@link #skin} hasn't gotten applied yet
		 */
		public GameProfile profile;
		/**
		 * The Skin of the GameProfile use {@link GameProfile#getOriginalSkin()} to get it
		 */
		private SkinEntry orgSkin;
		/**
		 * Is This GameProfileEvent Triggered by a Login
		 */
		public boolean login;
		
		public GameProfileEvent(GameProfile profile, String payload, boolean login)
		{
			this(profile, payload);
			this.login = login;
		}
		
		public GameProfileEvent(GameProfile profile, String payload)
		{
			this.profile = profile;
			this.skin = SkinEntry.fromPayload(profile.getId().toString(), profile.getName(), payload);
		}
		
		/**
		 * Gets the Current Profile's Skin
		 * NOTE: Editing it won't change the skin use {@link #skin} for this
		 */
		public SkinEntry getOriginalSkin()
		{
			if(this.orgSkin == null)
				this.orgSkin = SkinEntry.fromPayload(profile.getId().toString(), profile.getName(), SkinCache.getEncode(profile.getProperties()));
			return this.orgSkin;
		}

		/**
		 * Syncs the SkinEntry with the actual GameProfile's base64 payload in the properties
		 */
		public void update()
		{
			//Only patch UUID and Username when the skin is empty
			if(this.skin.isEmpty)
			{
				SkinCache.setUUIDEncode(this.profile);
				return;
			}
			
			//if skin is default or empty assign it to a default skin
			if(SkinCache.isSkinEmpty(this.skin.skin) || this.skin.skin.equals("http://textures.minecraft.net/texture/$null"))
			{
				boolean isAlex = this.skin.model.equals("slim") || this.skin.model.isEmpty() && PlayerUtil.isAlex(profile.getId());
				this.skin.skin = "http://textures.minecraft.net/texture/" + (isAlex ? "$alex" : "$steve");
				this.skin.model = isAlex ? "slim" : "";
			}
			
			SkinCache.setEncode(this.profile.getProperties(), this.skin);
		}
	}
	
	public static class Merge extends Event
	{
		public JSONObject org;
		public JSONObject json;
		
		public Merge(JSONObject o, JSONObject j)
		{
			this.org =  o.getSafeJSONObject("textures");
			this.json = j.getSafeJSONObject("textures");
		}
	}
	
	/**
	 * Fires on the SkinCache Downloading Thread right before the SkinCache Downloads a Skin That Gets Selected
	 */
	public static class User extends SkinEvent
	{
		public final String org_username;
		public String       username;
		
		public User(String username)
		{
			username = username.toLowerCase();
			this.org_username = username;
			this.username = username;
		}
		
		public static String fire(String s) 
		{
			User cap = new User(s);
			MinecraftForge.EVENT_BUS.post(cap);
			return cap.username.toLowerCase();
		}
	}
	
	/**
	 * Fires on the SkinCache Downloading Thread Right before the Skin Is Selected
	 * Use this to update capes and skin urls and override the player model (slim or default). 
	 * Call {@link SkinCache#INSTANCE#getOrDownload(String)} to get a SkinEntry during this event
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
			Capability cap = new Capability(s.copy());
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
	
	@SideOnly(Side.CLIENT)
	public static class DinnerboneTab extends SkinEvent
	{
		public boolean dinnerbone;
		@Nullable
		public EntityPlayer player;
		public NetworkPlayerInfo info;
		
		public DinnerboneTab(EntityPlayer p, NetworkPlayerInfo info)
		{
			this.player = p;
			this.info = info;
		}
	}
	
	/**
	 * Fires the Mouse Ears and Respects the Player's Invisibility
	 */
	public static boolean fireMouse(EntityPlayer player)
	{
		if(player.isInvisible())
			return false;
		
		SkinEvent.Mouse event = new SkinEvent.Mouse(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.ears;
	}

	public static boolean fireDinnerbone(Entity e)
	{
		if(!(e instanceof EntityPlayer))
			return false;
		
		SkinEvent.Dinnerbone event = new SkinEvent.Dinnerbone((EntityPlayer) e);
		MinecraftForge.EVENT_BUS.post(event);
		return event.dinnerbone;
	}

	@SideOnly(Side.CLIENT)
	public static boolean fireDinnerbone(@Nullable EntityPlayer p, NetworkPlayerInfo info) 
	{
		SkinEvent.DinnerboneTab event = new SkinEvent.DinnerboneTab(p, info);
		MinecraftForge.EVENT_BUS.post(event);
		return event.dinnerbone;
	}

}