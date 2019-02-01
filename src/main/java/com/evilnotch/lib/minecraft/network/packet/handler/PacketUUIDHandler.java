package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.util.EntityUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketUUIDHandler extends MessegeBase<PacketUUID>{

	@Override
	public void handleClientSide(PacketUUID message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			EntityUtil.setPlayerUUID(player, message.uuid);
		});
	}

	@Override
	public void handleServerSide(PacketUUID message, EntityPlayer player) {
	}

}
