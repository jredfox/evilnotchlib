package com.evilnotch.lib.minecraft.basicmc.auto.json;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BasicItemJSON implements IBasicItemJSON<Item>{
	
	public Item item;
	public ResourceLocation loc;
	public String textureName;
	
	public BasicItemJSON(Item item)
	{
		this.item = item;
		this.loc = item.getRegistryName();
		this.textureName = this.loc.getResourcePath();
	}

	@Override
	public ResourceLocation getResourceLocation() 
	{
		return this.loc;
	}

	@Override
	public Item getObject()
	{
		return this.item;
	}

}
