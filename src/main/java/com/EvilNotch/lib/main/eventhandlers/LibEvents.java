package com.EvilNotch.lib.main.eventhandlers;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.SkinUpdater;
import com.EvilNotch.lib.minecraft.events.PlayerDataFixEvent;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.number.IntObj;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class LibEvents {
	public static boolean cachedEnts = false;
	public static File worlDir = null;
	public static File playerDataNames = null;
	public static File playerDataDir = null;
	public static File playerStatsDir = null;
	public static File playerAdvancedmentsDir = null;
	
	 /**
	  * for when mojang servers don't respond quick enough try again in 32 seconds
	  */
	 public static HashMap<String,IntObj> noSkins = new HashMap();
	 @SubscribeEvent
	 public void skinTick(ServerTickEvent e)
	 {
		 if(e.phase != Phase.END || noSkins.isEmpty())
			 return;
		 Set<String> toRemove = new HashSet();
		 Iterator<Map.Entry<String,IntObj>> it = noSkins.entrySet().iterator();
		 while(it.hasNext())
		 {
			 Map.Entry<String,IntObj> pair = it.next();
			 IntObj i = pair.getValue();
			 if(i.integer == 32*20)
			 {
				if(!JavaUtil.isOnline("api.mojang.com"))
				{
					toRemove.addAll(noSkins.keySet());
					System.out.println("server is offline will not try to re-instantiate new skins again");
					break;
				}
				 String name = pair.getKey();
				 EntityPlayer player = EntityUtil.getPlayer(name);
				 SkinUpdater.fireSkinEvent(player, true);
				 //if not reset add to the remove list
				 if(i.integer != 0)
					 toRemove.add(name);
			 }
			 else
				 i.integer++;
		 }
		 for(String n : toRemove)
		 {
			 noSkins.remove(n);
		 }
	 }
	 @SubscribeEvent
	 public void skinNo(PlayerLoggedOutEvent e)
	 {
		 noSkins.remove(e.player.getName());
	 }
	
	 @SubscribeEvent
	 public void playerData(PlayerDataFixEvent e)
	 {
		 if(e.type != UUIDFixer.Types.UUIDFIX)
			 return;
		 
		 String name = e.player.getName().toLowerCase();
		 SkinUpdater.removeUser(name);
		 
		 String newUUID = SkinUpdater.getUUID(name);
		 if(newUUID == null)
		 {
			 System.out.println("unable to fetch uuid from mojang:");
			 return;
		 }
		 SkinUpdater.uuids.put(name, newUUID);//strip the bars away from uuid to match mojangs api
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
