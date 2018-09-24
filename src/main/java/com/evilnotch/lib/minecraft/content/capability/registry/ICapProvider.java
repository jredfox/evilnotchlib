package com.evilnotch.lib.minecraft.content.capability.registry;

import com.evilnotch.lib.minecraft.content.capability.ICapability;

import net.minecraft.util.ResourceLocation;

public interface ICapProvider {
	
	public CapContainer getCapContainer();
	public void setCapContainer(CapContainer c);
	public ICapability getCapability(ResourceLocation loc);

}
