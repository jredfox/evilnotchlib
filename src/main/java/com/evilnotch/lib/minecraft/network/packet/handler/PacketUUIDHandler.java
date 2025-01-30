package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;

import net.minecraft.entity.player.EntityPlayer;

public class PacketUUIDHandler extends MessegeBase<PacketUUID>{

	@Override
	public void handleClientSide(PacketUUID message, EntityPlayer p) 
	{
		MainJava.proxy.handleUUIDChange(message);
	}

	@Override
	public void handleServerSide(PacketUUID message, EntityPlayer player) 
	{
		
	}

}
