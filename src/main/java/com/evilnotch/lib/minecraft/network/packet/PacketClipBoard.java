package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketClipBoard implements IMessage{

	public String str = null;
	
	public PacketClipBoard(){}
	public PacketClipBoard(String s){
		this.str = s;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.str = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.str);
	}

}
