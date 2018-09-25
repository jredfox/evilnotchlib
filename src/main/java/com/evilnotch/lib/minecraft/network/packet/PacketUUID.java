package com.evilnotch.lib.minecraft.network.packet;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketUUID implements IMessage{
	
	public UUID uuid = null;
	
	public PacketUUID(){}
	
	public PacketUUID(UUID id)
	{
		this.uuid = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		long max = buf.readLong();
		long min = buf.readLong();
		this.uuid = new UUID(max,min);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		long max = this.uuid.getMostSignificantBits();
		long min = this.uuid.getLeastSignificantBits();
		buf.writeLong(max);
		buf.writeLong(min);
	}

}
