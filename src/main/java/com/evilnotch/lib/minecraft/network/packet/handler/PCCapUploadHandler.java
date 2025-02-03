package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.main.capability.LoginCap;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PCCapDownload;
import com.evilnotch.lib.minecraft.network.packet.PCCapUpload;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PCCapUploadHandler extends MessegeBase<PCCapUpload> {

	@Override
	public void handleServerSide(PCCapUpload message, EntityPlayer player) 
	{
		EntityPlayerMP p = (EntityPlayerMP)player;
		p.getServerWorld().addScheduledTask(() ->
		{
			LoginCap login = ClientCapHooks.getLoginCap(p);
			login.setClientCaps(message.nbt);//update the IClientCap's NBT
			NetWorkHandler.INSTANCE.sendToTracking(new PCCapDownload(p), p);//Update all Players Tracking the IClientCaps
		});
	}
	
	@Override
	public void handleClientSide(PCCapUpload message, EntityPlayer player) {}
	
}
