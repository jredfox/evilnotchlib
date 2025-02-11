package com.evilnotch.lib.minecraft.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

public class NetWorkRegistry extends NetWorkWrapper {
	
	/**
	 * Registry From MODID to NetWorkWrapper
	 */
	public static Map<String, NetWorkWrapper> reg = new HashMap();
	/**
	 * Reference Between Packet to NetWorkWrapper
	 */
	public static Map<Class<? extends IMessage>, NetWorkWrapper> regGet = new HashMap();

	public NetWorkRegistry(String name)
	{
		super(name + "_reg");
	}
	
	@Override
    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestMessageType, int discriminator, Side side)
    {
		String modid = Loader.instance().activeModContainer().getModId().toString();
		NetWorkWrapper nw = reg.get(modid);
		if(nw == null)
		{
			nw = new NetWorkWrapper(modid);
			reg.put(modid, nw);
		}
		regGet.put(requestMessageType, nw);
		
		nw.registerMessage(messageHandler, requestMessageType, (side == Side.CLIENT ? nw.idClient++ : nw.idServer++), side);
    }
	
	@Override
    public void sendToAll(IMessage message)
    {
		regGet.get(message.getClass()).sendToAll(message);
    }
	
	@Override
    public void sendTo(IMessage message, EntityPlayerMP player)
    {
		regGet.get(message.getClass()).sendTo(message, player);
    }
	
	@Override
    public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point)
    {
		regGet.get(message.getClass()).sendToAllAround(message, point);
    }
	
	@Override
    public void sendToDimension(IMessage message, int dimensionId)
    {
		regGet.get(message.getClass()).sendToDimension(message, dimensionId);
    }
	
	@Override
    public void sendToServer(IMessage message)
    {
		regGet.get(message.getClass()).sendToServer(message);
    }
	
	@Override
	public void sendToTrackingAndPlayer(IMessage msg, EntityPlayerMP player)
	{
		regGet.get(msg.getClass()).sendToTrackingAndPlayer(msg, player);
	}
	
	@Override
	public void sendToTracking(IMessage msg, EntityPlayerMP player)
	{
		regGet.get(msg.getClass()).sendToTracking(msg, player);
	}

}
