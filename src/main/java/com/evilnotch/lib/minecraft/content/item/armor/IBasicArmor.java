package com.evilnotch.lib.minecraft.content.item.armor;

import com.evilnotch.lib.minecraft.content.item.IBasicItem;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface IBasicArmor extends IBasicItem{
	
	public ArmorSet getArmorSet();
	public void setArmorSet(ArmorSet set);
	public boolean hasFullArmorSet(ItemStack boots,ItemStack leggings,ItemStack chestplate,ItemStack helmet);
	
}
