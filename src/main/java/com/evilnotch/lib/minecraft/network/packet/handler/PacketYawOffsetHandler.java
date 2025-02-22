package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketYawOffset;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PacketYawOffsetHandler extends MessegeBase<PacketYawOffset>{

	@Override
	public void handleClientSide(PacketYawOffset message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Entity e = Minecraft.getMinecraft().world.getEntityByID(message.id);
			if(!(e instanceof EntityLivingBase))
			{
				System.err.println("invalid packet recieved for player:" + message.id);
				return;
			}
			EntityLivingBase living = (EntityLivingBase)e;
			living.renderYawOffset = message.yawOffsetRender;
			living.prevRenderYawOffset = message.yawOffsetRender;
		});
	}

	@Override
	public void handleServerSide(PacketYawOffset message, EntityPlayer player) 
	{
		
	}

}
