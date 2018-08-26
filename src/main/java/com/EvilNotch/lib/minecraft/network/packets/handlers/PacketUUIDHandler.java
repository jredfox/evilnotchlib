package com.EvilNotch.lib.minecraft.network.packets.handlers;

import java.util.UUID;

import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.minecraft.network.packets.PacketUUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketUUIDHandler extends MessegeBase<PacketUUID>{

	@Override
	public void handleClientSide(PacketUUID message, EntityPlayer player) {
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			if(message.uuid == null)
			{
				System.out.println("Recieved Invalid Packet for null uuid!");
				return;
			}
			System.out.println("Setting Client UUID to:" + message.uuid);
			EntityUtil.setPlayerUUID(player, UUID.fromString(message.uuid));
		});
	}

	@Override
	public void handleServerSide(PacketUUID message, EntityPlayer player) {
	}

}
