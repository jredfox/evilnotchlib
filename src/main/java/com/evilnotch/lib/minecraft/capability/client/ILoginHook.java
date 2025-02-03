package com.evilnotch.lib.minecraft.capability.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface ILoginHook {
	
	public void write(NBTTagCompound nbt);
	public void read(NBTTagCompound nbt);
	public ResourceLocation getId();

}
