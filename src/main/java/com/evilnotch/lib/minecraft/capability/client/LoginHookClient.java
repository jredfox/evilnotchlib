package com.evilnotch.lib.minecraft.capability.client;

import com.evilnotch.lib.main.MainJava;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class LoginHookClient implements ILoginHook {

	private static final ResourceLocation ID = new ResourceLocation(MainJava.MODID, "IClientCaps");
	
	public LoginHookClient() {}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void write(NBTTagCompound nbt) 
	{
		ClientCapHooks.write(nbt, ClientCapHooks.clientCaps.values());
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		ClientCapHooks.read(nbt, ClientCapHooks.clientCaps.values());
	}
}
