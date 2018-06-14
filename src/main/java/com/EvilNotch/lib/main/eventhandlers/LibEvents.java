package com.EvilNotch.lib.main.eventhandlers;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.SkinUpdater;
import com.EvilNotch.lib.minecraft.events.PlayerDataFixEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LibEvents {
	public static boolean cachedEnts = false;
	public static File worlDir = null;
	public static File playerDataNames = null;
	public static File playerDataDir = null;
	public static File playerStatsDir = null;
	public static File playerAdvancedmentsDir = null;
	
	 @SubscribeEvent
	 public void playerData(PlayerDataFixEvent e)
	 {
		 if(e.type != UUIDFixer.Types.UUIDFIX)
			 return;
		 SkinUpdater.uuids.put(e.player.getName().toLowerCase(), e.uuidNew);
	 }
	
	/**
	 * Attempt to re-instantiate the entity caches for broken entities when the world is no longer fake
	 */
	 @SubscribeEvent
	 public void worldload(WorldEvent.Load e)
	 {
		 World ew = e.getWorld();
		 if(ew.isRemote)
			 return;
		 
		 World w = DimensionManager.getWorld(0);
		 
		 //instantiate directories for core minecraft bug fixes
		 worlDir = w.getSaveHandler().getWorldDirectory();
		 playerDataNames = new File(worlDir,"playerdata/names");
		 playerDataNames.mkdirs();
		 playerDataDir = playerDataNames.getParentFile();
		 playerStatsDir = new File(playerDataDir.getParentFile(),"stats");
		 playerAdvancedmentsDir = new File(playerDataDir.getParentFile(),"advancements");
		 
		 if(cachedEnts)
			 return;
		 MainJava.worldServer = w;
		 
		 System.out.println("Attempting to Repair Cache From Re-Instantiating Broken Entities");
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist,w);
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist_nbt,w);
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist_commandsender,w);
		 
		 System.out.println("blacklistEnt:" + EntityUtil.ent_blacklist);
		 System.out.println("blacklistNBTEnt:" + EntityUtil.ent_blacklist_nbt);
		 System.out.println("blacklistCMDEnt:" + EntityUtil.ent_blacklist_commandsender);
		 cachedEnts = true;
	 }
}
