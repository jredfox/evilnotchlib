package com.evilnotch.lib.main.eventhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.evilnotch.lib.main.world.ListenerSync;
import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.event.EventCanceler;
import com.evilnotch.lib.minecraft.event.client.MessageEvent;
import com.evilnotch.lib.minecraft.tick.TickRegistry;

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
	 
	 @SubscribeEvent(priority = EventPriority.HIGH)
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
}
