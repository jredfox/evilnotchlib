package com.evilnotch.lib.minecraft.tick;

import java.util.ArrayList;
import java.util.List;

public class TickReg {
	
	public static List<ITick> serverTick = new ArrayList();
	public static List<ITick> clientTick = new ArrayList();
	
	public static int tickCountServer;
	public static int tickCountClient;
	
	public static void tickClient()
	{
		for(ITick t : clientTick)
			t.tick();
		tickCountClient++;
		if(tickCountClient == Integer.MAX_VALUE)
			tickCountClient = 0;
	}
	
	public static void tickServer()
	{
		for(ITick t : serverTick)
			t.tick();
		tickCountServer++;
		if(tickCountServer == Integer.MAX_VALUE)
			tickCountServer = 0;
	}
	
	public static void regServer(ITick server)
	{
		serverTick.add(server);
	}
	
	public static void regClient(ITick c)
	{
		clientTick.add(c);
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
