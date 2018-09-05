package com.EvilNotch.lib.asm;

import java.util.Collection;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapRegHandler;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public class Caps implements ICapProvider{
	
	public Caps()
	{
		CapRegHandler.registerCapsToObj(this);
	}
	
	public CapContainer capContainer = new CapContainer();
	private List<TileEntity> loadedTileEntityList = null;
	private List<TileEntity> field_147482_g = null;
	
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
	
    public void tickTileCapsDeOb() 
    {
        for(TileEntity tile : this.loadedTileEntityList)
            ((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)tile).getCapContainer().tick(tile);
	}
    public void tickTileCapsOb() 
    {
        for(TileEntity tile : this.field_147482_g)
            ((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)tile).getCapContainer().tick(tile);
	}
    
	private IChunkProvider chunkProvider = null;
    public void tickChunksDeOb() {
        if(this.chunkProvider instanceof ChunkProviderServer)
        {
        	ChunkProviderServer cp = (ChunkProviderServer)this.chunkProvider;
        	Collection<Chunk> loadedChunks = cp.getLoadedChunks();
        	for(Chunk c : loadedChunks)
        	{
        		((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)c).getCapContainer().tick(c);
        	}
        }
        else if(this.chunkProvider instanceof ChunkProviderClient)
        {
        	ChunkProviderClient cp = (ChunkProviderClient)this.chunkProvider;
        	for(Chunk c : cp.chunkMapping.values())
        	{
        		((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)c).getCapContainer().tick(c);
        	}
        }
	}
    private IChunkProvider field_73020_y = null;
    public void tickChunksOb() {
        if(this.field_73020_y instanceof ChunkProviderServer)
        {
        	ChunkProviderServer cp = (ChunkProviderServer)this.field_73020_y;
        	Collection<Chunk> loadedChunks = cp.getLoadedChunks();
        	for(Chunk c : loadedChunks)
        	{
        		((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)c).getCapContainer().tick(c);
        	}
        }
        else if(this.field_73020_y instanceof ChunkProviderClient)
        {
        	ChunkProviderClient cp = (ChunkProviderClient)this.field_73020_y;
        	for(Chunk c : cp.chunkMapping.values())
        	{
        		((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)c).getCapContainer().tick(c);
        	}
        }
	}

	
}
