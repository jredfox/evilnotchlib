package com.evilnotch.lib.main.eventhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.event.EventCanceler;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LibEvents {
	
	 @SubscribeEvent
	 public void tickServer(ServerTickEvent e)
	 {
		 TickRegistry.tickServer(e.phase);
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
		 
		 if(side == side.CLIENT)
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
	 
	public static boolean canSpawnClient = true;
	public static boolean canSpawnServer = true;
		
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void stopSpawning(EntityJoinWorldEvent event)
	{
		World world = event.getWorld();
		Entity entity = event.getEntity();
		
		if(!isCurrentThread(world) || entity == null)
		{
			return;
		}
			
		if(world.isRemote && !canSpawnClient)
		{
			event.setCanceled(true);
		}
		else if(!world.isRemote && !canSpawnServer)
		{
			event.setCanceled(true);
		}
	}
		
	public static boolean isCurrentThread(World w)
	{
		if(w.isRemote)
		{
			return ClientProxy.isCurrentThread();
		}
		else
		{
			return Thread.currentThread() == LoaderMain.serverThread;
		}
	}
	
	public static void setSpawn(World world, boolean value)
	{
		if(world.isRemote)
		{
			canSpawnClient = value;
		}
		else
		{
			canSpawnServer = value;
		}
	}
	
	public static boolean getSpawn(World world)
	{
		return world.isRemote ? canSpawnClient : canSpawnServer;
	}
}
