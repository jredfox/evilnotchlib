package com.EvilNotch.lib.minecraft.content.world;

import java.io.IOException;

import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class FakeChunkLoader implements IChunkLoader{

	public static Chunk chunk = null;
	@Override
	public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
		return getChunk(worldIn);
	}

	public static Chunk getChunk(World world) {
		if(chunk == null)
			chunk = new Chunk(world,0,0);
		System.out.println(chunk);
		return chunk;
	}

	@Override
	public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException 
	{
		
	}

	@Override
	public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException 
	{
		
	}

	@Override
	public void chunkTick() 
	{
		
	}

	@Override
	public void flush() 
	{
		
	}

	@Override
	public boolean isChunkGeneratedAt(int x, int z) 
	{
		return false;
	}

}
