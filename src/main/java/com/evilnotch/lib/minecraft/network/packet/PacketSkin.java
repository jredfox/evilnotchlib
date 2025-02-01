package com.evilnotch.lib.minecraft.network.packet;

import java.util.Properties;
import java.util.UUID;

import com.evilnotch.lib.minecraft.network.NetWorkWrapper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSkin implements IMessage {
	
	public PacketSkin() {}
	
	public GameProfile profile;
	public int ping;
	public GameType gameType;
	public ITextComponent name;
	
	public PacketSkin(EntityPlayerMP p)
	{
		this.profile = p.getGameProfile();
		this.ping = p.ping;
		this.gameType = p.interactionManager.getGameType();
		this.name = p.getTabListDisplayName() != null ? p.getTabListDisplayName() : null;
	}
	
	@Override
	public void toBytes(ByteBuf buf) 
	{
		NetWorkWrapper.writeGameProfile(buf, this.profile);
		buf.writeInt(this.ping);
		buf.writeInt(this.gameType.getID());
		if(this.name != null)
		{
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, ITextComponent.Serializer.componentToJson(this.name));
		}
		else
			buf.writeBoolean(false);
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.profile = NetWorkWrapper.readGameProfile(buf);
		this.ping = buf.readInt();
		this.gameType = GameType.getByID(buf.readInt());
		if(buf.readBoolean())
			this.name = ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(buf));
	}
}
