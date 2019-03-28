package com.evilnotch.lib.minecraft.basicmc.auto.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ICreativeTabHook {

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items);
}
