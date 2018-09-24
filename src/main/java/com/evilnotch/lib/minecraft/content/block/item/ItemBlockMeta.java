package com.evilnotch.lib.minecraft.content.block.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock{

	public ItemBlockMeta(Block block) {
		super(block);
		if(!(block instanceof IMetaName))
			throw new IllegalArgumentException("Block must implement the IMetaName interface:" + block.getRegistryName().toString());
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		return super.getUnlocalizedName() + ((IMetaName)this.block).getSpecialName(stack);
	}
	@Override
	public int getMetadata(int meta){
		return meta;
	}

}
