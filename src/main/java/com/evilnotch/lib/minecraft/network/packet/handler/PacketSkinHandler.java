package com.evilnotch.lib.minecraft.network.packet.handler;

import java.util.UUID;

import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSkin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;

public class PacketSkinHandler extends MessegeBase<PacketSkin>{

	@Override
	public void handleClientSide(PacketSkin message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP thePlayer = mc.player;
			UUID uuid = message.profile.getId();
			System.out.println("PayLoad:" + SkinCache.getEncode(message.profile.getProperties()));
			System.out.println("Name:" + message.profile.getName());
			NetworkPlayerInfo info = new NetworkPlayerInfo(new SPacketPlayerListItem().newAddPlayerData(message.profile, message.ping, message.gameType, message.name));
			thePlayer.connection.playerInfoMap.put(uuid, info);
			EntityPlayer other = mc.world.getPlayerEntityByUUID(uuid);//TODO: look into if UUIDPatcher needs to also update client's player
			if(other instanceof AbstractClientPlayer)
			{
				((AbstractClientPlayer)other).playerInfo = null;//TODO: make this public-f
				//TODO: make SPacketPlayerListItem#newAddPlayerData
			}
		});
	}

	@Override
	public void handleServerSide(PacketSkin message, EntityPlayer player) {}

}
