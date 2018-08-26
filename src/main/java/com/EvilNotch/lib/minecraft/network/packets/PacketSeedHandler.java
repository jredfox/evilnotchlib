package com.EvilNotch.lib.minecraft.network.packets;

import com.EvilNotch.lib.main.eventhandlers.ClientEvents;
import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSeedHandler extends MessegeBase<PacketSeed>{

	@Override
	public void handleClientSide(PacketSeed message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			ClientProxy.setSeed(message.dim,message.seed);
		});
	}

	@Override
	public void handleServerSide(PacketSeed message, EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

}
