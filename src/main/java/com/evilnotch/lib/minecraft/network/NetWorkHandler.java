package com.evilnotch.lib.minecraft.network;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.network.packet.PacketClientHooks;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.minecraft.network.packet.PacketHand;
import com.evilnotch.lib.minecraft.network.packet.PacketPickBlock;
import com.evilnotch.lib.minecraft.network.packet.PacketPickEntity;
import com.evilnotch.lib.minecraft.network.packet.PacketRequestSeed;
import com.evilnotch.lib.minecraft.network.packet.PacketSeed;
import com.evilnotch.lib.minecraft.network.packet.PacketSeedDeny;
import com.evilnotch.lib.minecraft.network.packet.PacketSkin;
import com.evilnotch.lib.minecraft.network.packet.PacketSkinChange;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.network.packet.PacketYawHead;
import com.evilnotch.lib.minecraft.network.packet.PacketYawOffset;
import com.evilnotch.lib.minecraft.network.packet.PacketYawPitch;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketClientHooksHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketClipBoardHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketHandHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketPickBlockHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketPickEntityHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketRequestSeedHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketSeedDenyHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketSeedHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketSkinChangeHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketSkinHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketUUIDHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketYawHeadHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketYawOffsetHandler;
import com.evilnotch.lib.minecraft.network.packet.handler.PacketYawPitchHandler;

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
		registerMessage(PacketSeedDenyHandler.class, PacketSeedDeny.class, Side.CLIENT);
		registerMessage(PacketYawOffsetHandler.class, PacketYawOffset.class, Side.CLIENT);
		registerMessage(PacketYawPitchHandler.class, PacketYawPitch.class, Side.CLIENT);
		registerMessage(PacketYawHeadHandler.class, PacketYawHead.class, Side.CLIENT);
		registerMessage(PacketHandHandler.class, PacketHand.class, Side.CLIENT);
		registerMessage(PacketSkinHandler.class, PacketSkin.class, Side.CLIENT);
		registerMessage(PacketClientHooksHandler.class, PacketClientHooks.class, Side.CLIENT);
		
		//to server
		registerMessage(PacketRequestSeedHandler.class, PacketRequestSeed.class, Side.SERVER);
		registerMessage(PacketPickBlockHandler.class, PacketPickBlock.class, Side.SERVER);
		registerMessage(PacketPickEntityHandler.class, PacketPickEntity.class, Side.SERVER);
		registerMessage(PacketSkinChangeHandler.class, PacketSkinChange.class, Side.SERVER);
	}
	
	public static void registerMessage(Class handler, Class packet, Side side)
	{
		INSTANCE.registerMessage(handler, packet, networkid++, side);
	}
}