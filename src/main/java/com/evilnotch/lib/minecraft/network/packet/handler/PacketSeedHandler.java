package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.basicmc.client.Seeds;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSeed;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSeedHandler extends MessegeBase<PacketSeed>{

	@Override
	public void handleClientSide(PacketSeed message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Seeds.setSeed(message.dim,message.seed);
		});
	}

	@Override
	public void handleServerSide(PacketSeed message, EntityPlayer player) 
	{
		
	}

}
