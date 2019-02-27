package com.evilnotch.lib.minecraft.basicmc.block.item;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.minecraft.basicmc.block.IBasicBlockMeta;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock{

	public ItemBlockMeta(Block block) 
	{
		super(block);
		if(!(block instanceof IBasicBlockMeta))
			throw new IllegalArgumentException("Block must implement the IMetaName interface:" + block.getRegistryName().toString());
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName() + "_" + ((IBasicBlockMeta)this.block).getPropertyName(stack.getMetadata());
	}
	
	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

}
