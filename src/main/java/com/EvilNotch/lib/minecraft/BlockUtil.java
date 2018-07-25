package com.EvilNotch.lib.minecraft;

import java.util.List;

import com.EvilNotch.lib.Api.BlockApi;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	/**
	 * Gets harvest tool from block based on meta's best guess...
	 * @param blockh
	 * @return
	 */
	public static String getToolFromBlock(Block b,int meta,IBlockState state)
	{
		String itool = b.getHarvestTool(state);
		
		if(itool == null)
			itool = getActualToolFromBlock(b,meta);
		
		if(itool == null)
			return getToolFromFirstMeta(b);
		
		return itool;
	}
	public static String getToolFromFirstMeta(Block b) 
	{
        for (int i=0;i<16;i++)
        {
             String toolit = BlockApi.getHarvestTool(b, i);
             if(toolit != null)
            	 return toolit;
        }
        return null;
	}

	public static String getActualToolFromBlock(Block b,int meta)
	{
		return BlockApi.getHarvestTool(b, meta);
	}
	
	public static String getToolFromHighestMeta(Block b)
	{
		String itool = null;
        for (int i=0;i<16;i++)
        {
             String toolit = BlockApi.getHarvestTool(b, i);
             if(toolit != null)
            	 itool = toolit;
        }
        return itool;
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

}
