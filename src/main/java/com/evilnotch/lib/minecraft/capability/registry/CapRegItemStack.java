package com.evilnotch.lib.minecraft.capability.registry;

import net.minecraft.item.ItemStack;

public abstract class CapRegItemStack implements ICapabilityRegistry<ItemStack>{
	
	@Override
	public Class getObjectClass() {
		return ItemStack.class;
	}

}
