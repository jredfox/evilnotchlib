package com.evilnotch.lib.minecraft.capability;

import net.minecraft.util.ResourceLocation;

public interface ICapabilityProvider {
	
	public CapContainer getCapContainer();
	public void setCapContainer(CapContainer c);
	public ICapability getCapability(ResourceLocation loc);
	public ICapabilityTick getCapabilityTick(ResourceLocation loc);

}
