package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSendSkin implements IMessage {
	
	public String payload;

	public PacketSendSkin() 
	{
		
	}
	
	public PacketSendSkin(String skindata)
	{
		this.payload = skindata == null ? "" : skindata;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.payload = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.payload);
	}

}
