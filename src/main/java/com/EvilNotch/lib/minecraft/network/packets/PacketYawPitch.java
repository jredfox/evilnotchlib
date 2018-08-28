package com.EvilNotch.lib.minecraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketYawPitch implements IMessage{
	
	public float yaw;
	public float pitch;
	public int id;
	
	public PacketYawPitch(float yaw, float pitch,int id)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		this.id = id;
	}
	public PacketYawPitch(){}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
		buf.writeInt(this.id);
	}

}
