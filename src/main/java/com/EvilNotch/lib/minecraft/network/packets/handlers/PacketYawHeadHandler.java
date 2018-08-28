package com.EvilNotch.lib.minecraft.network.packets.handlers;

import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.minecraft.network.packets.PacketYawHead;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketYawHeadHandler extends MessegeBase<PacketYawHead>{

	@Override
	public void handleClientSide(PacketYawHead message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Entity e = Minecraft.getMinecraft().world.getEntityByID(message.id);
			if(!(e instanceof EntityPlayer))
			{
				System.out.println("invalid packet recieved for the head:" + message.id);
			}
			EntityPlayer p = (EntityPlayer)e;
			p.setRotationYawHead(message.head);
		});
	}

	@Override
	public void handleServerSide(PacketYawHead message, EntityPlayer player) {}

}
