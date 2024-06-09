package com.evilnotch.lib.minecraft.capability.client;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.ResourceLocation;

public interface ILoginHook {
	
	public void read(CPacketLoginStart pck, PacketBuffer buf);
	public void write(CPacketLoginStart pck, PacketBuffer buf);
	public ResourceLocation getId();
	public ILoginHook newInstance();

}
