package com.evilnotch.lib.minecraft.network.packet;

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
			//sync skins with client
			s = s.copy();
			Session session = Minecraft.getMinecraft().getSession();
			s.user = session.getUsername();
			s.uuid = session.getPlayerID().toString().replace("-", "");
			
			//encode the skin into a useable payload
			this.payload = s.encode();
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
