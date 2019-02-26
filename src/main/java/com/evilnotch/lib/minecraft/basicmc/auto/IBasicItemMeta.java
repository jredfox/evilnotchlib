package com.evilnotch.lib.minecraft.basicmc.auto;

import net.minecraft.item.Item;

public interface IBasicItemMeta<T extends Item> extends IBasicItem<T>{
	
	public int getMaxMeta();

}
