package com.evilnotch.lib.minecraft.util;

import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockUtil {
	
	public static ResourceLocation getStringId(Block block)
	{
		return ForgeRegistries.BLOCKS.getKey(block);
	}
	
    public static int getHarvestLevel(IBlockState state) 
    {
    	Block b = state.getBlock();
    	return b.getHarvestLevel(state);
	}
	
	/**
	 * Gets harvest tool from block based on meta's best guess...
	 * @param blockh
	 * @return
	 */
	public static String getToolFromBlock(Block b,IBlockState state)
	{
		String itool = b.getHarvestTool(state);
		
		if(itool == null)
			itool = getHarvestTool(b,state.getBlock().getMetaFromState(state));
		
		if(itool == null)
			return getToolFromFirstMeta(b);
		
		return itool;
	}
	
	public static String getToolFromFirstMeta(Block b) 
	{
        for (int i=0;i<16;i++)
        {
             String toolit = getHarvestTool(b, i);
             if(toolit != null)
            	 return toolit;
        }
        return null;
	}
	
	public static String getToolFromHighestMeta(Block b)
	{
        for (int i=0;i<16;i++)
        {
             String toolit = getHarvestTool(b, i);
             if(toolit != null)
            	 return toolit;
        }
        return null;
	}
	
    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(Block b,World worldIn,EntityPlayer player,boolean silktouch, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            List<ItemStack> drops = b.getDrops(worldIn, pos, state, fortune); // use the old method until it gets removed, for backward compatibility
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, fortune, chance, silktouch, player);

            for (ItemStack drop : drops)
            {
                if (worldIn.rand.nextFloat() <= chance)
                    b.spawnAsEntity(worldIn, pos, drop);
            }
        }
    }
    
    public static void DropBlock(World world, BlockPos p, ItemStack stack)
    {
    	 if (!world.isRemote && world.getGameRules().hasRule("doTileDrops") && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
         {
            float f = 0.7F;
            double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            int x = p.getX();
            int y = p.getY();
            int z = p.getZ();
            EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, stack);
            entityitem.setPickupDelay(10);
            world.spawnEntity(entityitem);
        }
    }
    
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
		float blockHardness = getBlockHardness(b);
		float blastResistance = getBlastResistence(b);
		int harvestLvl = b.getHarvestLevel(b.getDefaultState());
		SoundType sound = b.getSoundType();
		System.out.println(b.getRegistryName() + " tool:" + tool + " mat rock:" + (getblockmaterial(b) == Material.ROCK)+",hardness:" + blockHardness + ",blast:" + blastResistance + ",harvestlvl:" + harvestLvl + ",sound:" + (sound == SoundType.STONE) + 
				" flameE:" + Blocks.FIRE.getEncouragement(b) + " flame:" + Blocks.FIRE.getFlammability(b) + 
				" slip:" + b.slipperiness + " light:" + b.getLightValue(b.getDefaultState()));
	}

	public static String getHarvestTool(Block b, int meta) 
	{
		String[] tools = (String[]) ReflectionUtil.getObject(b, Block.class, "harvestTool");
		return tools[meta];
	}
	
	public static String getPropertyValue(IBlockState state, IProperty p)
	{
		return JavaUtil.splitFirst(getBlockStateName(state, p), '=')[1];
	}
	
	public static String getBlockStateName(IBlockState state, IProperty p)
	{	
		if(p instanceof PropertyInteger)
		{
			String i = state.getValue(p).toString();
			return  p.getName() + "=" + i;
		}
		else if(p instanceof PropertyBool)
		{
			String bool = state.getValue(p).toString();
			return p.getName() + "=" + bool;
		}
		else if(p instanceof PropertyDirection)
		{
			String dir = state.getValue(p).toString();
			return p.getName() + "=" + dir;
		}
		else if(p instanceof PropertyEnum)
		{
			IStringSerializable name = (IStringSerializable) state.getValue(p);
			return p.getName() + "=" + name.getName();
		}
		return null;
	}

}
