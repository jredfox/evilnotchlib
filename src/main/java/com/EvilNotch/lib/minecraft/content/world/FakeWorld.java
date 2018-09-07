package com.EvilNotch.lib.minecraft.content.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.EvilNotch.lib.main.MainJava;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.MinecraftException;
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
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWorld extends World{
	
	public static WorldInfo world_info = null;

	public FakeWorld() 
	{
		super(new FakeSaveHandler(), getInfo(), new FakeWorldProvider(), getProfiler(), true);
        this.worldInfo.setDifficulty(EnumDifficulty.NORMAL);
        this.provider.setWorld(this);
        FakeWorldProvider pro = (FakeWorldProvider) this.provider;
        pro.init(this);
        this.chunkProvider = this.createChunkProvider();
        this.mapStorage = this.perWorldStorage;
        this.villageCollection = new VillageCollection(this);
//        this.initCapabilities();
	}

	public static WorldInfo getInfo() {
		if(world_info != null)
			return world_info;
		WorldInfo info = new WorldInfo(getSettings(),"fake_world");
		world_info = info;
		return world_info;
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
    	return Biomes.PLAINS;
    }
	@Override
	public Biome getBiomeForCoordsBody(BlockPos pos) {
	    return Biomes.PLAINS;
	}
	@Override
    public IBlockState getGroundAboveSeaLevel(BlockPos pos)
    {
    	 return this.getBlockState(new BlockPos(0,64,0));
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
    public boolean collidesWithAnyBlock(AxisAlignedBB bbox)
    {
    	return false;
    }
	@Override
    public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb)
    {
    	return new ArrayList();
    }
	@Override
	public void onEntityAdded(Entity par1Entity) 
	{
		
	}
	@Override
    public void removeEventListener(IWorldEventListener listener)
    {
    	
    }
	@Override
    public boolean isInsideWorldBorder(Entity entityToCheck)
    {
    	return true;
    }
	@Override
	public void onEntityRemoved(Entity par1Entity) 
	{
		
	}
	@Override
    public void addEventListener(IWorldEventListener listener)
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
	public void immediateBlockTick(BlockPos pos, IBlockState state, Random random)
	{
		   
	}
	@Override
	public void tick()
	{
		
	}
	@Override
    public void calculateInitialWeatherBody()
    {
    	
    }
	@Override
    protected void calculateInitialWeather()
    {
    	
    }
	@Override
    public void updateEntity(Entity ent)
    {
    	
    }
	@Override
	public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2) 
	{
		
	}
	@Override
	public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB)
	{
	  return true;
	}
	@Override
	public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB, Entity par2Entity) {
	     return true;
	}
	@Override
	public boolean checkBlockCollision(AxisAlignedBB par1AxisAlignedBB) {
	     return false;
	}
	@Override
    public boolean containsAnyLiquid(AxisAlignedBB bb)
    {
    	return false;
    }
	@Override
	public boolean isFlammableWithin(AxisAlignedBB bb)
	{
	   return false;
	}
	@Override
    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn)
    {
    	return false;
    }
	@Override
	public boolean isMaterialInBB(AxisAlignedBB par1AxisAlignedBB, Material par2Material) 
	{
	   return false;
	}
	@Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate <? super Entity > predicate)
    {
    	return new ArrayList(0);
    }
	@Override
    public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side)
    {
    	return true;
    }
	@Override
	@SideOnly(Side.CLIENT)
	public String getDebugLoadedEntities() {
	    return "";
	}
	@Override
    @SideOnly(Side.CLIENT)
    public String getProviderName()
    {
    	return "fake_chunk_provider";
    }
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
	     return null;
	}
	@Override
	public void setTileEntity(BlockPos pos, TileEntity tileEntityIn)
	{
		
	}
	@Override
	public void removeTileEntity(BlockPos pos) 
	{
		
	}
	@Override
	public void markTileEntityForRemoval(TileEntity tileEntityIn) {
	}
	@Override
	public boolean isBlockFullCube(BlockPos pos) {
	     return false;
	}
	@Override
    public boolean isBlockNormalCube(BlockPos pos, boolean _default)
    {
		return true;
    }
	@Override
	public void calculateInitialSkylight()
	{
		   
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
	@Override
    public void addTileEntities(Collection<TileEntity> tileEntityCollection)
    {
    	
    }
	@Override
    public boolean canSeeSky(BlockPos pos)
    {
		 return pos.getY() > 62;
    }
	@Override
    public boolean canBlockSeeSky(BlockPos pos)
    {
    	return pos.getY() > 62;
    }
	@Override
    public boolean isBlockTickPending(BlockPos pos, Block blockType)
    {
    	return false;
    }
	@Override
    public int getChunksLowestHorizon(int x, int z)
    {
    	return 0;
    }
	@Override
    public int getLightFor(EnumSkyBlock type, BlockPos pos)
    {
		return 14;
    }
	@Override
    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue)
    {
		
    }
	@Override
	public void updateWeather() {
	}
	@Override
	public void updateWeatherBody() {
	}
	@Override
    @SideOnly(Side.CLIENT)
    protected void playMoodSoundAndCheckLight(int x, int z, Chunk chunkIn)
    {
    	
    }
	@Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > clazz, AxisAlignedBB aabb, @Nullable Predicate <? super T > filter)
    {
    	return new ArrayList(0);
    }
	@Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > classEntity, AxisAlignedBB bb)
    {
    	return new ArrayList(0);
    }
	@Override
    public <T extends Entity> List<T> getEntities(Class <? extends T > entityType, Predicate <? super T > filter)
    {
    	return new ArrayList<T>(0);
    }
	@Override
    public <T extends Entity> List<T> getPlayers(Class <? extends T > playerType, Predicate <? super T > filter)
    {
    	return new ArrayList(0);
    }
	@Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb)
    {
    	return new ArrayList(0);
    }
	@Override
    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos)
    {
    	return false;
    }
	@Override
    public boolean checkLight(BlockPos pos)
    {
    	return false;
    }
	@Override
    public void notifyLightSet(BlockPos pos)
    {
    	
    }
	@Override
    public int getLight(BlockPos pos)
    {
    	return 14;
    }
	@Override
    public int getLightFromNeighbors(BlockPos pos)
    {
		return 14;
    }
	@Override
    public int getLight(BlockPos pos, boolean checkNeighbors)
    {
    	return 14;
    }
	@Override
    public boolean isDaytime()
    {
    	return true;
    }
	@Override
    public int getHeight(int x, int z)
    {
    	return 63;
    }
	@Override
    public int getSeaLevel()
    {
    	return 2;
    }
	@Override
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end)
    {
    	return null;
    }
	@Override
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid)
    {
    	return null;
    }
	@Override
    public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
    	return null;
    }
	@Override
    public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
    	
    }
	@Override
    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
    	
    }
	@Override
	public void playRecord(BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn)
	{
		   
	}
	@Override
	public int calculateSkylightSubtracted(float partialTicks)
	{
	   	return 6;
	}
	@Override
    public float getSunBrightnessFactor(float partialTicks)
    {
    	return 0.7F;
    }
	@Override
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos)
    {
    	return new BlockPos(0,63,0);
    }
	@Override
    public boolean canBlockFreezeBody(BlockPos pos, boolean noWaterAdj)
    {
    	return false;
    }
	@Override
    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj)
    {
    	return false;
    }
	@Override
    public boolean canBlockFreezeNoWater(BlockPos pos)
    {
    	return false;
    }
	@Override
	public boolean canBlockFreezeWater(BlockPos pos)
	{
		return false;
	}
	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight) {
	     return false;
	}
	@Override
	public boolean canSnowAtBody(BlockPos pos, boolean checkLight) {
	     return false;
	}
   @Override
   public boolean tickUpdates(boolean par1) {
      return false;
   }
   @Override
   public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2) {
      return null;
   }
   @Override
   public <T extends Entity> T findNearestEntityWithinAABB(Class <? extends T > entityType, AxisAlignedBB aabb, T closestTo)
   {
	   return null;
   }
   @Override
   public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity)
   {
	   
   }
   @Override
   public int countEntities(Class<?> entityType)
   {
	   return 0;
   }
   @Override
   public void loadEntities(Collection<Entity> entityCollection)
   {
	   
   }
   @Override
   public void unloadEntities(Collection<Entity> entityCollection)
   {
	   
   }
   @Override
   public boolean mayPlace(Block blockIn, BlockPos pos, boolean skipCollisionCheck, EnumFacing sidePlacedOn, @Nullable Entity placer)
   {
	   return true;
   }
   @Override
   public void checkSessionLock() throws MinecraftException
   {
	   
   }
   @Override
   public void setTotalWorldTime(long worldTime)
   {
	   
   }
   @Override
   public void setSpawnPoint(BlockPos pos)
   {
	   
   }
   @Override
   public long getSeed() 
   {
	  return 1L;
   }
   @Override
   public long getTotalWorldTime() 
   {
	   return 1L;
   }
   @Override
   public long getWorldTime() {
	   return 1L;
	}
   @Override
   public void setWorldTime(long par1) {
   }
   @Override
   public BlockPos getSpawnPoint()
   {
	   return new BlockPos(0,64,0);
   }
   @Override
   @SideOnly(Side.CLIENT)
   public void joinEntityInSurroundings(Entity entityIn)
   {
	   
   }
   @Override
   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos)
   {
	   return false;
   }
   @Override
   public boolean canMineBlockBody(EntityPlayer player, BlockPos pos)
   {
       return false;
   }
   @Override
   public void setEntityState(Entity par1Entity, byte par2) {
   }
   @Override
   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
   {
	   
   }
   @Override
   public void updateAllPlayersSleepingFlag() {
   }
   @Override
   public float getThunderStrength(float delta)
   {
	   return 0.0F;
   }
   @Override
   @SideOnly(Side.CLIENT)
   public void setThunderStrength(float strength)
   {
	   
   }
   @Override
   public float getRainStrength(float par1) {
	  return 0.0F;
   }
   @Override
   @SideOnly(Side.CLIENT)
   public void setRainStrength(float par1) {
   }
   @Override
   public boolean isThundering()
   {
	   return false;
   }
   @Override
   public boolean isRaining()
   {
	   return false;
   }
   @Override
   public boolean isRainingAt(BlockPos position)
   {
	   return false;
   }
   @Override
   public boolean isBlockinHighHumidity(BlockPos pos)
   {
	   return false;
   }
   @Override
   public void playBroadcastSound(int id, BlockPos pos, int data)
   {
	   
   }
   @Override
   public void playEvent(int type, BlockPos pos, int data)
   {
	   
   }
   @Override
   public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data)
   {
	   
   }
   @Override
   public int getHeight()
   {
	   return 255;
   }
   @Override
   public int getActualHeight()
   {
	   return 255;
   }
   @Override
   public BlockPos getHeight(BlockPos pos)
   {
	   return new BlockPos(0,64,0);
   }
   @Override
   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
   {
	   
   }
   @Override
   public void updateComparatorOutputLevel(BlockPos pos, Block blockIn)
   {
	   
   }
   @Override
   public DifficultyInstance getDifficultyForLocation(BlockPos pos)
   {
	   return new DifficultyInstance(EnumDifficulty.NORMAL, 1L, 0L, 0L);
   }
   @Override
   public boolean isSideSolid(BlockPos pos, EnumFacing side)
   {
	   if(pos.getY() > 63)
		   return false;
	   return true;
   }
   @Override
   public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
   {
	   return this.isSideSolid(pos, side);
   }
   @Override
   public EnumDifficulty getDifficulty()
   {
	   return EnumDifficulty.NORMAL;
   }
   @Override
   public IBlockState getBlockState(BlockPos pos)
   {
	   if(pos.getY() <= 63)
		   return Blocks.GRASS.getDefaultState();
	   return Blocks.AIR.getDefaultState();
   }
	/**
	 * this is commentded out when porting bpkrscore but, no particles?????
	 */
	@Deprecated
	@Override
    @SideOnly(Side.CLIENT)
    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
    	
    }
	@Override
    @Nullable
    public Entity getEntityByID(int id)
    {
    	return null;
    }
	@Override
	public Chunk getChunkFromChunkCoords(int x, int z) {
		return this.chunkProvider.getLoadedChunk(x, z);
	}
	@Override
    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
    	return 0;
    }
	@Override
    public int getStrongPower(BlockPos pos)
    {
    	return 0;
    }
	@Override
    public boolean isSidePowered(BlockPos pos, EnumFacing side)
    {
    	return false;
    }
	@Override
	public int getRedstonePower(BlockPos pos, EnumFacing facing)
	{
	  return 0;
	}
	@Override
    public boolean isBlockPowered(BlockPos pos)
    {
    	return false;
    }
	@Override
    public void markTileEntitiesInChunkForRemoval(Chunk chunk)
    {
    	
    }
	
	@Override
    public int isBlockIndirectlyGettingPowered(BlockPos pos)
    {
    	return 0;
    }
    @Nullable
    @Override
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance)
    {
    	return null;
    }
    @Nullable
    @Override
    public EntityPlayer getNearestPlayerNotCreative(Entity entityIn, double distance)
    {
    	return null;
    }
    @Nullable
    @Override
    public EntityPlayer getClosestPlayer(double posX, double posY, double posZ, double distance, boolean spectator)
    {
    	return null;
    }
    @Override
    @Nullable
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate)
    {
    	return null;
    }
    @Override
    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range)
    {
    	return false;
    }
    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance)
    {
    	return null;
    }
    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance)
    {
    	return null;
    }
    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> predicate)
    {
    	return null;
    }
    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByName(String name)
    {
    	return null;
    }
    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByUUID(UUID uuid)
    {
    	return null;
    }
	@Override
    public boolean addWeatherEffect(Entity entityIn)
    {
    	return false;
    }
	@Override
    public Iterator<Chunk> getPersistentChunkIterable(Iterator<Chunk> chunkIterator)
    {
    	return super.getPersistentChunkIterable(chunkIterator);
    }
	@Override
    @SideOnly(Side.CLIENT)
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos)
    {
    	return 14;
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
