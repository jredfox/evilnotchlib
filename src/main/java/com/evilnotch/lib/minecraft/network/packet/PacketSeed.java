package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSeed implements IMessage{

	public int dim;
	public long seed;
	
	public PacketSeed(int d, long s)
	{
		this.dim = d;
		this.seed = s;
	}
	public PacketSeed(){}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.dim = buf.readInt();
		this.seed = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.dim);
		buf.writeLong(this.seed);
	}

}
