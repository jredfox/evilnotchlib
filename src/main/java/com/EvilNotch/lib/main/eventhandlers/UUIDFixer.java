package com.EvilNotch.lib.main.eventhandlers;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.EnumChatFormatting;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.events.PlayerDataFixEvent;
import com.EvilNotch.lib.util.PointId;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class UUIDFixer {
	
	public static enum Types {
		UUIDFIX(),
		UUIDLEVELDAT();
	}
	
	/**
	 * initial check for uuid fixer
	 */
	@SubscribeEvent
	public void playerDataFix(PlayerEvent.LoadFromFile e)
	{
		System.out.print("player is reading from file thread no sleep anymore zzzzzzzzzzzzzzzzzzzzzzzzz:\n");
		EntityPlayerMP player = (EntityPlayerMP) e.getEntityPlayer();
		File file = EntityUtil.getPlayerFileSafley(player, false);//updates player file synced to uuid on login
		
		String pname = player.getName();
		NBTTagCompound nbt = EntityUtil.getPlayerFileNBT(pname,player,false);
		
		String cached = nbt.getString("uuid");
		String compare = player.getUniqueID().toString();
		
		players.put(pname,cached);//cache check for player tick
		
		if(!compare.equals(cached) )
		{
			patchPlayerData(player,cached,compare,false,true);
			return;
		}
		
		//client world swapping world's to another player fix
		NBTTagCompound lvl = player.getServer().worlds[0].getWorldInfo().getPlayerNBTTagCompound();
		if(lvl != null && EntityUtil.isPlayerOwner(player) && !MainJava.isDeObfuscated)
		{
			String compare2 = new UUID(lvl.getLong("UUIDMost"),lvl.getLong("UUIDLeast")).toString();
			
			if(!compare2.equals(cached))
			{
				System.out.println("fired from level.dat but,is looking for another player:\ncached:" + cached + "\nlvl.dat:" + compare2);
				
				NBTTagCompound proper = null;
				File tst = new File(LibEvents.playerDataDir,cached + ".dat");
				
				if(tst.exists())
					proper = EntityUtil.getPlayerFileNBT(cached, player, true);
				
				if(proper == null)
				{
					System.out.println("unable to find playerdata file reading from blank playerdata:" + player.getName());
					
					// it's not intended for a player to always be swaping when transfering client world to new user
					if(!Config.playerOwnerAlwaysFix)
						return;
					proper = EntityUtil.getBlankPlayerData(player);
				}
				
				player.readFromNBT(proper);//notice doesn't take out uuid vanilla hotfix
				updateLevelDatPlayer(proper);//doesn't update regular player file since the nbt is equal to that
				
				PlayerDataFixEvent event = new PlayerDataFixEvent(player,cached,compare2,UUIDFixer.Types.UUIDLEVELDAT);
				MinecraftForge.EVENT_BUS.post(event);//fire fix event so other mods can sync when playerdata uuidfixer detects uuid has changed
			}
		}
	}

	@SubscribeEvent
	public void playerDataLogin(PlayerLoggedInEvent e)
	{
		if(e.player.world.isRemote)
			return;
		
		System.out.println("loging inzzzzzzzzzzzzzzzzzzzzzzzzz:");
		String compare = e.player.getUniqueID().toString();
		String cached = players.get(e.player.getName() );
		
		if(!compare.equals(cached) )
		{
			patchPlayerData((EntityPlayerMP) e.player,cached,compare,true,false);
		}
	}
	
	public static void patchPlayerData(EntityPlayerMP p, String cached, String compare, boolean requiresRestart,boolean readFromFile) 
	{
		//instantiate error checking here
		File tst = new File(LibEvents.playerDataDir,compare + ".dat");
		File tst2 = new File(LibEvents.playerDataDir,cached + ".dat");
		if(!tst.exists() )
		{
			MainJava.logger.info("[UUIDFIX/ERR] Unable to find current playerdata creating new one:" + compare + ".dat");
			EntityUtil.getPlayerFileSafley(p, true);
		}
		if(!tst2.exists() )
		{
			MainJava.logger.error("[UUIDFIX/ERR] Unable to patch playerdata as file doesn't exist:" + cached + ".dat");
			return;
		}
		
		String pname = p.getName();
		System.out.print("UUID Change Detected Patching Player:\"" + pname + "\"\n");
		File toUpdate = EntityUtil.getPlayerFile(p, true);
	
		NBTTagCompound proper = EntityUtil.getPlayerFileNBT(cached,p,true);
		proper.setLong("UUIDLeast", p.getUniqueID().getLeastSignificantBits() );//reformat uuids to match current one
		proper.setLong("UUIDMost", p.getUniqueID().getMostSignificantBits() );
		EntityUtil.updatePlayerFile(toUpdate, proper);//for when vanilla doesn't do it itself when exceptions are thrown and cuaght
		
		//update level.dat file just in case mc doesn't do it
		if(EntityUtil.isPlayerOwner(p))
		{
			updateLevelDatPlayer(proper);
		}

		//supports cross dimensional teleporting
		if(!readFromFile)
		{
			int traveldim = proper.getInteger("Dimension");
			NBTTagList list = proper.getTagList("Pos", 6);
			EntityUtil.telePortEntity(p, p.getServer(), list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2), NBTUtil.getRotationYaw(proper), NBTUtil.getRotationPitch(proper), traveldim);
		}
		p.readFromNBT(proper);//force update everything
	
		//update playername file so it doesn't constantly update it the first time it changes
		File name = EntityUtil.getPlayerFile(p, false);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("uuid", compare);
		EntityUtil.updatePlayerFile(name,nbt);
		players.put(pname, compare);//update hashmap
	
		MainJava.logger.log(Level.INFO, "[UUIDFIX] copying over stats");
		File oldStats = new File(LibEvents.playerStatsDir,cached + ".json");
//		p.mcServer.getPlayerList().getPlayerAdvancements(p_192054_1_)
	
		MainJava.logger.log(Level.INFO, "[UUIDFIX] copying over advancedments");
		File oldAch = new File(LibEvents.playerAdvancedmentsDir,cached + ".json");
	
		PlayerDataFixEvent event = new PlayerDataFixEvent(p,cached,compare,UUIDFixer.Types.UUIDFIX);
		MinecraftForge.EVENT_BUS.post(event);//fire fix event so other mods can sync when playerdata uuidfixer detects uuid has changed
		
		if(requiresRestart)
		{
			EntityUtil.printChat(p, EnumChatFormatting.GOLD, EnumChatFormatting.DARK_RED, "UUID Change Dedected For Player Disconnecting");
			EntityUtil.kickPlayer(p,60,EnumChatFormatting.AQUA + "UUID Change Detected Relog For Syncing " + EnumChatFormatting.YELLOW + "Forge Capabilities");
		}
	}

	public static void updateLevelDatPlayer(NBTTagCompound proper) 
	{
		try
		{
			File f = new File(LibEvents.playerDataDir.getParentFile(),"level.dat");
			NBTTagCompound lvldat = NBTUtil.getFileNBT(f);
			NBTTagCompound toUpdateNBT = lvldat.getCompoundTag("Data");
			toUpdateNBT.setTag("Player", proper);//don't have to fix data since EntityUtil.getPlayerFileNBT() already does this and it's replacing the tag
		
			NBTUtil.updateNBTFile(f, lvldat);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	/**
	 * playername to uuid as string
	 */
	public static HashMap<String,String> players = new HashMap();

	public static int count = 0;
	public static final int maxTick = 20*6;
	public static boolean isFixerIterating = false;
	/**
	 * stop vanilla/forge bug of playerdata being randomly wiped
	 */
	@SubscribeEvent
	public void inventoryFixer(TickEvent.ServerTickEvent e)
	{
		if(e.phase != Phase.END)
			return;

		if(count < maxTick)
		{
			count++;
			return;
		}
		else
			count = 0;
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(server == null)
			return;
		PlayerList list = server.getPlayerList();
		
		Iterator<String> it = players.keySet().iterator();
		while(it.hasNext())
		{
			isFixerIterating = true;
			try
			{
				String pname = it.next();
				EntityPlayerMP p = list.getPlayerByUsername(pname);
				if(p == null)
					continue;
			
				String compare = p.getUniqueID().toString();
				String cached = players.get(pname);
				
				if(cached == null)
					continue;
			
				if(!compare.equals(cached))
				{
					patchPlayerData(p,cached,compare,true,false);
				}
				if(Config.playerDataFixOptimized)
					it.remove();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		isFixerIterating = false;
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
}
