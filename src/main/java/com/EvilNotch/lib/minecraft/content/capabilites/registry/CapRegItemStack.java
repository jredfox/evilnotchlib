package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public abstract class CapRegItemStack implements ICapRegistry{
	
	@Override
	public Class getObjectClass() {
		return ItemStack.class;
	}

}
