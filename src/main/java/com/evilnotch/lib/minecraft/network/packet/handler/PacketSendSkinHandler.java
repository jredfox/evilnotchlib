package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketSeed;
import com.evilnotch.lib.minecraft.network.packet.PacketSeedDeny;
import com.evilnotch.lib.minecraft.network.packet.PacketSendSkin;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSendSkinHandler extends MessegeBase<PacketSendSkin>{

	@Override
	public void handleClientSide(PacketSendSkin message, EntityPlayer player) 
	{
		
	}

	@Override
	public void handleServerSide(PacketSendSkin message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
//			PropertyMap map = player.getGameProfile().getProperties();
//			map.removeAll("textures");
//			map.put("textures", new Property("textures", message.payload, null));
//			System.out.println(message.payload);
		});
	}

}
