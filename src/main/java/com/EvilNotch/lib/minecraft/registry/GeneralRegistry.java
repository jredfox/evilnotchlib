package com.EvilNotch.lib.minecraft.registry;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.util.ResourceLocation;

public class GeneralRegistry {
	
	public static ArrayList<ICommand> cmds = new ArrayList();
	public static HashMap<ResourceLocation,Material> blockmats = new HashMap();
	public static HashMap<ResourceLocation,SoundType> soundTypes = new HashMap();
	
	public static void load()
	{
		//register block materials
		blockmats.put(new ResourceLocation("AIR"), Material.AIR);
		blockmats.put(new ResourceLocation("ANVIL"), Material.ANVIL);
		blockmats.put(new ResourceLocation("BARRIER"), Material.BARRIER);
		blockmats.put(new ResourceLocation("CACTUS"), Material.CACTUS);
		blockmats.put(new ResourceLocation("CAKE"), Material.CAKE);
		blockmats.put(new ResourceLocation("CIRCUITS"), Material.CIRCUITS);
		blockmats.put(new ResourceLocation("CARPET"), Material.CARPET);
		blockmats.put(new ResourceLocation("CLAY"), Material.CLAY);
		blockmats.put(new ResourceLocation("CLOTH"), Material.CLOTH);
		blockmats.put(new ResourceLocation("CORAL"), Material.CORAL);
		blockmats.put(new ResourceLocation("CRAFTED_SNOW"), Material.CRAFTED_SNOW);
		blockmats.put(new ResourceLocation("DRAGON_EGG"), Material.DRAGON_EGG);
		blockmats.put(new ResourceLocation("FIRE"), Material.FIRE);
		blockmats.put(new ResourceLocation("GLASS"), Material.GLASS);
		blockmats.put(new ResourceLocation("GOURD"), Material.GOURD);
		blockmats.put(new ResourceLocation("GRASS"), Material.GRASS);
		blockmats.put(new ResourceLocation("GROUND"), Material.GROUND);
		blockmats.put(new ResourceLocation("ICE"), Material.ICE);
		blockmats.put(new ResourceLocation("IRON"), Material.IRON);
		blockmats.put(new ResourceLocation("LAVA"), Material.LAVA);
		blockmats.put(new ResourceLocation("LEAVES"), Material.LEAVES);
		blockmats.put(new ResourceLocation("PACKED_ICE"), Material.PACKED_ICE);
		blockmats.put(new ResourceLocation("PISTON"), Material.PISTON);
		blockmats.put(new ResourceLocation("PLANTS"), Material.PLANTS);
		blockmats.put(new ResourceLocation("PORTAL"), Material.PORTAL);
		blockmats.put(new ResourceLocation("REDSTONE_LIGHT"), Material.REDSTONE_LIGHT);
		blockmats.put(new ResourceLocation("ROCK"), Material.ROCK);
		blockmats.put(new ResourceLocation("SAND"), Material.SAND);
		blockmats.put(new ResourceLocation("SNOW"), Material.SNOW);
		blockmats.put(new ResourceLocation("SPONGE"), Material.SPONGE);
		blockmats.put(new ResourceLocation("STRUCTURE_VOID"), Material.STRUCTURE_VOID);
		blockmats.put(new ResourceLocation("TNT"), Material.TNT);
		blockmats.put(new ResourceLocation("VINE"), Material.VINE);
		blockmats.put(new ResourceLocation("WATER"), Material.WATER);
		blockmats.put(new ResourceLocation("WEB"), Material.WEB);
		blockmats.put(new ResourceLocation("WOOD"), Material.WOOD);
		 
		//register soundtypes
		soundTypes.put(new ResourceLocation("ANVIL"), SoundType.ANVIL);
		soundTypes.put(new ResourceLocation("CLOTH"), SoundType.CLOTH);
		soundTypes.put(new ResourceLocation("GLASS"), SoundType.GLASS);
		soundTypes.put(new ResourceLocation("GROUND"), SoundType.GROUND);
		soundTypes.put(new ResourceLocation("LADDER"), SoundType.LADDER);
		soundTypes.put(new ResourceLocation("METAL"), SoundType.METAL);
		soundTypes.put(new ResourceLocation("PLANT"), SoundType.PLANT);
		soundTypes.put(new ResourceLocation("SAND"), SoundType.SAND);
		soundTypes.put(new ResourceLocation("SLIME"), SoundType.SLIME);
		soundTypes.put(new ResourceLocation("SNOW"), SoundType.SNOW);
		soundTypes.put(new ResourceLocation("STONE"), SoundType.STONE);
		soundTypes.put(new ResourceLocation("WOOD"), SoundType.WOOD);
	}
	
	public static void registerCommand(ICommand cmd){
		cmds.add(cmd);
	}
	public static void removeCmd(int index){
		cmds.remove(index);
	}
	public static ArrayList<ICommand> getCmdList(){
		return cmds;
	}

}
