package com.EvilNotch.lib.minecraft.network;

import com.EvilNotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class MessegeBaseResponce<REQ extends IMessage,REPLY extends IMessage> implements IMessageHandler<REQ,REPLY>{

	@Override
	public REPLY onMessage(REQ message, MessageContext ctx) 
	{
		if(ctx.side == Side.SERVER)
			return (REPLY) handleServer(message,ctx,ctx.getServerHandler().player);
		else
			return (REPLY) handleClient(message,ctx,ClientProxy.getPlayer());
	}

	public abstract REPLY handleServer(REQ message, MessageContext ctx, EntityPlayerMP player);
	public abstract REPLY handleClient(REQ message, MessageContext ctx, EntityPlayer player);

}
