package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PCCapDLUpdate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PCCapDLUpdateHandler extends MessegeBase<PCCapDLUpdate> {

	@Override
	public void handleClientSide(PCCapDLUpdate message, EntityPlayer player)
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			ClientCapHooks.downloadUpdate(message.uuid, message.nbt);
		});
	}
	
	@Override
	public void handleServerSide(PCCapDLUpdate message, EntityPlayer player) {}

}
