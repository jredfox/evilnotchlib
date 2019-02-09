package com.evilnotch.lib.main.eventhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evilnotch.lib.minecraft.content.tick.TickReg;
import com.evilnotch.lib.minecraft.event.EventCanceler;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.simple.PairObj;
import com.evilnotch.lib.util.simple.PointId;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LibEvents {
	
	 @SubscribeEvent
	 public void tickServer(ServerTickEvent e)
	 {
		 if(e.phase != Phase.END)
			 return;
		 
		 TickReg.tickServer();
	 }
	 
	 public static EventCanceler cancelerClient = null;
	 public static EventCanceler cancelerServer = null;
	 @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	 public void canceler(Event e)
	 {
		 Side side = FMLCommonHandler.instance().getEffectiveSide();
		 if(cancelerClient != null && cancelerClient.toIgnore != e && e.getClass().equals(cancelerClient.clazz) && Side.CLIENT == side)
		 {
			 e.setCanceled(cancelerClient.setIsCanceled);
			 cancelerClient = null;
		 }
		 if(cancelerServer != null && cancelerServer.toIgnore != e && e.getClass().equals(cancelerServer.clazz) && Side.SERVER == side)
		 {
			 e.setCanceled(cancelerServer.setIsCanceled);
			 cancelerServer = null;
		 }
	 }
	
	/**
	 * Attempt to re-instantiate the entity caches for broken entities when the world is no longer fake
	 */
	 public static boolean cachedEnts = false;
	 @SubscribeEvent
	 public void worldload(WorldEvent.Load e)
	 {
		 World ew = e.getWorld();
		 if(!EntityUtil.cached || ew.isRemote || cachedEnts || EntityUtil.ent_blacklist.isEmpty() && EntityUtil.ent_blacklist_nbt.isEmpty() && EntityUtil.ent_blacklist_nbt.isEmpty())
			 return;
		 cachedEnts = true;
		 World w = e.getWorld();
		 System.out.println("Attempting to Repair Cache From Re-Instantiating Broken Entities. This gives modders debug onWorldLoad if the exception still occurs");
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist,w);
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist_nbt,w);
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist_commandsender,w);
	 }
}
