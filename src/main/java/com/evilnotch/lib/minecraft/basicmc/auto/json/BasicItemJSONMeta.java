package com.evilnotch.lib.minecraft.basicmc.auto.json;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class BasicItemJSONMeta extends BasicItemJSON implements IBasicItemMetaJSON<Item>{
	
	public int maxMeta;
	
	public BasicItemJSONMeta(Item item, int max) 
	{
		super(item);
		this.maxMeta = max;
	}

	@Override
	public int getMaxMeta() 
	{
		return this.maxMeta;
	}

}
