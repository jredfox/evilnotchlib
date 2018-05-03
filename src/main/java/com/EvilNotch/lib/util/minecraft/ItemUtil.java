package com.EvilNotch.lib.util.minecraft;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemUtil {
	
	public static ResourceLocation getStringId(Item item)
	{
		return ForgeRegistries.ITEMS.getKey(item);
	}

}
