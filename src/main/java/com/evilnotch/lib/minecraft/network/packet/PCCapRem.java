package com.evilnotch.lib.minecraft.network.packet;

import java.io.IOException;
import java.util.UUID;

import com.evilnotch.lib.main.capability.LoginCap;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.network.NetWorkWrapper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PCCapRem implements IMessage {

	public UUID uuid;
	
	public PCCapRem(){}
	
	public PCCapRem(EntityPlayerMP p) {
		this.uuid = p.getGameProfile().getId();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		NetWorkWrapper.writeUUID(buf, this.uuid);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.uuid = NetWorkWrapper.readUUID(buf);
	}

}
