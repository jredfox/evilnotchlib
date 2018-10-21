package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketHand;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketHandHandler extends MessegeBase<PacketHand>{

	@Override
	public void handleClientSide(PacketHand message, EntityPlayer player) {
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			player.inventory.currentItem = message.slot;
		});
	}

	@Override
	public void handleServerSide(PacketHand message, EntityPlayer player) {
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			player.inventory.currentItem = message.slot;
		});
	}

}
