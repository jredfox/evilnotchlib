package com.evilnotch.lib.minecraft.network.packet;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketUUID implements IMessage{
	
	public UUID uuid = null;
	public int id;
	
	public PacketUUID(int id, UUID uuid)
	{
		this.id = id;
		this.uuid = uuid;
	}
	
	public PacketUUID()
	{
		
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		long max = buf.readLong();
		long min = buf.readLong();
		this.uuid = new UUID(max,min);
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		long max = this.uuid.getMostSignificantBits();
		long min = this.uuid.getLeastSignificantBits();
		buf.writeLong(max);
		buf.writeLong(min);
		buf.writeInt(this.id);
	}

}
