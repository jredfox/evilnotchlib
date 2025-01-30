package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.minecraft.basicmc.auto.item.ArmorSet;

import net.minecraft.item.ItemStack;

public interface IBasicArmor {
	
	public ArmorSet getArmorSet();
	public void setArmorSet(ArmorSet set);
	public boolean hasFullArmorSet(ItemStack boots, ItemStack leggings, ItemStack chestplate, ItemStack helmet);
	
}
