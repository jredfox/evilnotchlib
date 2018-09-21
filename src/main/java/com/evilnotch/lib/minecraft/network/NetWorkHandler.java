package com.evilnotch.lib.minecraft.network;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.network.packets.PacketClipBoard;
import com.evilnotch.lib.minecraft.network.packets.PacketRequestSeed;
import com.evilnotch.lib.minecraft.network.packets.PacketSeed;
import com.evilnotch.lib.minecraft.network.packets.PacketUUID;
import com.evilnotch.lib.minecraft.network.packets.PacketYawHead;
import com.evilnotch.lib.minecraft.network.packets.PacketYawOffset;
import com.evilnotch.lib.minecraft.network.packets.PacketYawPitch;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketClipBoardHandler;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketRequestSeedHandler;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketSeedHandler;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketUUIDHandler;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketYawHeadHandler;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketYawOffsetHandler;
import com.evilnotch.lib.minecraft.network.packets.handlers.PacketYawPitchHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetWorkHandler {
	
	public static NetWorkWrapper INSTANCE;
	public static int networkid = 0;
	
	public static void init()
	{
		INSTANCE = new NetWorkWrapper(MainJava.MODID);
		
		//to client
		registerMessage(PacketUUIDHandler.class, PacketUUID.class, Side.CLIENT);
		registerMessage(PacketClipBoardHandler.class, PacketClipBoard.class, Side.CLIENT);
		registerMessage(PacketSeedHandler.class, PacketSeed.class, Side.CLIENT);
		registerMessage(PacketYawOffsetHandler.class, PacketYawOffset.class, Side.CLIENT);
		registerMessage(PacketYawPitchHandler.class, PacketYawPitch.class, Side.CLIENT);
		registerMessage(PacketYawHeadHandler.class, PacketYawHead.class, Side.CLIENT);
		
		//to server
		registerMessage(PacketRequestSeedHandler.class, PacketRequestSeed.class, Side.SERVER);
	}
	protected static void registerMessage(Class handler, Class packet, Side side)
	{
		INSTANCE.registerMessage(handler, packet,networkid++, side);
	}
}