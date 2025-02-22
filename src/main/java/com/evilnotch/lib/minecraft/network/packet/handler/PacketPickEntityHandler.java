package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.eventhandler.PickBlock;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketPickEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;

public class PacketPickEntityHandler extends MessegeBase<PacketPickEntity>{

	@Override
	public void handleClientSide(PacketPickEntity message, EntityPlayer player) 
	{
		
	}

	@Override
	public void handleServerSide(PacketPickEntity message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			Entity e = player.world.getEntityByID(message.entityId);
			if(e == null)
			{
				System.err.println("recieved invalid packet for pickEntity event");
				return;
			}
			PickBlock.pickBlock(new RayTraceResult(e,message.vec), message.ctr, player, player.world);
		});
	}

}
