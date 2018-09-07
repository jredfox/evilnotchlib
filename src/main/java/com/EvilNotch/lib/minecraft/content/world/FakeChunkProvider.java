package com.EvilNotch.lib.minecraft.content.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class FakeChunkProvider implements IChunkProvider{
	
	public World world;
	public FakeChunkProvider(World w)
	{
		this.world = w;
	}

	@Override
	public Chunk getLoadedChunk(int x, int z) 
	{
		return FakeChunkLoader.getChunk(this.world);
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		return FakeChunkLoader.getChunk(this.world);
	}

	@Override
	public boolean tick() {
		return false;
	}

	@Override
	public String makeString() {
		return "stopUsingThisOnConstruction";
	}

	@Override
	public boolean isChunkGeneratedAt(int x, int z) 
	{
		return false;
	}

}
