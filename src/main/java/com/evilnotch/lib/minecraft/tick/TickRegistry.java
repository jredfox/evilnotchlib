package com.evilnotch.lib.minecraft.tick;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

public class TickRegistry {
	
	public static List<ITick> serverTick = new ArrayList();
	public static List<ITick> clientTick = new ArrayList();
	
	public static int tickCountServer;
	public static int tickCountClient;
	
	public static void tickClient(TickEvent.Phase phase)
	{
		for(ITick t : clientTick)
			if(phase == t.getPhase())
				t.tick();
		
		if(phase == Phase.END)
		{
			tickCountClient++;
			if(tickCountClient == Integer.MAX_VALUE)
				tickCountClient = 0;
		}
	}
	
	public static void tickServer(TickEvent.Phase phase)
	{
		for(ITick t : serverTick)
			if(phase == t.getPhase())
				t.tick();
		
		if(phase == Phase.END)
		{
			tickCountServer++;
			if(tickCountServer == Integer.MAX_VALUE)
				tickCountServer = 0;
		}
	}
	
	public static void register(ITick tick, Side side)
	{
		if(side == Side.CLIENT) 
		{
			clientTick.add(tick);
		}
		else
		{
			serverTick.add(tick);
		}
	}
	
	/**
	 * remove a tick handler that may be broken from another mod or for an override
	 */
	public static void remove(Class<? extends ITick> clazz, Side side)
	{
		if(side == Side.CLIENT)
		{
			remove(clazz, clientTick);
		}
		else
		{
			remove(clazz, serverTick);
		}
	}
	
	private static void remove(Class<? extends ITick> clazz, List<ITick> list) 
	{
		for(ITick tick : list)
		{
			if(tick.getClass().equals(clazz))
			{
				list.remove(tick);
				return;
			}
		}
	}

	public static void garbageCollectServer()
	{
		for(ITick t : serverTick)
			t.garbageCollect();
	}
	
	public static void garbageCollectClient()
	{
		for(ITick t : clientTick)
			t.garbageCollect();
	}
	
	public static boolean isRightTickServer(int ticks)
	{
		return tickCountServer % ticks == 0;
	}
	
	public static boolean isRightTickClient(int ticks)
	{
		return tickCountClient % ticks == 0;
	}
}
