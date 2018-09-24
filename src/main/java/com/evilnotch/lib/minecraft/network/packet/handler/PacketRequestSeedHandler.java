package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketRequestSeed;
import com.evilnotch.lib.minecraft.network.packet.PacketSeed;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestSeedHandler extends MessegeBase<PacketRequestSeed>{

	@Override
	public void handleClientSide(PacketRequestSeed message, EntityPlayer player) {}

	@Override
	public void handleServerSide(PacketRequestSeed message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			long seed = p.mcServer.getWorld(message.dim).getSeed();
			PacketSeed packet = new PacketSeed(message.dim,seed);
			NetWorkHandler.INSTANCE.sendTo(packet,p);
		});
	}

}
