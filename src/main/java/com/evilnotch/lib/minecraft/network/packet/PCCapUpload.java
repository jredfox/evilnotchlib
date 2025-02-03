package com.evilnotch.lib.minecraft.network.packet;

import java.io.IOException;

import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PCCapUpload implements IMessage {
	
	public PCCapUpload() {}
	
	public NBTTagCompound nbt;
	
	public PCCapUpload(Object unused)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		ClientCapHooks.write(nbt, ClientCapHooks.clientCaps.values());
		this.nbt = nbt;
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
