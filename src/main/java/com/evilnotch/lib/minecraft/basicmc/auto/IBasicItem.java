package com.evilnotch.lib.minecraft.basicmc.auto;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IBasicItem<T> {
	
	public T getObject();
	public ResourceLocation getResourceLocation();
	
	default public String getTextureName()
	{
		return getResourceLocation().getResourcePath();
	}
}
