package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketYawOffset implements IMessage{
	
	public float yawOffsetRender = 0.0F;
	public int id;
	
	public PacketYawOffset(float yaw,int id)
	{
		this.yawOffsetRender = yaw;
		this.id = id;
	}
	
	public PacketYawOffset()
	{
		
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.yawOffsetRender = buf.readFloat();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeFloat(this.yawOffsetRender);
		buf.writeInt(this.id);
	}

}
