package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.eventhandler.PickBlock;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.minecraft.network.packet.PacketPickBlock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;

public class PacketPickBlockHandler extends MessegeBase<PacketPickBlock>{
	
	@Override
	public void handleServerSide(PacketPickBlock message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			PickBlock.pickBlock(new RayTraceResult(message.vec,message.facing,message.pos), player, player.world);
		});
	}
	
	
	@Override
	public void handleClientSide(PacketPickBlock message, EntityPlayer player){}

}
