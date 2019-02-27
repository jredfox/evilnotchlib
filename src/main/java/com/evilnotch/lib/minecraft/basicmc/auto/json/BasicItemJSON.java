package com.evilnotch.lib.minecraft.basicmc.auto.json;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BasicItemJSON implements IBasicItemJSON<Item>{
	
	public Item item;
	public ResourceLocation loc;
	
	public BasicItemJSON(Item item)
	{
		this.item = item;
		this.loc = item.getRegistryName();
	}
	
	/**
	 * used for the the object registry name isn't set for the json yet
	 */
	public BasicItemJSON(Item item, ResourceLocation loc)
	{
		this.item = item;
		this.loc = loc;
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
