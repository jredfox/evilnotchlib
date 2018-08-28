package com.EvilNotch.lib.minecraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class NetWorkWrapper extends SimpleNetworkWrapper{

	public NetWorkWrapper(String channelName) 
	{
		super(channelName);
	}
	/**
	 * sends to self and tracking players
	 */
	public void sendToTrackingAndPlayer(IMessage msg,EntityPlayerMP player)
	{
		this.sendTo(msg, player);
		this.sendToTracking(msg, player);
	}
	/**
	 * sends to tracking players
	 */
	public void sendToTracking(IMessage msg,EntityPlayerMP player)
	{
        for(EntityPlayer p : player.getServerWorld().getEntityTracker().getTrackingPlayers(player) )
        {
        	this.sendTo(msg, (EntityPlayerMP) p);
        }
	}
}
