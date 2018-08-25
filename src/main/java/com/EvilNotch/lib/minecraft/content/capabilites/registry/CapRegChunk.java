package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;

public abstract class CapRegChunk implements ICapRegistry{
	
	@Override
	public Class getObjectClass() {
		return Chunk.class;
	}

}
