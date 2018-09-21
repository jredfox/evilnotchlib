package com.evilnotch.lib.main;

import com.evilnotch.lib.main.testing.CapTickTest;
import com.evilnotch.lib.main.testing.CapWorldSeed;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.evilnotch.lib.minecraft.content.capabilites.registry.ICapProvider;
import com.evilnotch.lib.minecraft.content.capabilites.registry.ICapRegistry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldInfo;

public class CapRegWorldInfoTst implements ICapRegistry<WorldInfo> {

	@Override
	public void register(WorldInfo wi, CapContainer c) 
	{
		c.registerCapability(new ResourceLocation("a:b"), new CapTickTest());
		c.registerCapability(new ResourceLocation("a:c"), new CapWorldSeed());
	}

	@Override
	public Class getObjectClass() {
		return WorldInfo.class;
	}

}
