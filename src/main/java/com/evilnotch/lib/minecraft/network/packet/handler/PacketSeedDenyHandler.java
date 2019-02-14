package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSeedDeny;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSeedDenyHandler extends MessegeBase<PacketSeedDeny>{

	@Override
	public void handleClientSide(PacketSeedDeny message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			Seeds.setSeed(message.dimension, Seeds.denied);
		});
	}

	@Override
	public void handleServerSide(PacketSeedDeny message, EntityPlayer player) 
	{
		
	}

}
