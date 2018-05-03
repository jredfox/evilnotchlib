package com.EvilNotch.lib.Api;

import java.util.Iterator;
import java.util.Map;

import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.minecraft.BlockUtil;
import com.EvilNotch.lib.minecraft.ItemUtil;
import com.EvilNotch.lib.minecraft.registry.GeneralRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;


public class BlockApi {
	
	//getters
	public static float getBlockHardness(Block b)
	{
		return (float)ReflectionUtil.getObject(b, Block.class, FieldAcess.blockHardness);
	}
	public static ResourceLocation getBlockString(Block b)
	{
		return BlockUtil.getStringId(b);
	}
	public static ResourceLocation getItemString(Item item)
	{
		return ItemUtil.getStringId(item);
	}
	public static Material getblockmaterial(Block b) {
		return (Material) ReflectionUtil.getObject(b, Block.class, FieldAcess.blockMaterial);
	}
	public static String getHarvestTool(Block b,int meta){
		return ((String[]) ReflectionUtil.getObject(b, Block.class, FieldAcess.harvestTool))[meta];
	}
	public static float getBlastResistence(Block b) {
		return (Float)ReflectionUtil.getObject(b, Block.class, FieldAcess.blastResistence);
	}
	
	//setters
	public static void setHarvestTool(Block b,int meta,String s)
	{
		String[] tools = (String[]) ReflectionUtil.getObject(b, Block.class, FieldAcess.harvestTool);
		tools[meta] = s;
	}
	public static void setStepSound(Block b,SoundType type)
	{
		ReflectionUtil.setObject(b, type, Block.class, FieldAcess.blockSoundType);
	}
	public static void setTransLucent(Block b, boolean boole)
	{
		ReflectionUtil.setObject(b, boole, Block.class, FieldAcess.translucent);
	}
	public static void setBlasResistence(Block b, float f)
	{	
		ReflectionUtil.setObject(b, f, Block.class, FieldAcess.blockResistance);
	}
	public static void setEnableStats(Block b, boolean boole)
	{
		ReflectionUtil.setObject(b, boole, Block.class, FieldAcess.enableStats);
	}
	public static void setisTileProvider(Block b, boolean boole)
	{
		ReflectionUtil.setObject(b, boole, Block.class, FieldAcess.isTileProvider);
	}
	public static void setMaterial(Block b, Material m)
	{
		setMaterial(b,m,false);
	}
	public static void setMaterial(Block b, Material m,boolean setMatColor)
	{
		ReflectionUtil.setObject(b, m, Block.class, FieldAcess.blockMaterial);
		if(setMatColor)
			ReflectionUtil.setObject(b, m.getMaterialMapColor(), Block.class, FieldAcess.blockMaterialMapColor);
	}
	public static void printBlock(Block b){
		String tool = b.getHarvestTool(b.getDefaultState());
		float blockHardness = BlockApi.getBlockHardness(b);
		float blastResistance = BlockApi.getBlastResistence(b);
		int harvestLvl = b.getHarvestLevel(b.getDefaultState());
		SoundType sound = b.getSoundType();
		System.out.println("tool:" + tool + ",hardness:" + blockHardness + ",blast:" + blastResistance + ",harvestlvl:" + harvestLvl + ",sound:" + (sound == SoundType.STONE) + 
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
	public static ResourceLocation getMaterialLoc(Material mat){
		if(mat == null)
			return null;
		return (ResourceLocation)JavaUtil.getMemoryLocKey(GeneralRegistry.blockmats,mat);
	}
	public static ResourceLocation getSoundTypeLoc(SoundType sound) {
		if(sound == null)
			return null;
		return (ResourceLocation)JavaUtil.getMemoryLocKey(GeneralRegistry.soundTypes,sound);
	}
	public static void printMaterialMapColor(Block b) {
		MapColor color = (MapColor) ReflectionUtil.getObject(b, Block.class, "blockMapColor");
		System.out.println("colorValue:" + color.colorValue);
	}
	
}
