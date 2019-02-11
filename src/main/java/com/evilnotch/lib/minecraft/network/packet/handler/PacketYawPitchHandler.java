package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketYawPitch;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PacketYawPitchHandler extends MessegeBase<PacketYawPitch>{

	@Override
	public void handleClientSide(PacketYawPitch message, EntityPlayer p) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Entity e = Minecraft.getMinecraft().world.getEntityByID(message.id);
			if(!(e instanceof EntityLivingBase))
			{
				System.out.println("invalid packet recieved for player:" + message.id);
				return;
			}
			EntityLivingBase player = (EntityLivingBase)e;
			
			player.renderYawOffset = message.yaw;
			player.prevRenderYawOffset = message.yaw;
			
			player.prevRotationYaw = message.yaw;
			player.rotationYaw = message.yaw;
			
			player.prevRotationPitch = message.pitch;
			player.rotationPitch = message.pitch;
		});
	}

	@Override
	public void handleServerSide(PacketYawPitch message, EntityPlayer player) 
	{
		
	}

}
