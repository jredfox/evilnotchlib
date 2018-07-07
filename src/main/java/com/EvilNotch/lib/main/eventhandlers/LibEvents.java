package com.EvilNotch.lib.main.eventhandlers;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.PointId;
import com.EvilNotch.lib.util.number.IntObj;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
	
	 public static int mTick = 0;
	 public static final List<String> msgs = new ArrayList();
	 @SubscribeEvent
	 public void mTick(ServerTickEvent e)
	 {
		 if(e.phase != Phase.END || msgs.isEmpty())
			 return;
		 Set<String> toRemove = new HashSet();
		 for(String msg : msgs)
		 {
			MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
			for(EntityPlayerMP p : mcServer.getPlayerList().getPlayers())
			{
				p.sendMessage(new TextComponentString(msg));
			}
			toRemove.add(msg);
		 }
		 for(String s : toRemove)
			 msgs.remove(s);
	 }
	 
		/**
		 * entity player connection to point id(ticks existed, max tick count,String msg)
		 * the connection is kept even on respawn so there is no glitching this kicker
		 */
		public static HashMap<NetHandlerPlayServer,PointId> kicker = new HashMap();
		public static boolean isKickerIterating = false;
		@SubscribeEvent
		public void kick(TickEvent.ServerTickEvent e)
		{
			if(e.phase != Phase.END || kicker.isEmpty())
				return;

			Iterator<Map.Entry<NetHandlerPlayServer,PointId> > it = kicker.entrySet().iterator();
			while(it.hasNext())
			{
				isKickerIterating = true;
				Map.Entry<NetHandlerPlayServer,PointId> pair = it.next();
				NetHandlerPlayServer connection = pair.getKey();
				PointId point = pair.getValue();
				if(point.getX() >= point.getY())
				{
					it.remove();
					EntityUtil.disconnectPlayer(connection.player,new TextComponentString(point.id));
				}
			}
			isKickerIterating = false;
			
			for(PointId p : kicker.values())
				p.setLocation(p.getX() + 1,p.getY());
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
		 
		 if(cachedEnts || !Config.debug)
			 return;
		 MainJava.worldServer = w;
		 
		 System.out.println("Attempting to Repair Cache From Re-Instantiating Broken Entities");
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist,w);
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist_nbt,w);
		 EntityUtil.cacheEnts(EntityUtil.ent_blacklist_commandsender,w);
		 
		 cachedEnts = true;
	 }
}
