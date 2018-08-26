package com.EvilNotch.lib.minecraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSeed implements IMessage{

	public long seed;
	public int dim;
	
	public PacketSeed(int d, long s)
	{
		this.dim = d;
		this.seed = s;
	}
	public PacketSeed(){}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.dim = buf.readInt();
		this.seed = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.seed);
		buf.writeInt(this.dim);
	}

}
