package com.evilnotch.lib.minecraft.basicmc.auto;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BasicBlockJSON implements IBasicBlock{
	
	public ResourceLocation loc;
	public Block block;
	
	public BasicBlockJSON(Block b)
	{
		this.loc = b.getRegistryName();
		this.block = b;
	}
	
	/**
	 * used for the the object registry name isn't set for the json yet
	 */
	public BasicBlockJSON(Block b, ResourceLocation loc)
	{
		this.loc = loc;
		this.block = b;
	}

	@Override
	public ResourceLocation getResourceLocation() 
	{
		return this.loc;
	}

	@Override
	public Block getObject() 
	{
		return this.block;
	}
}
