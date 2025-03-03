package com.evilnotch.lib.minecraft.network.packet.handler;

import java.util.UUID;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSkin;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
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
			//Smoothest possible way to change skin but may be incompatible with some skin mods
			if(Config.skinPacketSmooth)
			{
				NetworkPlayerInfo prev = mc.player.connection.playerInfoMap.get(uuid);
				if(prev != null)
				{
					prev.gameProfile = message.profile;
					prev.playerTexturesLoaded = false;
					
					//Ensure the other player's info is synced with the live map
					EntityPlayer other = mc.world.getPlayerEntityByUUID(uuid);
					if(other instanceof AbstractClientPlayer)
					{
						AbstractClientPlayer ap = (AbstractClientPlayer) other;
						ClientProxy.removeSkinCache(mc, ap);
						ap.playerInfo = prev;
					}
					
					return;
				}
			}
			NetworkPlayerInfo info = new NetworkPlayerInfo(new SPacketPlayerListItem().new AddPlayerData(message.profile, message.ping, message.gameType, message.name));
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
			{
				AbstractClientPlayer ap = ((AbstractClientPlayer) other);
				ClientProxy.removeSkinCache(mc, ap);
				ap.playerInfo = null;
				ap.hasPlayerInfo();
			}
		});
	}

	@Override
	public void handleServerSide(PacketSkin message, EntityPlayer player) {}

}
