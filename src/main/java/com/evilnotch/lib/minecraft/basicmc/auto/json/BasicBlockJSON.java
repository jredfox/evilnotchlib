package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BasicBlockJSON implements IBasicBlockJSON{
	
	public ResourceLocation loc;
	public Block block;
	public Item item;
	public ModelPart model;
	public boolean hasItemBlock = true;
	public String textureName;
	
	public BasicBlockJSON(Block b, ModelPart part)
	{
		this.loc = b.getRegistryName();
		this.block = b;
		this.item = ItemBlock.getItemFromBlock(b);
		this.model = part;
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

	public boolean getHasItemBlock()
	{
		return this.hasItemBlock;
	}
	
	public void setHasItemBlock(boolean b) 
	{
		this.hasItemBlock  = b;
	}
}
