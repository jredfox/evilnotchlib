package com.evilnotch.lib.minecraft.basicmc.auto.json;

import net.minecraft.item.Item;

public interface IBasicItemMetaJSON<T extends Item> extends IBasicItemJSON<T>{
	
	public int getMaxMeta();

}
