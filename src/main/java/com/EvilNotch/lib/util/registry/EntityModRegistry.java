package com.EvilNotch.lib.util.registry;

import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class EntityModRegistry {
	
	public static void addSpawn(ResourceLocation loc,NBTTagCompound nbt, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, Biome biome)
    {
		addSpawnList(loc,nbt,weightedProb,min,max,typeOfCreature,biome);
    }
	public static void removeSpawn(ResourceLocation loc,NBTTagCompound nbt, EnumCreatureType typeOfCreature, Biome biome)
    {
		removeSpawnList(loc,nbt,typeOfCreature,biome);
    }
	
	 /**
     * Add a spawn entry for the supplied entity in the supplied {@link Biome} list
     * @param entityClass Entity class added
     * @param weightedProb Probability
     * @param min Min spawn count
     * @param max Max spawn count
     * @param typeOfCreature Type of spawn
     * @param biomes List of biomes
     */
    public static void addSpawnList(ResourceLocation loc,NBTTagCompound nbt, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, Biome... biomes)
    {
    	SpawnListEntryAdvanced advanced = new SpawnListEntryAdvanced(loc,weightedProb,min,max,nbt);
        for (Biome biome : biomes)
        {
            List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);
            if(!spawns.contains(advanced))
            	spawns.add(advanced);
            else{
            	//the .equals method doesn't check ints just checks class and nbt so reset values if already contains it
            	spawns.remove(advanced);
            	spawns.add(advanced);
            }
        }
    }
    public static void removeSpawnList(ResourceLocation loc,NBTTagCompound nbt, EnumCreatureType typeOfCreature, Biome... biomes)
    {
    	SpawnListEntryAdvanced advanced = new SpawnListEntryAdvanced(loc,-1,-1,-1,nbt);
    	 for (Biome biome : biomes)
         {
             List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);
             spawns.remove(advanced);
         }
    }

}
