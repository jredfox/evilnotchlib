package com.evilnotch.lib.minecraft.client;

import java.util.HashMap;
import java.util.Map;

import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketRequestSeed;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class Seeds {
	
	public static Map<Integer,String> seeds = new HashMap();
	public static int permisionLevel = -1;
	public static final String denied = "Server Denied Seed Request";
	
	public static String getSeed(WorldClient world) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		boolean flag = false;
		int dim = world.provider.getDimension();
		
		if(!seeds.containsKey(dim))
		{
			seeds.put(dim,"pending...");
			NetWorkHandler.INSTANCE.sendToServer(new PacketRequestSeed(dim));
		}
		
		if(permisionLevel != mc.player.getPermissionLevel() && permisionLevel != -1)
		{
			flag = true;
		}
		
		permisionLevel = mc.player.getPermissionLevel();
		String seed = seeds.get(dim);
		
		if(flag && seed.equals(denied))
		{
			seeds.remove(dim);
			return getSeed(world);
		}
		return seed;
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
