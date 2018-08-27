package com.EvilNotch.lib.minecraft.network.packets.handlers;

import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawOffset;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketYawOffsetHandler extends MessegeBase<PacketYawOffset>{

	@Override
	public void handleClientSide(PacketYawOffset message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			player.renderYawOffset = message.yawOffsetRender;
		});
	}

	@Override
	public void handleServerSide(PacketYawOffset message, EntityPlayer player) {}

}
