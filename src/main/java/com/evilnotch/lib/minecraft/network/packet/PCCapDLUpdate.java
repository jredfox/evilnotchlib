package com.evilnotch.lib.minecraft.network.packet;

import java.io.IOException;
import java.util.UUID;

import com.evilnotch.lib.main.capability.LoginCap;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PCCapDLUpdate implements IMessage {
	
	public UUID uuid;
	public NBTTagCompound nbt;
	
	public PCCapDLUpdate(){}
	
	public PCCapDLUpdate(EntityPlayerMP p, NBTTagCompound toUpdate)
	{
		this.uuid = p.getUniqueID();
		this.nbt = toUpdate != null ? toUpdate : new NBTTagCompound();
	}
	
	public PCCapDLUpdate(EntityPlayerMP p, ResourceLocation... ids)
	{
		this.uuid = p.getGameProfile().getId();
		LoginCap login = ClientCapHooks.getLoginCap(p);
		NBTTagCompound loginNBT = login.getClientCaps();
		this.nbt = new NBTTagCompound();
		for(ResourceLocation id : ids)
		{
			if(id == null) 
				continue;
			String key = ClientCapHooks.convertID(id);
			if(loginNBT.hasKey(key))
				this.nbt.setTag(key, loginNBT.getTag(key));
		}
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
