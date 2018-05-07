package com.EvilNotch.lib.main.eventhandlers;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.EnumChatFormatting;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.util.PointId;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TickHandler {
	
	@SubscribeEvent
	public void singlePlayerFix(PlayerEvent.LoadFromFile e)
	{
		/*
		if(!Config.inventoryFixer)
			return;
		EntityPlayer player = e.getEntityPlayer();
		NBTTagCompound lvl = player.getServer().worlds[0].getWorldInfo().getPlayerNBTTagCompound();
		String owner = player.getServer().getServerOwner();
		
		LibEvents.playerDataDir = e.getPlayerDirectory();
		LibEvents.playerDataNames = new File(LibEvents.playerDataDir,"names");
		LibEvents.playerDataNames.mkdir();
		
		if(lvl != null && player.getName().equals(owner) )
		{
			System.out.println("Scanning if EntitySP uuid:");
			String lvl_uuid = new UUID(lvl.getLong("UUIDMost"),lvl.getLong("UUIDLeast")).toString();
			String actual_uuid = player.getUniqueID().toString();
			
			if(!lvl_uuid.equals(actual_uuid))
			{
				System.out.println("Original Owner Not Here Resseting Owner\nUUID Error level.dat:" + lvl_uuid + " actual:" + actual_uuid);
				NBTTagCompound actualNBT = EntityUtil.getPlayerFileNBT(actual_uuid, true);
				if(actualNBT != null)
					player.readFromNBT(actualNBT);
				else{
					System.out.println("new player:" + player.getName());
				}
			}
		}*/
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
	public void inventoryFixer(TickEvent.PlayerTickEvent e)
	{
		if(e.phase != Phase.END || e.player.world.isRemote || !Config.inventoryFixer)
			return;

		if(count < maxTick)
		{
			return;
		}
		else
			count = 0;
		
		EntityPlayerMP p = (EntityPlayerMP) e.player;
		try
		{
			String pname = p.getName();
			if(!players.containsKey(pname))
			{
				File file = EntityUtil.getPlayerFile(p, false);
				NBTTagCompound nbt = EntityUtil.getPlayerFileNBT(pname,false);
				players.put(pname, "" + nbt.getString("uuid") );
			}
			
			String compare = p.getUniqueID().toString();
			String cached = players.get(pname);
			
			if(!compare.equals(cached))
			{
				System.out.println("UUID Change Detected Patching Player:\"" + pname + "\"");
				File toUpdate = EntityUtil.getPlayerFile(p, true);
				
				NBTTagCompound proper = EntityUtil.getPlayerFileNBT(cached.toString(),true);
				proper.setLong("UUIDLeast", p.getUniqueID().getLeastSignificantBits() );//reformat uuids to match current one
				proper.setLong("UUIDMost", p.getUniqueID().getMostSignificantBits() );
				EntityUtil.updatePlayerFile(toUpdate, proper);//find actual uuid then update it

				int traveldim = proper.getInteger("Dimension");
				NBTTagList list = proper.getTagList("Pos", 6);
				
				//supports cross dimensional teleporting
				p.dismountRidingEntity();
				EntityUtil.telePortEntity(p, p.getServer(), list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2), NBTUtil.getRotationYaw(proper), NBTUtil.getRotationPitch(proper), traveldim);
				p.readFromNBT(proper);//force update everything
				
				//update playername file so it doesn't constantly update it the first time it changes
				File name = EntityUtil.getPlayerFile(p, false);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("uuid", p.getUniqueID().toString());
				EntityUtil.updatePlayerFile(name,nbt);
				players.put(pname, p.getUniqueID().toString());//update hashmap
				
				EntityUtil.printChat(p, EnumChatFormatting.GOLD, EnumChatFormatting.DARK_RED, "UUID Change Dedected For Player Disconnecting");
                EntityUtil.kickPlayer(p,40,EnumChatFormatting.AQUA + "UUID Change Detected Relog For Syncing " + EnumChatFormatting.YELLOW + "Forge Capabilities");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
		if(count < maxTick)
			count++;
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
				connection.disconnect(new TextComponentString(point.id));
			}
		}
		isKickerIterating = false;
		
		for(PointId p : kicker.values())
			p.setLocation(p.getX() + 1,p.getY());
	}
	@SubscribeEvent
	public void login(PlayerLoggedInEvent e)
	{
		if(e.player.world.isRemote)
			return;
		File file = EntityUtil.getPlayerFile(e.player, false);//updates player file synced to uuid on login
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
}
