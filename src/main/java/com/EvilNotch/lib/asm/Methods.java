package com.EvilNotch.lib.asm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Methods {
	
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
    	if(!(entityIn instanceof EntityPlayer))
    		entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }

}
