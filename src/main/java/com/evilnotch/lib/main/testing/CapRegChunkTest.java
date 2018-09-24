package com.evilnotch.lib.main.testing;

import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;
import com.evilnotch.lib.minecraft.content.capability.registry.CapRegChunk;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;

public class CapRegChunkTest extends CapRegChunk{

	@Override
	public void register(Chunk object, CapContainer c) {
		c.registerCapability(new ResourceLocation("timestamp:stamp"), new CapStamp());
		c.registerCapability(new ResourceLocation("a:b"), new CapTickTest());
	}

}
