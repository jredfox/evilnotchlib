package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;

import net.minecraft.util.ResourceLocation;

public interface ICapProvider {
	
	public CapContainer getCapContainer();
	public void setCapContainer(CapContainer c);
	public ICapability getCapability(ResourceLocation loc);

}
