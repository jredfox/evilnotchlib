package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketYawHead;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PacketYawHeadHandler extends MessegeBase<PacketYawHead>{

	@Override
	public void handleClientSide(PacketYawHead message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Entity e = Minecraft.getMinecraft().world.getEntityByID(message.id);
			if(!(e instanceof EntityLivingBase))
			{
				System.out.println("invalid packet recieved for the yawHead:" + message.id);
				return;
			}
			EntityLivingBase p = (EntityLivingBase)e;
			p.setRotationYawHead(message.head);
		});
	}

	@Override
	public void handleServerSide(PacketYawHead message, EntityPlayer player) {}

}
