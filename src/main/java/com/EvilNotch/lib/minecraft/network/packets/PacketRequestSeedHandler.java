package com.EvilNotch.lib.minecraft.network.packets;

import com.EvilNotch.lib.minecraft.network.MessegeBase;
import com.EvilNotch.lib.minecraft.network.MessegeBaseResponce;
import com.EvilNotch.lib.minecraft.network.NetWorkHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestSeedHandler extends MessegeBaseResponce<PacketRequestSeed,PacketSeed>{

	@Override
	public PacketSeed handleServer(PacketRequestSeed message, MessageContext ctx, EntityPlayerMP player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		long seed = p.mcServer.getWorld(message.dim).getSeed();
		PacketSeed packet = new PacketSeed(message.dim,seed);
		return packet;
	}

	@Override
	public PacketSeed handleClient(PacketRequestSeed message, MessageContext ctx, EntityPlayer player) 
	{
		return null;
	}

}
