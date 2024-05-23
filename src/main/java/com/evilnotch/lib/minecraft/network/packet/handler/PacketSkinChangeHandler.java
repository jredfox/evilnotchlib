package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.skin.SkinCache.EvilProperty;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSkinChange;
import com.evilnotch.lib.minecraft.util.UUIDPatcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSkinChangeHandler extends MessegeBase<PacketSkinChange> {

	@Override
	public void handleClientSide(PacketSkinChange message, EntityPlayer player) {}

	@Override
	public void handleServerSide(PacketSkinChange message, EntityPlayer player)
	{
		EntityPlayerMP p = (EntityPlayerMP) player;
		p.getServerWorld().addScheduledTask(() ->
		{
			GameProfile profile = p.getGameProfile();
	    	UUIDPatcher.setSkin(profile.getProperties(), UUIDPatcher.patchSkin(profile, message.payload));
			VanillaBugFixes.updateSkinPackets((EntityPlayerMP) player);
		});
	}

}
