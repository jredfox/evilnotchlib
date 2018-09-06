package com.EvilNotch.lib.minecraft.content.world;

import javax.annotation.Nullable;

import com.EvilNotch.lib.main.MainJava;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWorld extends World{
	
	public static WorldInfo world_info = null;

	public FakeWorld() 
	{
		super(new FakeSaveHandler(), getInfo(), new FakeWorldProvider(), getProfiler(), true);
        this.worldInfo.setDifficulty(EnumDifficulty.NORMAL);
        this.provider.setWorld(this);
        this.chunkProvider = this.createChunkProvider();
	}

	public static WorldInfo getInfo() {
		WorldInfo info = new WorldInfo(getSettings(),"fake_world");
		return info;
	}

	public static WorldSettings getSettings() {
		return new WorldSettings(0, GameType.SURVIVAL, true, false, WorldType.DEFAULT);
	}

	public static Profiler getProfiler() 
	{
		Profiler p = new Profiler();
		p.profilingEnabled = false;
		return p;
	}
	@Override
    public Biome getBiome(final BlockPos pos)
    {
    	return Biome.getBiome(1);
    }
	@Override
	public Biome getBiomeForCoordsBody(BlockPos pos) {
	    return Biome.getBiome(1);
	}
	@Override
    public MinecraftServer getMinecraftServer()
    {
		return null;
//    	return new FakeServer();
    }
	@Override
    public IBlockState getGroundAboveSeaLevel(BlockPos pos)
    {
    	 return this.getBlockState(new BlockPos(0,4,0));
    }
	@Override
	public IBlockState getBlockState(BlockPos pos)
	{
		return Blocks.GRASS.getDefaultState();
	}
	@Override
    public boolean isBlockLoaded(BlockPos pos)
    {
    	return false;
    }
	@Override
    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty)
    {
    	return false;
    }
	@Override
	public boolean isAreaLoaded(BlockPos center, int radius)
	{
	        return false;
	}
	@Override
	public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty)
	{
	    return false;
	}
	@Override
	public boolean isAreaLoaded(BlockPos from, BlockPos to)
	{
	     return false;
	}
	@Override
	public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty)
	{
	    return false;
	}
	@Override
	public boolean isAreaLoaded(StructureBoundingBox box)
	{
	     return false;
	}
	@Override
	public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty)
	{
	     return false;
	}
	@Override
	public boolean isAirBlock(BlockPos pos) {
	    return pos.getY() > 63;
	}
	@Override
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
	    return true;
	}
	@Override
	public boolean setBlockState(BlockPos pos,IBlockState newState) {
	    return true;
	}
	@Override
	public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
	    return this.setBlockToAir(pos);
	}
	@Override
	public boolean setBlockToAir(BlockPos pos)
	{
	  	return true;
	}
	@Override
    public void markAndNotifyBlock(BlockPos pos, @Nullable Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags)
    {
    	
    }
	@Override
    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean updateObservers)
    {
    	
    }
	@Override
    public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
    	
    }
	@Override
    public void markBlocksDirtyVertical(int x, int z, int y1, int y2)
    {
    	
    }
	@Override
	public void markBlockRangeForRenderUpdate(int p_147458_1_, int p_147458_2_, int p_147458_3_, int p_147458_4_, int p_147458_5_, int p_147458_6_) {
	}
	@Override
    public void observedNeighborChanged(BlockPos pos, final Block changedBlock, BlockPos changedBlockPos)
    {
		
    }
	@Override
    public void updateObservingBlocksAt(BlockPos pos, Block blockType)
    {
    	
    }
	@Override
    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean updateObservers)
    {
    	
    }
	@Override
    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide)
    {
    	
    }
	@Override
    public void neighborChanged(BlockPos pos, final Block blockIn, BlockPos fromPos)
    {
    	
    }
	@Override
    public boolean spawnEntity(Entity entityIn)
    {
    	return false;
    }
	@Override
    public void removeEntity(Entity entityIn)
    {
    	
    }
	@Override
    public void removeEntityDangerously(Entity entityIn)
    {
    	
    }
	@Override
    public BlockPos getPrecipitationHeight(BlockPos pos)
    {
    	return new BlockPos(0,64,0);
    }
	@Override
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos)
    {
    	return pos;
    }
	@Override
    public boolean isUpdateScheduled(BlockPos pos, Block blk)
    {
    	return false;
    }
	@Override
    public void updateEntities()
    {
    	
    }
	@Override
    public boolean addTileEntity(TileEntity tile)
    {
    	return false;
    }
	   
	public static IChunkProvider chunkP = null;
	@Override
	protected IChunkProvider createChunkProvider() {
		if(chunkP == null)
			chunkP = new FakeChunkProvider(this);
		return chunkP;
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) 
	{
		return false;
	}

}
