package com.evilnotch.lib.minecraft.network.packet.handler;

import java.util.UUID;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSkin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;

public class PacketSkinHandler extends MessegeBase<PacketSkin>{

	@Override
	public void handleClientSide(PacketSkin message, EntityPlayer player) 
	{
		Minecraft.getMinecraft().addScheduledTask(()->
		{
			Minecraft mc = Minecraft.getMinecraft();
			UUID uuid = message.profile.getId();
			NetworkPlayerInfo info = new NetworkPlayerInfo(new SPacketPlayerListItem().newAddPlayerData(message.profile, message.ping, message.gameType, message.name));
			NetworkPlayerInfo prev = mc.player.connection.playerInfoMap.put(uuid, info);
			if(prev != null)
			{
				//nicknames are broken and won't re-sync keep the old one
				if(prev.displayName != null)
					info.displayName = prev.displayName;
				
				//Sync old values so the client doesn't freak out and glitch
				info.displayHealth = prev.displayHealth;
				info.healthBlinkTime = prev.healthBlinkTime;
				info.lastHealth = prev.lastHealth;
				info.lastHealthTime = prev.lastHealthTime;
				info.renderVisibilityId = prev.renderVisibilityId;
			}
			EntityPlayer other = mc.world.getPlayerEntityByUUID(uuid);
			if(other instanceof AbstractClientPlayer)
				((AbstractClientPlayer) other).playerInfo = null;
		});
	}

	@Override
	public void handleServerSide(PacketSkin message, EntityPlayer player) {}

}
