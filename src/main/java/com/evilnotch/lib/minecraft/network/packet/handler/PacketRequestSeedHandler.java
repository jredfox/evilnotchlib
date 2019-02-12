package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketRequestSeed;
import com.evilnotch.lib.minecraft.network.packet.PacketSeed;
import com.evilnotch.lib.minecraft.network.packet.PacketSeedDeny;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketRequestSeedHandler extends MessegeBase<PacketRequestSeed>{

	@Override
	public void handleClientSide(PacketRequestSeed message, EntityPlayer player) 
	{
		
	}

	@Override
	public void handleServerSide(PacketRequestSeed message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			if(!MinecraftUtil.checkPermission(p, "seed") && !PlayerUtil.isPlayerOwner(p))
			{
				PacketSeedDeny packet = new PacketSeedDeny(message.dim);
				NetWorkHandler.INSTANCE.sendTo(packet, p);
			}
			else
			{
				long seed = p.mcServer.getWorld(message.dim).getSeed();
				PacketSeed packet = new PacketSeed(message.dim, seed);
				NetWorkHandler.INSTANCE.sendTo(packet, p);
			}
		});
	}

}
