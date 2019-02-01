package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSeedDeny implements IMessage{
	
	public PacketSeedDeny(){}
	
	public int dimension;
	public PacketSeedDeny(int d)
	{
		this.dimension = d;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.dimension);
	}

}
