package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketUUIDHandler extends MessegeBase<PacketUUID>{

	@Override
	public void handleClientSide(PacketUUID message, EntityPlayer p) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			EntityPlayer player = Minecraft.getMinecraft().player;
			if(player.getEntityId() != message.id)
			{
				PlayerUtil.setPlayerUUID((EntityPlayer) player.world.getEntityByID(message.id), message.uuid);
			}
			else
			{
				PlayerUtil.setPlayerUUID(player, message.uuid);
			}
		});
	}

	@Override
	public void handleServerSide(PacketUUID message, EntityPlayer player) 
	{
		
	}

}
