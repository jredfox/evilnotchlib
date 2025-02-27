package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSkinChange;
import com.evilnotch.lib.minecraft.util.UUIDPatcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSkinChangeHandler extends MessegeBase<PacketSkinChange> {

	@Override
	public void handleClientSide(PacketSkinChange message, EntityPlayer player) {}

	@Override
	public void handleServerSide(PacketSkinChange message, EntityPlayer player)
	{
		if(!Config.skinCache)
			return;//allow server disabling the skin cache to stop changing the skins
		EntityPlayerMP p = (EntityPlayerMP) player;
		p.getServerWorld().addScheduledTask(() ->
		{
			if(!SkinCache.isSkinEmpty(message.payload))
			{
				UUIDPatcher.patchSkin(p.getGameProfile(), message.payload);
				SkinCache.syncSkin(p);
			}
		});
	}

}
