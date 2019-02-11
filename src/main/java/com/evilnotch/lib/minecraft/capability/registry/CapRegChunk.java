package com.evilnotch.lib.minecraft.capability.registry;

import net.minecraft.world.chunk.Chunk;

public abstract class CapRegChunk implements ICapabilityRegistry<Chunk>{
	
	@Override
	public Class getObjectClass() 
	{
		return Chunk.class;
	}

}
