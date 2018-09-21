package com.evilnotch.lib.main.testing;

import com.evilnotch.lib.minecraft.content.capabilites.primitive.CapString;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapRegWorld;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CapRegWorldTest extends CapRegWorld{

	@Override
	public void register(World object, CapContainer c) 
	{
		c.registerCapability(new ResourceLocation("a:c"), new CapString<World>("timeStampVersion"));
		c.registerCapability(new ResourceLocation("a:b"), new CapTickTest());
	}

}
