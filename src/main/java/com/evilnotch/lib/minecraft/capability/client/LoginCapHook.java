package com.evilnotch.lib.minecraft.capability.client;

import com.evilnotch.lib.main.MainJava;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.ResourceLocation;

public class LoginCapHook implements ILoginHook {

	public static final ResourceLocation ID = new ResourceLocation(MainJava.MODID, "loginCaps");

	public NBTTagCompound nbt;

	public LoginCapHook() {
		this.nbt = new NBTTagCompound();
	}

	public LoginCapHook(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void read(CPacketLoginStart pck, PacketBuffer buf) {
		try {
			this.nbt = buf.readCompoundTag();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(CPacketLoginStart pck, PacketBuffer buf) {
		buf.writeCompoundTag(this.nbt);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public ILoginHook newInstance()
	{
		NBTTagCompound tag = new NBTTagCompound();
		for (IClientCap cap : ClientCapHooks.clientCaps.values())
		{
			Object o = cap.get();
			String id = cap.getId().toString().replace(":", "_");
			if (o instanceof Boolean)
				tag.setBoolean(id, (boolean) cap.get());
			else if (o instanceof String)
				tag.setString(id, (String) cap.get());
			else if (o instanceof Integer)
				tag.setInteger(id, (int) cap.get());
		}

		return new LoginCapHook(tag);
	}

}
