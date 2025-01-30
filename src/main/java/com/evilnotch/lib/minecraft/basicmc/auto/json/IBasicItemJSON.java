package com.evilnotch.lib.minecraft.basicmc.auto.json;

import net.minecraft.util.ResourceLocation;

public interface IBasicItemJSON<T> {
	
	public T getObject();
	public ResourceLocation getResourceLocation();
}
