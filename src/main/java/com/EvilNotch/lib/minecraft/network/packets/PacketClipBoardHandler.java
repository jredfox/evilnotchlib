package com.EvilNotch.lib.minecraft.network.packets;

import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.entity.player.EntityPlayer;

public class PacketClipBoardHandler extends MessegeBase<PacketClipBoard>{

	@Override
	public void handleClientSide(PacketClipBoard message, EntityPlayer player) 
	{
		JavaUtil.writeToClipboard(message.str, null);
		System.out.println("copied to clipboard:" + message.str);
	}

	@Override
	public void handleServerSide(PacketClipBoard message, EntityPlayer player) 
	{
		
	}

}
