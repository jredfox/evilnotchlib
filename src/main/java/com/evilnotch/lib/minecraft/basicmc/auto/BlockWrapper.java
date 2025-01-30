package com.evilnotch.lib.minecraft.basicmc.auto;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BlockWrapper {
	
	public ResourceLocation loc;
	public Block b;
	public ItemBlock itemblock;
	
	public BlockWrapper(Block b, ItemBlock itemblock)
	{
		this.b = b;
		this.loc = b.getRegistryName();
		this.itemblock = itemblock;
	}
	
}
