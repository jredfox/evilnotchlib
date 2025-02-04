package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PCCapRem;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PCCapRemHandler extends MessegeBase<PCCapRem> {
	
	@Override
	public void handleClientSide(PCCapRem message, EntityPlayer player)
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			ClientCapHooks.others.remove(message.uuid);
		});
	}
	
	@Override
	public void handleServerSide(PCCapRem message, EntityPlayer player) {}

}
