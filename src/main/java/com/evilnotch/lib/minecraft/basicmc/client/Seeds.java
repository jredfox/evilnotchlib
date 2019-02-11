package com.evilnotch.lib.minecraft.basicmc.client;

import java.util.HashMap;
import java.util.Map;

import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketRequestSeed;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.client.multiplayer.WorldClient;

public class Seeds {
	
	public static Map<Integer,String> seeds = new HashMap();
	
	public static String getSeed(WorldClient world) 
	{
		int dim = world.provider.getDimension();
		if(!seeds.containsKey(dim))
		{
			seeds.put(dim,"pending...");
			NetWorkHandler.INSTANCE.sendToServer(new PacketRequestSeed(dim));
		}
		return seeds.get(dim);
	}
	/**
	 * set the proper seed to the dimension
	 */
	public static void setSeed(int dim, long seed) 
	{
		seeds.put(dim, "" + seed);
	}
	/**
	 * used for disabling seeds
	 */
	public static void setSeed(int dim, String msg)
	{
		seeds.put(dim, msg);
	}
	
	public static void clearSeeds()
	{
		seeds.clear();
	}


}
