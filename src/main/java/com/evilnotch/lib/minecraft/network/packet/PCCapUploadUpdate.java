package com.evilnotch.lib.minecraft.network.packet;

import java.io.IOException;

import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.capability.client.IClientCap;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PCCapUploadUpdate implements IMessage {
	
	public PCCapUploadUpdate() {}
	
	public NBTTagCompound nbt;
	
	public PCCapUploadUpdate(ResourceLocation... ids)
	{
		NBTTagCompound n = new NBTTagCompound();
		for(ResourceLocation id : ids)
		{
			if(id == null)
				continue;
			IClientCap cap = ClientCapHooks.get(id);
			if(cap != null)
				cap.write(n);
		}
		this.nbt = n;
	}
	
	@Override
	public void toBytes(ByteBuf b)
	{
		PacketBuffer buf = b instanceof PacketBuffer ? (PacketBuffer) b : new PacketBuffer(b);
		buf.writeCompoundTag(this.nbt);
	}

	@Override
	public void fromBytes(ByteBuf b) 
	{
		PacketBuffer buf = b instanceof PacketBuffer ? (PacketBuffer) b : new PacketBuffer(b);
		try 
		{
			this.nbt = buf.readCompoundTag();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
