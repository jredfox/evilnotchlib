package com.elix_x.itemrender.compat.asm;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;

public class JEIRenderer{
	
	public static Set<Item> slowItems = new HashSet();
	
	public static void a(int i)
	{
		if(i > 1)
		{
			System.out.println();
		}
	}
}
