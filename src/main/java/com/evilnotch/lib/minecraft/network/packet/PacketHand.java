package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketHand implements IMessage{
	
	public int slot;
	
	public PacketHand(){}
	
	public PacketHand(int index)
	{
		this.slot = index;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.slot);
	}

}
