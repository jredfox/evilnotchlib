package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.eventhandler.PickBlock;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketPickEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;

public class PacketPickEntityHandler extends MessegeBase<PacketPickEntity>{

	@Override
	public void handleClientSide(PacketPickEntity message, EntityPlayer player) {}

	@Override
	public void handleServerSide(PacketPickEntity message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			PickBlock.pickBlock(new RayTraceResult(player.world.getEntityByID(message.entityId),message.vec), player, player.world);
		});
	}

}
