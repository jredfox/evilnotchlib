package com.evilnotch.lib.minecraft.network.packets.handlers;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packets.PacketClipBoard;
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
			System.out.println("copied to clipboard:" + message.str);
		});
	}

	@Override
	public void handleServerSide(PacketClipBoard message, EntityPlayer player) 
	{
		
	}

}
