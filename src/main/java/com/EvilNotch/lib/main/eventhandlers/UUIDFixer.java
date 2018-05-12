package com.EvilNotch.lib.main.eventhandlers;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.EnumChatFormatting;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.events.PlayerDataFixEvent;
import com.EvilNotch.lib.util.PointId;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
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
		NBTTagCompound nbt = EntityUtil.getPlayerFileNBT(pname,false);
		String cached = nbt.getString("uuid");
		
		String compare = player.getUniqueID().toString();
		String compare2 = player.getGameProfile().getId().toString();
		
		players.put(pname,cached);//cache check for player tick
		
		if(!compare.equals(cached) || !compare2.equals(cached) )
		{
			patchPlayerData(player,cached,compare,false,UUIDFixer.Types.UUIDFIX,true);
			return;
		}
		
		//player swap controll
		NBTTagCompound lvl = player.getServer().worlds[0].getWorldInfo().getPlayerNBTTagCompound();
		if(lvl != null && EntityUtil.isPlayerOwner(player) && !MainJava.isDeObfuscated)
		{
			String compare3 = new UUID(lvl.getLong("UUIDMost"),lvl.getLong("UUIDLeast")).toString();
			if(!compare3.equals(cached))
			{
				System.out.println("fired from level.dat but,is looking for another player");
				patchPlayerData(player,cached,compare,false,UUIDFixer.Types.UUIDLEVELDAT,true);
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
		String compare2 = e.player.getGameProfile().getId().toString();
		String cached = players.get(e.player.getName() );
		
		if(!compare.equals(cached) || !compare2.equals(cached) )
		{
			patchPlayerData((EntityPlayerMP) e.player,cached,compare,true,UUIDFixer.Types.UUIDFIX,false);
		}
	}
	
	public static void patchPlayerData(EntityPlayerMP p, String cached, String compare, boolean requiresRestart,UUIDFixer.Types type,boolean readFromFile) 
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
	
		NBTTagCompound proper = EntityUtil.getPlayerFileNBT(cached,true);
		proper.setLong("UUIDLeast", p.getUniqueID().getLeastSignificantBits() );//reformat uuids to match current one
		proper.setLong("UUIDMost", p.getUniqueID().getMostSignificantBits() );
		EntityUtil.updatePlayerFile(toUpdate, proper);//for when vanilla doesn't do it itself when exceptions are thrown and cuaght
		
		//update level.dat file just in case mc doesn't do it
		if(type == UUIDFixer.Types.UUIDLEVELDAT)
		{
			try
			{
				File f = new File(LibEvents.playerDataDir.getParentFile(),"level.dat");
				NBTTagCompound lvldat = NBTUtil.getFileNBT(f);
				NBTTagCompound toUpdateNBT = lvldat.getCompoundTag("Data");
				toUpdateNBT.setTag("Player", proper);//override existing data
			
				NBTUtil.updateNBTFile(f, lvldat);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		int traveldim = proper.getInteger("Dimension");
		NBTTagList list = proper.getTagList("Pos", 6);
	
		//supports cross dimensional teleporting
		p.dismountRidingEntity();
		if(!readFromFile)
		{
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
	
		PlayerDataFixEvent event = new PlayerDataFixEvent(p,cached,compare,type);
		MinecraftForge.EVENT_BUS.post(event);//fire fix event so other mods can sync when playerdata uuidfixer detects uuid has changed
		
		if(requiresRestart)
		{
			EntityUtil.printChat(p, EnumChatFormatting.GOLD, EnumChatFormatting.DARK_RED, "UUID Change Dedected For Player Disconnecting");
			EntityUtil.kickPlayer(p,60,EnumChatFormatting.AQUA + "UUID Change Detected Relog For Syncing " + EnumChatFormatting.YELLOW + "Forge Capabilities");
		}
	}

	/**
	 * playername to uuid as string
	 */
	public static HashMap<String,String> players = new HashMap();

	public static int count = 0;
	public static final int maxTick = 20*6;
	/**
	 * stop vanilla/forge bug of playerdata being randomly wiped
	 */
	@SubscribeEvent
	public void inventoryFixer(TickEvent.ServerTickEvent e)
	{
		if(e.phase != Phase.END || !Config.inventoryFixer)
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
		
		for(String pname : players.keySet())
		{
			try
			{
				EntityPlayerMP p = list.getPlayerByUsername(pname);
				if(p == null)
					continue;
			
				String compare = p.getUniqueID().toString();
				String cached = players.get(pname);
				
				if(cached == null)
					continue;
			
				if(!compare.equals(cached))
				{
					patchPlayerData(p,cached,compare,true,UUIDFixer.Types.UUIDFIX,false);
				}
				if(Config.playerDataFixOptimized)
					players.remove(pname);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void logout(PlayerLoggedOutEvent e)
	{
		if(e.player instanceof EntityPlayerMP)
		{
			String pname = e.player.getName();
			players.remove(pname);
			if(!isKickerIterating)
				kicker.remove( ((EntityPlayerMP)e.player).connection);
			
			if(EntityUtil.isPlayerOwner((EntityPlayerMP)e.player) )
				count = 0;//reset variable if integrated server owner decides to quit or view the main menu doesn't happen on dedicated
		}
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
		if(e.phase != Phase.END)
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
