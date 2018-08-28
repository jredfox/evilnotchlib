package com.EvilNotch.lib.minecraft.network;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.network.packets.PacketClipBoard;
import com.EvilNotch.lib.minecraft.network.packets.PacketRequestSeed;
import com.EvilNotch.lib.minecraft.network.packets.PacketSeed;
import com.EvilNotch.lib.minecraft.network.packets.PacketUUID;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawHead;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawOffset;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawPitch;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketClipBoardHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketRequestSeedHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketSeedHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketUUIDHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketYawHeadHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketYawOffsetHandler;
import com.EvilNotch.lib.minecraft.network.packets.handlers.PacketYawPitchHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetWorkHandler {
	
	public static NetworkWrapper INSTANCE;
	public static int networkid = 0;
	
	public static void init()
	{
		INSTANCE = new NetworkWrapper(MainJava.MODID);
		
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
	public static void registerMessage(Class handler, Class packet, Side side)
	{
		INSTANCE.registerMessage(handler, packet,networkid++, side);
	}
	public static class NetworkWrapper extends SimpleNetworkWrapper
	{
		public NetworkWrapper(String channelName) 
		{
			super(channelName);
		}
		
		public void sendToTrackingAndPlayer(IMessage msg,EntityPlayerMP player)
		{
			this.sendTo(msg, player);
			this.sendToTracking(msg, player);
		}
		
		public void sendToTracking(IMessage msg,EntityPlayerMP player)
		{
            for(EntityPlayer p : player.getServerWorld().getEntityTracker().getTrackingPlayers(player) )
            {
            	this.sendTo(msg, (EntityPlayerMP) p);
            }
		}
		
	}
}