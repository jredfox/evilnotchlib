package com.evilnotch.lib.minecraft.network.packet;

import java.util.UUID;

import com.evilnotch.lib.minecraft.network.NetWorkWrapper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
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
