package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketClipBoardHandler extends MessegeBase<PacketClipBoard>{

	@Override
	public void handleClientSide(PacketClipBoard message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			JavaUtil.writeToClipboard(message.str, null);
		});
	}

	@Override
	public void handleServerSide(PacketClipBoard message, EntityPlayer player) 
	{
		
	}

}
