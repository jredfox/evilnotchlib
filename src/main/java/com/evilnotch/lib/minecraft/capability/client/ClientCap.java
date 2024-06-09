package com.evilnotch.lib.minecraft.capability.client;

import net.minecraft.util.ResourceLocation;

public class ClientCap<T> implements IClientCap<T> {
	
	public ResourceLocation id;
	public T value;
	
	public ClientCap(ResourceLocation loc, T o)
	{
		this.id = loc;
		this.value = o;
	}

	@Override
	public ResourceLocation getId() 
	{
		return this.id;
	}

	@Override
	public T get() {
		return this.value;
	}

	@Override
	public void set(T o) {
		this.value = o;
	}

}
