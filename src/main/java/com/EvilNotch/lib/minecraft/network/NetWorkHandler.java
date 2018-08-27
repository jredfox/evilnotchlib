package com.EvilNotch.lib.minecraft.network;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.network.packets.PacketClipBoard;
import com.EvilNotch.lib.minecraft.network.packets.PacketRequestSeed;
import com.EvilNotch.lib.minecraft.network.packets.PacketSeed;
import com.EvilNotch.lib.minecraft.network.packets.PacketUUID;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawOffset;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketClipBoardHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketRequestSeedHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketSeedHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketUUIDHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketYawOffsetHandler;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetWorkHandler {
	
	public static SimpleNetworkWrapper INSTANCE;
	public static int networkid = 0;
	
	public static void init()
	{
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MainJava.MODID);
		
		//to client
		registerMessage(PacketUUIDHandler.class, PacketUUID.class, Side.CLIENT);
		registerMessage(PacketClipBoardHandler.class, PacketClipBoard.class, Side.CLIENT);
		registerMessage(PacketSeedHandler.class, PacketSeed.class, Side.CLIENT);
		registerMessage(PacketYawOffsetHandler.class, PacketYawOffset.class, Side.CLIENT);
		
		//to server
		registerMessage(PacketRequestSeedHandler.class, PacketRequestSeed.class, Side.SERVER);
	}
	public static void registerMessage(Class handler, Class packet, Side side)
	{
		INSTANCE.registerMessage(handler, packet,networkid++, side);
	}
}