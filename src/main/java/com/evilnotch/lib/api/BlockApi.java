package com.evilnotch.lib.api;

import java.util.HashSet;
import java.util.Set;

import com.evilnotch.lib.main.loader.LoaderFields;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.util.BlockUtil;
import com.evilnotch.lib.minecraft.util.ItemUtil;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;


public class BlockApi {
	
	public static Set<ResourceLocation> blocksModified = new HashSet<ResourceLocation>();
	
	//getters
	public static float getBlockHardness(Block b)
	{
		return b.blockHardness;
	}
	
	public static ResourceLocation getBlockString(Block b)
	{
		return BlockUtil.getStringId(b);
	}
	
	public static ResourceLocation getItemString(Item item)
	{
		return ItemUtil.getStringId(item);
	}
	
	public static Material getblockmaterial(Block b) 
	{
		return b.blockMaterial;
	}
	
	public static String getHarvestTool(Block b,IBlockState state)
	{
		return b.getHarvestTool(state);
	}
	
	public static float getBlastResistence(Block b) 
	{
		return b.blockResistance;
	}
	
	public static void setStepSound(Block b,SoundType type)
	{
		b.blockSoundType = type;
	}
	
	public static void setTransLucent(Block b, boolean boole)
	{
		b.translucent = boole;
	}
	
	public static void setBlasResistence(Block b, float f)
	{	
		b.blockResistance = f;
	}
	
	public static void setEnableStats(Block b, boolean boole)
	{
		b.enableStats = boole;
	}
	
	public static void setMaterial(Block b, Material m, boolean mapColor)
	{
		b.blockMaterial = m;
		if(mapColor)
			b.blockMapColor = m.getMaterialMapColor();
	}
	
	public static void setMapColor(Block b, MapColor m)
	{
		b.blockMapColor = m;
	}
	
	public static void setHarvestTool(Block b,int meta,String s)
	{
		String[] tools = (String[]) ReflectionUtil.getObject(b, Block.class, "harvestTool");
		tools[meta] = s;
	}
	
	public static void printBlock(Block b)
	{
		String tool = b.getHarvestTool(b.getDefaultState());
		float blockHardness = BlockApi.getBlockHardness(b);
		float blastResistance = BlockApi.getBlastResistence(b);
		int harvestLvl = b.getHarvestLevel(b.getDefaultState());
		SoundType sound = b.getSoundType();
		System.out.println(b.getRegistryName() + " tool:" + tool + " mat rock:" + (BlockApi.getblockmaterial(b) == Material.ROCK)+",hardness:" + blockHardness + ",blast:" + blastResistance + ",harvestlvl:" + harvestLvl + ",sound:" + (sound == SoundType.STONE) + 
				" flameE:" + Blocks.FIRE.getEncouragement(b) + " flame:" + Blocks.FIRE.getFlammability(b) + 
				" slip:" + b.slipperiness + " light:" + b.getLightValue(b.getDefaultState()));
	}
	
	/**
	 * Only supports whatever is registered via my mod
	 * Get block material from material registry
	 */
	public static Material getMatFromReg(ResourceLocation s)
	{
		return GeneralRegistry.blockmats.get(s);
	}
	
	public static SoundType getSoundType(ResourceLocation s)
	{
		return GeneralRegistry.soundTypes.get(s);
	}
	
	public static ResourceLocation getMaterialLoc(Material mat)
	{
		if(mat == null)
			return null;
		return (ResourceLocation)JavaUtil.getMemoryLocKey(GeneralRegistry.blockmats, mat);
	}
	
	public static ResourceLocation getSoundTypeLoc(SoundType sound) 
	{
		if(sound == null)
			return null;
		return (ResourceLocation)JavaUtil.getMemoryLocKey(GeneralRegistry.soundTypes, sound);
	}

	public static String getHarvestTool(Block b, int meta) 
	{
		String[] tools = (String[]) ReflectionUtil.getObject(b, Block.class, "harvestTool");
		return tools[meta];
	}
	
}
