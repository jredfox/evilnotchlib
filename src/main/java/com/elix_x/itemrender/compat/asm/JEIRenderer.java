package com.elix_x.itemrender.compat.asm;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;

public class JEIRenderer{
	
	public static Set<Item> slowItems = new HashSet();

	public static void addSlowRenderer(Item i) {
		slowItems.add(i);
	}

	public static void removeSlowRenderer(Item i) {
		slowItems.remove(i);
	}
}
