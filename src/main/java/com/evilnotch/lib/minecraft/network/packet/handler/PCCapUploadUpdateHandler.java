package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.capability.LoginCap;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PCCapDLUpdate;
import com.evilnotch.lib.minecraft.network.packet.PCCapUploadUpdate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PCCapUploadUpdateHandler extends MessegeBase<PCCapUploadUpdate> {

	@Override
	public void handleServerSide(PCCapUploadUpdate message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			LoginCap login = ClientCapHooks.getLoginCap(p);
			login.getClientCaps().merge(message.nbt);
			NetWorkHandler.INSTANCE.sendToAll(new PCCapDLUpdate(p, message.nbt));//Update all Players Tracking the IClientCaps
		});
	}
	
	@Override
	public void handleClientSide(PCCapUploadUpdate message, EntityPlayer player) {}
	
}
