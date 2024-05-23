package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.skin.SkinCache.EvilProperty;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketSkinChange;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSkinChangeHandler extends MessegeBase<PacketSkinChange> {

	@Override
	public void handleClientSide(PacketSkinChange message, EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleServerSide(PacketSkinChange message, EntityPlayer player)
	{
		EntityPlayerMP p = (EntityPlayerMP) player;
		p.getServerWorld().addScheduledTask(() ->
		{
	    	PropertyMap properties = p.getGameProfile().getProperties();
	    	properties.removeAll("textures");
	    	properties.put("textures", new EvilProperty("textures", message.payload));
			VanillaBugFixes.updateSkinPackets((EntityPlayerMP) player);
		});
	}

}
