package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItem;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public interface IBasicArmor<T extends ItemArmor> extends IBasicItem<T>{
	
	public ArmorSet getArmorSet();
	public void setArmorSet(ArmorSet set);
	public boolean hasFullArmorSet(ItemStack boots,ItemStack leggings,ItemStack chestplate,ItemStack helmet);
	
}
