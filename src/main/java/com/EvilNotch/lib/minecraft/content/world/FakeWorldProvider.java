package com.EvilNotch.lib.minecraft.content.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWorldProvider extends WorldProvider{
	
	public FakeWorldProvider()
	{
		super();
		this.biomeProvider = new BiomeProvider(FakeWorld.getInfo());
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
     public int getRespawnDimension(EntityPlayerMP player) {
        return 0;
     }
     @Override
     public Biome getBiomeForCoords(BlockPos pos) {
        return Biome.REGISTRY.getObjectById(1);
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
        return 256;
     }
     @Override
     public int getActualHeight() {
        return 256;
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

}
