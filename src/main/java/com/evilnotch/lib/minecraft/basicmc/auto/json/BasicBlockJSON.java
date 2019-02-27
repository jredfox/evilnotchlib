package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BasicBlockJSON implements IBasicBlockJSON{
	
	public ResourceLocation loc;
	public Block block;
	public Item item;
	public ModelPart model;
	
	public BasicBlockJSON(Block b, ModelPart part)
	{
		this.loc = b.getRegistryName();
		this.block = b;
		this.item = ItemBlock.getItemFromBlock(b);
		this.model = part;
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

	@Override
	public ModelPart getModelPart() 
	{
		return this.model;
	}
}
