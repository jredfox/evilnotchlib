package com.evilnotch.lib.minecraft.capability.client;

import net.minecraft.util.ResourceLocation;

public interface IClientCap<T> {
	
	public ResourceLocation getId();
	public T get();
	public void set(T o);

}
