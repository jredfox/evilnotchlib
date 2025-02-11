package com.evilnotch.lib.minecraft.network;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetWorkWrapper extends SimpleNetworkWrapper{
	
	public int idClient;
	public int idServer = 80;

	public NetWorkWrapper(String channelName) 
	{
		super(channelName);
	}
	
	/**
	 * sends to self and tracking players
	 */
	public void sendToTrackingAndPlayer(IMessage msg, EntityPlayerMP player)
	{
		this.sendTo(msg, player);
		this.sendToTracking(msg, player);
	}
	
	/**
	 * sends to tracking players
	 */
	public void sendToTracking(IMessage msg, EntityPlayerMP player)
	{
        for(EntityPlayer p : player.getServerWorld().getEntityTracker().getTrackingPlayers(player) )
        {
        	this.sendTo(msg, (EntityPlayerMP) p);
        }
	}
	
	public static void writeGameProfile(ByteBuf buf, GameProfile profile)
	{
		writeUUID(buf, profile.getId());
		ByteBufUtils.writeUTF8String(buf, profile.getName());
		PropertyMap props = profile.getProperties();
		buf.writeInt(props.size());
		for (Property property : props.values())
        {
			ByteBufUtils.writeUTF8String(buf, property.getName());
			ByteBufUtils.writeUTF8String(buf, property.getValue());

            if (property.hasSignature())
            {
                buf.writeBoolean(true);
                ByteBufUtils.writeUTF8String(buf, property.getSignature());
            }
            else
                buf.writeBoolean(false);
        }
	}
	
	public static void writeUUID(ByteBuf buf, UUID uuid)
	{
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}
	
	public static UUID readUUID(ByteBuf buf)
	{
		return new UUID(buf.readLong(), buf.readLong());
	}
	
	public static GameProfile readGameProfile(ByteBuf buf)
	{
		UUID id = readUUID(buf);
		String user = ByteBufUtils.readUTF8String(buf);
		GameProfile profile = new GameProfile(id, user);
		int size = buf.readInt();
		PropertyMap p = profile.getProperties();
		for(int i=0;i<size;i++)
		{
			String name =  ByteBufUtils.readUTF8String(buf);
			String value = ByteBufUtils.readUTF8String(buf);
			p.put(name, buf.readBoolean() ? new Property(name, value, ByteBufUtils.readUTF8String(buf)) : new Property(name, value) );
		}
		return profile;
	}
}
