package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketYawHead implements IMessage{

	public float head;
	public int id;
	
	public PacketYawHead(float head,int id)
	{
		this.head = head;
		this.id = id;
	}
	
	public PacketYawHead()
	{
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.head = buf.readFloat();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(this.head);
		buf.writeInt(this.id);
	}

}
