package com.evilnotch.lib.api;

import java.util.HashSet;
import java.util.Set;

import com.evilnotch.lib.minecraft.BlockUtil;
import com.evilnotch.lib.minecraft.ItemUtil;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
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
	public static void setMaterial(Block b, Material m,String toolclazz)
	{
		setMaterial(b,m,false,toolclazz);
	}
	public static void setMaterial(Block b, Material m,boolean setMatColor,String toolclazz)
	{
		ReflectionUtil.setFinalObject(b, m, Block.class, FieldAcess.blockMaterial);
		if(setMatColor)
			ReflectionUtil.setFinalObject(b, m.getMaterialMapColor(), Block.class, FieldAcess.blockMaterialMapColor);
		
		//if people want null tool classes they can use setHarvestLevel
		if(toolclazz != null)
		{
			java.util.Iterator<IBlockState> it = b.getBlockState().getValidStates().iterator();
			while (it.hasNext())
			{
				IBlockState state = it.next();
				setHarvestTool(b,b.getMetaFromState(state),toolclazz);
			}
		}
        blocksModified.add(b.getRegistryName() );
	}
	public static void printBlock(Block b){
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
