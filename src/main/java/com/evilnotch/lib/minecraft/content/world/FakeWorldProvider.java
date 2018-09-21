package com.evilnotch.lib.minecraft.content.world;

import com.evilnotch.lib.main.MainJava;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWorldProvider extends WorldProvider{
	
	public FakeChunkGenerator gen = null;
	
	public FakeWorldProvider()
	{
		super();
		this.biomeProvider = new BiomeProvider(FakeWorld.getInfo());
	}
    public void init(World w)
    {
    	super.init();
    	this.world = w;
    	this.gen = new FakeChunkGenerator(this.world);
    }
	@Override
    public int getRespawnDimension(net.minecraft.entity.player.EntityPlayerMP player)
    {
    	return 0;
    }
	@Override
    public IChunkGenerator createChunkGenerator()
    {
    	return this.gen;
    }
	
	@Override
	public void generateLightBrightnessTable()
	{
		
	}
	@Override
	public boolean canRespawnHere()
	{
		return true;
	}
	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored()
	{
		return true;
	}
	@Override
	public DimensionType getDimensionType() {
		return DimensionType.OVERWORLD;
	}
	@Override
    public BlockPos getSpawnCoordinate()
    {
    	return new BlockPos(0,0,0);
    }
	@Override
	public int getAverageGroundLevel(){
		return 63;
	}
	@Override
    public void setDimension(int dim) {
    }
	@Override
    public String getSaveFolder() {
        return null;
     }
	 @Override
     public BlockPos getRandomizedSpawnPoint() {
        return new BlockPos(0, 64, 0);
     }
     @Override
     public boolean shouldMapSpin(String entity, double x, double y, double z) {
        return false;
     }
     @Override
     public Biome getBiomeForCoords(BlockPos pos) {
        return Biomes.PLAINS;
     }
     @Override
     public boolean isDaytime() {
        return true;
     }
     @Override
     public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
     }
     @Override
     public void calculateInitialWeather() {
     }
     @Override
     public void updateWeather() {
     }	
     @Override
     public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
        return false;
     }
     @Override
     public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        return false;
     }
     @Override
     public void setWorldTime(long time) {
     }
     @Override
     public long getSeed() {
        return 0L;
     }
     @Override
     public long getWorldTime() {
        return 1L;
     }

     @Override
     public boolean canMineBlock(EntityPlayer player, BlockPos pos) {
        return false;
     }
     @Override
     public boolean isBlockHighHumidity(BlockPos pos) {
        return false;
     }
     @Override
     public int getHeight() {
        return 255;
     }
     @Override
     public int getActualHeight() {
        return 255;
     }
     @Override
     public void resetRainAndThunder() {
     }
     @Override
     public boolean canDoLightning(Chunk chunk) {
        return false;
     }
     @Override
     public boolean canDoRainSnowIce(Chunk chunk) {
        return false;
     }
     @Override
     public BlockPos getSpawnPoint() {
        return new BlockPos(0, 64, 0);
     }
     @Override
     public boolean canCoordinateBeSpawn(int x, int z) {
        return true;
     }
     @Override
     public int getDimension()
     {
    	 return 0;
     }
     @Override
     public boolean canDropChunk(int x, int z)
     {
    	 return false;
     }

}
