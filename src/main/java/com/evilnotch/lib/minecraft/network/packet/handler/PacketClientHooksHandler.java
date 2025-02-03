package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketClientHooks;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketClientHooksHandler extends MessegeBase<PacketClientHooks> {

	@Override
	public void handleClientSide(PacketClientHooks message, EntityPlayer player)
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			ClientCapHooks.registerPlayer(message.uuid, message.nbt);
		});
	}
	
	@Override
	public void handleServerSide(PacketClientHooks message, EntityPlayer player) {}

}
