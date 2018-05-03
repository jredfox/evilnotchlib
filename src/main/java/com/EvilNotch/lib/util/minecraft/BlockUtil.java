package com.EvilNotch.lib.util.minecraft;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
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
    public static int getHarvestLevel(Block b)
	{
		int lvl = -1;
		for(int i=0;i<16;i++)
		{
			int harvest = b.getHarvestLevel(b.getDefaultState());
			if(harvest > lvl)
				lvl = harvest;
		}
		return lvl;
	}

}
