package com.evilnotch.lib.main.skin;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.minecraft.client.CapeRenderer;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
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
				this.skin.model = isAlex ? "slim" : this.skin.model;
			}
			
			SkinCache.setEncode(this.profile.getProperties(), this.skin);
		}
	}
	
	/**
	 * Fires when {@link SkinCache#setEncode(com.mojang.authlib.properties.PropertyMap, SkinEntry)} and two skins are about to merge
	 * Use this to clear tags that are not always expected to be present in the merger
	 * @author jredfox
	 */
	public static class Merge extends Event
	{
		public JSONObject org;
		public JSONObject json;
		
		public Merge(JSONObject o, JSONObject j)
		{
			this.org =  o.getSafeJSONObject("textures");
			this.json = j.getSafeJSONObject("textures");
		}
		
		public static void fire(JSONObject prevJSON, JSONObject skinJSON)
		{
			MinecraftForge.EVENT_BUS.post(new SkinEvent.Merge(prevJSON, skinJSON));
		}
	}
	
	public static class HashURLEvent extends Event
	{
		public String orgURL;
		public String url;
		
		public HashURLEvent(String u)
		{
			u = JavaUtil.safeString(u);
			this.orgURL = u;
			this.url = u;
		}
		
		public static String fire(String u)
		{
			HashURLEvent event = new HashURLEvent(u);
			MinecraftForge.EVENT_BUS.post(event);
			return event.url != null ? event.url : event.orgURL;
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
	
	/**
	 * @SideOnly(SIDE.CLIENT) Actually using the annotation results in crashes on the class level for some odd reason
	 * @author jredfox
	 */
	public static class Mouse extends SkinEvent
	{
		/**
		 * Set this to true to Enable Dynamic Events rather then just metadata
		 */
		public static boolean enabled;
		
		public boolean ears;
		public EntityPlayer player;
		
		public Mouse(EntityPlayer p)
		{
			this.player = p;
		}
		
		/**
		 * Fires the Mouse Ears and Respects the Player's Invisibility
		 */
		public static boolean fire(EntityPlayer player)
		{
			if(!(player instanceof AbstractClientPlayer) || player.isInvisible())
				return false;
			
			if(enabled)
			{
				SkinEvent.Mouse event = new SkinEvent.Mouse(player);
				MinecraftForge.EVENT_BUS.post(event);
				return event.ears;
			}
			
			NetworkPlayerInfo info = ((AbstractClientPlayer) player).playerInfo;
			return ClientProxy.getMetaBoolean(info, "e");
		}
	}
	
	/**
	 * @SideOnly(SIDE.CLIENT) Actually using the annotation results in crashes on the class level for some odd reason
	 * @author jredfox
	 */
	public static class Dinnerbone extends SkinEvent
	{
		/**
		 * Set this to true to Enable Dynamic Events rather then just metadata
		 */
		public static boolean enabled;
		
		public boolean dinnerbone;
		public EntityPlayer player;
		
		public Dinnerbone(EntityPlayer p)
		{
			this.player = p;
		}
		
		public static boolean fire(Entity e)
		{
			if(!(e instanceof AbstractClientPlayer))
				return false;
			
			if(enabled)
			{
				SkinEvent.Dinnerbone event = new SkinEvent.Dinnerbone((EntityPlayer) e);
				MinecraftForge.EVENT_BUS.post(event);
				return event.dinnerbone;
			}
			
			NetworkPlayerInfo info = ((AbstractClientPlayer) e).playerInfo;
			return ClientProxy.getMetaBoolean(info, "d");
		}
	}
	
	/**
	 * @SideOnly(SIDE.CLIENT) Actually using the annotation results in crashes on the class level for some odd reason
	 * @author jredfox
	 */
	public static class DinnerboneTab extends SkinEvent
	{
		/**
		 * Set this to true to Enable Dynamic Events rather then just metadata
		 */
		public static boolean enabled;
		
		public boolean dinnerbone;
		@Nullable
		public EntityPlayer player;
		@SideOnly(Side.CLIENT)
		public NetworkPlayerInfo info;
		
		public DinnerboneTab(EntityPlayer p, NetworkPlayerInfo info)
		{
			this.player = p;
			this.info = info;
		}
		
		/**
		 * @SideOnly(SIDE.CLIENT) Actually using the annotation results in crashes level for some odd reason
		 * @author jredfox
		 */
		public static boolean fire(@Nullable EntityPlayer p, NetworkPlayerInfo info) 
		{
			if(enabled)
			{
				SkinEvent.DinnerboneTab event = new SkinEvent.DinnerboneTab(p, info);
				MinecraftForge.EVENT_BUS.post(event);
				return event.dinnerbone;
			}
			
			return ClientProxy.getMetaBoolean(info, "d");
		}
	}
	
	/**
	 * @SideOnly(SIDE.CLIENT) Actually using the annotation results in crashes on the class level for some odd reason even
	 * @author jredfox
	 */
	public static class CapeEnchant extends SkinEvent
	{
		/**
		 * When false uses the Skin's Metadata instead of dynamic events
		 */
		public static boolean enabled;
		
		/**
		 * When True Renders the Cape as Enchanted regardless of if player is wearing an Enchanted Armor
		 */
		public boolean renderEnchant;
		
		@SideOnly(Side.CLIENT)
		public RenderPlayer renderer;
		@SideOnly(Side.CLIENT)
		public AbstractClientPlayer player;
		public float partialTicks;
		public float scale;
		
		public CapeEnchant(RenderPlayer r, AbstractClientPlayer p, float pt, float s)
		{
			this.renderer = r;
			this.player = p;
			this.partialTicks = pt;
			this.scale = s;
		}
		
		public static boolean fire(RenderPlayer r, AbstractClientPlayer p, float pt, float s)
		{
			if(enabled)
			{
				CapeEnchant e = new CapeEnchant(r, p, pt, s);
				MinecraftForge.EVENT_BUS.post(e);
				return e.renderEnchant;
			}
			
			NetworkPlayerInfo info = p.playerInfo;
			return ClientProxy.getMetaBoolean(info, "cg", true) && p.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isItemEnchanted() || ClientProxy.getMetaBoolean(info, "cga");
		}
		
		public static void render(RenderPlayer r, AbstractClientPlayer p, float pt, float s)
		{
			if(fire(r, p, pt, s))
				CapeRenderer.renderEnchantedCape(r, p, r.getMainModel(), pt, s);
		}
		
		/**
		 * Mo' Bends Support and Custom Mod Support!
		 */
		public static void render(RenderPlayer r, AbstractClientPlayer p, Method render, Object model, float pt, float s)
	    {
			if(fire(r, p, pt, s))
				CapeRenderer.renderEnchant(r, p, render, model, pt, s);
	    }
	}

}