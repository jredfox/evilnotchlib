package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRequestSeed implements IMessage{
	
	public int dim;
	
	public PacketRequestSeed(){
		
	}
	
	public PacketRequestSeed(int d)
	{
		this.dim = d;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.dim = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.dim);
	}

}
