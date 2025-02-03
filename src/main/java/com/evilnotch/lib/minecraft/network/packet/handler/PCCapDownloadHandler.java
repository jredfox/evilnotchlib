package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PCCapDownload;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PCCapDownloadHandler extends MessegeBase<PCCapDownload> {

	@Override
	public void handleClientSide(PCCapDownload message, EntityPlayer player)
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			ClientCapHooks.download(message.uuid, message.nbt);
		});
	}
	
	@Override
	public void handleServerSide(PCCapDownload message, EntityPlayer player) {}

}
