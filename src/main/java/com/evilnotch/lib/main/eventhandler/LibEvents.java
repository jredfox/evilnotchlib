package com.evilnotch.lib.main.eventhandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.skin.SkinEvent;
import com.evilnotch.lib.main.world.ListenerSync;
import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.event.EventCanceler;
import com.evilnotch.lib.minecraft.event.client.MessageEvent;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.crashy.Crashy;
import net.minecraftforge.fml.relauncher.Side;

public class LibEvents {
	
	@SubscribeEvent
	public void load(WorldEvent.Load e)
	{
		World w = e.getWorld();
		if(w == null)
			return;
		ListenerSync listener = new ListenerSync();
		w.removeEventListener(listener);//prevent duplicates
		w.addEventListener(listener);
	}
	
	 @SubscribeEvent
	 public void tickServer(ServerTickEvent e)
	 {
		 TickRegistry.tickServer(e.phase);
	 }
	 
	 @SubscribeEvent(priority = EventPriority.HIGHEST)
	 public void syncCaps(PlayerEvent.Clone event)
	 {
		 EntityPlayer original = event.getOriginal();
		 EntityPlayer player = event.getEntityPlayer();
		 CapContainer container = CapabilityRegistry.getCapContainer(original);
		 CapabilityRegistry.setCapContainer(player, container);
	 }
	 
	 public static final List<EventCanceler> cancelerClient = new ArrayList<EventCanceler>();
	 public static final List<EventCanceler> cancelerServer = new ArrayList<EventCanceler>();
	 @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	 public void canceler(Event e)
	 {
		 if(cancelerClient.isEmpty() && cancelerServer.isEmpty())
		 {
			 return;
		 }
		 
		 Side side = FMLCommonHandler.instance().getEffectiveSide();
		 
		 if(side == Side.CLIENT)
		 {
			 Iterator<EventCanceler> it = cancelerClient.iterator();
			 while(it.hasNext())
			 {
				 EventCanceler eventCanceler = it.next();
				 if(eventCanceler.toIgnore != e && e.getClass().equals(eventCanceler.clazz))
				 {
					 e.setCanceled(eventCanceler.setIsCanceled);
					 it.remove();
				 }
			 }
		 }
		 else
		 {
			 Iterator<EventCanceler> it = cancelerServer.iterator();
			 while(it.hasNext())
			 {
				 EventCanceler eventCanceler = it.next();
				 if(eventCanceler.toIgnore != e && e.getClass().equals(eventCanceler.clazz))
				 {
					 e.setCanceled(eventCanceler.setIsCanceled);
					 it.remove();
				 }
			 }
		 }
	 }
	 
	public static ThreadLocal<Boolean> disableSpawn = ThreadLocal.withInitial(() -> false);
	public static ThreadLocal<Boolean> disableSound = ThreadLocal.withInitial(() -> false);
	public static ThreadLocal<Boolean> disableMsg = ThreadLocal.withInitial(() -> false);
		
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void stopSpawning(EntityJoinWorldEvent event)
	{
		if(disableSpawn.get())
			event.setCanceled(true);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void stopMsg(MessageEvent event)
	{
		if(disableMsg.get())
			event.setCanceled(true);
	}
	
	public static void setMsg(boolean enabled)
	{
		disableMsg.set(!enabled);
	}

	public static void setSpawn(boolean enabled)
	{
		disableSpawn.set(!enabled);
	}

	public static void setSound(boolean enabled)
	{
		disableSound.set(!enabled);
	}
	
	public static void setMsgDisable(boolean disable)
	{
		disableMsg.set(disable);
	}

	public static void setSpawnDisable(boolean disable)
	{
		disableSpawn.set(disable);
	}

	public static void setSoundDisable(boolean disable)
	{
		disableSound.set(disable);
	}
	
	/**
	 * Clears skin data related to SkinEntry before the SkinCache#setEncode(GameProfile, SkinEntry); gets called
	 */
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void merge(SkinEvent.Merge e)
	{
		JSONObject j = e.org;
		for(MinecraftProfileTexture.Type t : MinecraftProfileTexture.Type.values())
		{
			String key = t.name();
			JSONObject rootSkin = j.getSafeJSONObject(key);
			rootSkin.remove("url");
			
			//Delete model metadata if there are additional tags leave them
			if(t == MinecraftProfileTexture.Type.SKIN)
			{
				JSONObject meta = rootSkin.getSafeJSONObject("metadata");
				meta.remove("model");
				if(meta.isEmpty())
					rootSkin.remove("metadata");
			}
			if(rootSkin.isEmpty())
				j.remove(key);
		}
	}
	
	private static final String[] dltags = new String[]{ "/", "/download", "/model", "/skin", "/cape", "/elytra"};
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void patchurl(SkinEvent.HashURLEvent e)
	{
		try
		{
			String surl = removeJunk(e.url).toLowerCase();
			for(int i = 0; i < dltags.length; i++)
			{
				String tag = dltags[i];
				if(surl.endsWith(tag))
				{
					surl = surl.substring(0, surl.lastIndexOf(tag));
					i = -1;//Reset the search
				}
			}
			e.url = e.url.substring(0, surl.length());//preserve original casing
			
			//inject skin domain if it's non mojang
			if(Config.skinHashDomain)
			{
				String host = new URL(e.url).getHost();
				if(!host.endsWith(".minecraft.net") && !host.endsWith(".mojang.com"))
				{
					int sep = e.url.lastIndexOf('/');
					e.url = e.url.substring(0, sep) + "/" + host.replace(".", "_") + "_" + e.url.substring(sep + 1, e.url.length());
					Crashy.displayCrash(e.orgURL + "\n" + e.url, false);
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	/**
	 * Removes & and ?
	 */
	public static String removeJunk(String url)
	{
		int app = url.indexOf('&');
		if(app != -1)
			url = url.substring(0, app);
		
		int que = url.indexOf('?');
		if(que != -1)
			url = url.substring(0, que);

		return url;
	}
}
