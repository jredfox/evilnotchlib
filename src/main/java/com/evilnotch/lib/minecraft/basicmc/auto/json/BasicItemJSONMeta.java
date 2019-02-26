package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.minecraft.basicmc.auto.BasicItemJSON;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItemMeta;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BasicItemJSONMeta extends BasicItemJSON implements IBasicItemMeta<Item>{
	
	public int maxMeta;
	
	public BasicItemJSONMeta(Item item, int max)
	{
		this(item, item.getRegistryName(), max);
	}
	
	public BasicItemJSONMeta(Item item, ResourceLocation loc, int max) 
	{
		super(item, loc);
		this.maxMeta = max;
	}

	@Override
	public int getMaxMeta() 
	{
		return this.maxMeta;
	}

}
