package com.evilnotch.lib.minecraft.network.packet;

import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.main.skin.SkinEntry;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSkinChange implements IMessage{
	
	public PacketSkinChange() {}
	
	public String payload = "";
	public PacketSkinChange(SkinEntry s)
	{
		if(!s.isEmpty)
		{
			this.payload = SkinCache.getEncode(s);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.payload = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.payload);
	}

}
