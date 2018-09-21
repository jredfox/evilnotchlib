package com.evilnotch.lib.minecraft.content.tick;

import java.util.ArrayList;
import java.util.List;

public class TickReg {
	
	public static List<ITickServer> serverTick = new ArrayList();
	public static List<ITickClient> clientTick = new ArrayList();
	
	public static int tickCountServer;
	public static int tickCountClient;
	
	public static void tickClient(){
		for(ITick t : clientTick)
			t.tick();
		tickCountClient++;
		if(tickCountClient == Integer.MAX_VALUE)
			tickCountClient = 0;
	}
	
	public static void tickServer(){
		for(ITick t : serverTick)
			t.tick();
		tickCountServer++;
		if(tickCountServer == Integer.MAX_VALUE)
			tickCountServer = 0;
	}
	public static void regServer(ITickServer server)
	{
		serverTick.add(server);
	}
	public static void regClient(ITickClient c)
	{
		clientTick.add(c);
	}
	public static void garbageCollectServer()
	{
		for(ITickServer t : serverTick)
			t.garbageCollect();
	}
	public static void garbageCollectClient()
	{
		for(ITickClient t : clientTick)
			t.garbageCollect();
	}
	public static boolean isRightCountServer(int ticks)
	{
		return tickCountServer % ticks == 0;
	}
	public static boolean isRightCountClient(int ticks)
	{
		return tickCountClient % ticks == 0;
	}
}
