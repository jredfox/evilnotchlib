package com.EvilNotch.lib.minecraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketYawOffset implements IMessage{
	
	public float yawOffsetRender = 0.0F;
	
	public PacketYawOffset(){}
	public PacketYawOffset(float yaw)
	{
		this.yawOffsetRender = yaw;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.yawOffsetRender = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(this.yawOffsetRender);
	}

}
