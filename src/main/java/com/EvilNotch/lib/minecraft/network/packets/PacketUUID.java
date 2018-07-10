package com.EvilNotch.lib.minecraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketUUID implements IMessage{
	
	public String uuid = null;
	
	public PacketUUID(){}
	
	public PacketUUID(String id){
		this.uuid = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.uuid = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		 ByteBufUtils.writeUTF8String(buf, this.uuid);
	}

}
