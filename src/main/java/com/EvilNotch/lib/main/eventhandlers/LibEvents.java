package com.EvilNotch.lib.main.eventhandlers;

import java.io.File;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LibEvents {
	public static boolean cachedEnts = false;
	public static File worlDir = null;
	public static File playerDataNames = null;
	public static File playerDataDir = null;
	public static File playerStatsDir = null;
	public static File playerAdvancedmentsDir = null;
	
	/**
	 * Attempt to re-instantiate the entity caches for broken entities when the world is no longer fake
	 */
	 @SubscribeEvent
	 public void worldload(WorldEvent.Load e)
	 {
		 World w = e.getWorld();
		 if(w.isRemote)
			 return;
		
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
