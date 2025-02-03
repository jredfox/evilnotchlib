package com.evilnotch.lib.minecraft.network.packet;

import java.io.IOException;
import java.util.UUID;

import com.evilnotch.lib.main.capability.LoginCap;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketClientHooks implements IMessage {
	
	public NBTTagCompound nbt;
	public UUID uuid;
	
	public PacketClientHooks(){}
	
	public PacketClientHooks(EntityPlayerMP p)
	{
		this.uuid = p.getGameProfile().getId();
		LoginCap login = (LoginCap) CapabilityRegistry.getCapability(p, ClientCapHooks.ID_LOGIN);
		this.nbt = login.getClientCaps();
	}
	
	@Override
	public void toBytes(ByteBuf b) 
	{
		PacketBuffer buf = b instanceof PacketBuffer ? (PacketBuffer) b : new PacketBuffer(b);
		buf.writeUniqueId(this.uuid);
		buf.writeCompoundTag(this.nbt);
	}

	@Override
	public void fromBytes(ByteBuf b) 
	{
		PacketBuffer buf = b instanceof PacketBuffer ? (PacketBuffer) b : new PacketBuffer(b);
		try 
		{
			this.uuid = buf.readUniqueId();
			this.nbt = buf.readCompoundTag();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
