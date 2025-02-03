package com.evilnotch.lib.minecraft.capability.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface IClientCap<T> {
	
	public ResourceLocation getId();
	public T get();
	public void set(T o);
	public Type type();
	public void write(NBTTagCompound nbt);
	public void read(NBTTagCompound nbt);
	public IClientCap<T> clone(NBTTagCompound nbt);
	
	public static enum Type
	{
		BOOLEAN,
		STRING,
		LONG,
		INT,
		SHORT,
		DOUBLE,
		FLOAT
	}

}
