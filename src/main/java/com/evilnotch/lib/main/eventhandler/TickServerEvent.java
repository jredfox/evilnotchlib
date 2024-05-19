package com.evilnotch.lib.main.eventhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.tick.ITick;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.simple.PointId;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TickServerEvent implements ITick{

	public static int mTick = 0;
	public static final List<String> msgs = new ArrayList();
	/**
	 * entity player connection to point id(ticks existed, max tick count,String msg)
	 * the connection is kept even on respawn so there is no glitching this kicker
	*/
	public static HashMap<EntityPlayerMP,PointId> kicker = new HashMap();
	
	@Override
	public void tick() 
	{
		sendMsgs();
		kickPlayers();
	}


	private void kickPlayers() 
	{
		if(kicker.isEmpty())
			return;
		Iterator<Map.Entry<EntityPlayerMP,PointId> > it = kicker.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<EntityPlayerMP,PointId> pair = it.next();
			EntityPlayerMP player = pair.getKey();
			PointId point = pair.getValue();
			if(point.getX() >= point.getY())
			{
				it.remove();
				PlayerUtil.disconnectPlayer(player,new TextComponentString(point.id));
			}
		}
			
		for(PointId p : kicker.values())
			p.setLocation(p.getX() + 1,p.getY());
	}


	private void sendMsgs() 
	{
		if(msgs.isEmpty())
			return;
		Iterator<String> it_msg = msgs.iterator();
		while(it_msg.hasNext())
		{
			String msg = it_msg.next();
			MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
			for(EntityPlayerMP p : mcServer.getPlayerList().getPlayers())
			{
				p.sendMessage(new TextComponentString(msg));
			}
			it_msg.remove();
		}
	}

	@Override
	public void garbageCollect() 
	{
		msgs.clear();
		kicker.clear();
	}

	@Override
	public Phase getPhase() 
	{
		return Phase.END;
	}

}
