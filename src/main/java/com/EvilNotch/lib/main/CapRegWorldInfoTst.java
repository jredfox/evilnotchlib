package com.EvilNotch.lib.main;

import com.EvilNotch.lib.main.testing.CapTickTest;
import com.EvilNotch.lib.main.testing.CapWorldSeed;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapRegistry;

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
