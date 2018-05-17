package com.EvilNotch.lib.minecraft;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemUtil {
	
	public static ResourceLocation getStringId(Item item)
	{
		return ForgeRegistries.ITEMS.getKey(item);
	}
	
	/**
	 * Returns the string of the classes tool if any
	 */
	public static String getToolClass(ItemStack stack)
	{
		String str = null;
		Set<String> list = stack.getItem().getToolClasses(stack);
		Iterator<String> it = list.iterator();
		while (it.hasNext())
		{
			String tool = it.next();
			if(tool != null)
				str = tool;
		}
		return str;
	}

	public static boolean hasToolClass(ItemStack stack,String blockclazz) {
		String str = null;
		Set<String> list = stack.getItem().getToolClasses(stack);
		Iterator<String> it = list.iterator();
		while (it.hasNext())
		{
			String tool = it.next();
			if(tool == null)
				continue;
			if(tool.equals(blockclazz))
				return true;
		}
		return false;
	}

}
