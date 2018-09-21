package com.evilnotch.lib.asm;

import java.util.Collection;
import java.util.List;

import com.evilnotch.lib.minecraft.EntityUtil;
import com.evilnotch.lib.minecraft.content.capabilites.ICapability;
import com.evilnotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapRegHandler;
import com.evilnotch.lib.minecraft.content.capabilites.registry.ICapProvider;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public class Caps implements ICapProvider{
	
	public CapContainer capContainer = new CapContainer();
	
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public CapContainer getCapContainer()
	{
		return this.capContainer;
	}
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public void setCapContainer(CapContainer c)
	{
		this.capContainer = c;
	}
	/**
	 * this is a universal method for getting a capability from the ICapProvider object
	 */
	public ICapability getCapability(ResourceLocation loc)
	{
		return this.capContainer.getCapability(loc);
	}
	
}
