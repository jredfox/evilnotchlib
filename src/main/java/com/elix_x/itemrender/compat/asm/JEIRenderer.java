package com.elix_x.itemrender.compat.asm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.EvilNotch.lib.minecraft.content.ArmorMat;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class JEIRenderer{
	
	public static Set<Item> slowItems = new HashSet();
	public static ArrayList list = null;

	public static void addSlowRenderer(Item i) 
	{
		int index = 0;
		if(index < 1)
		{
			slowItems.add(i);
		}
	}

	public static void removeSlowRenderer(Item i) 
	{
		slowItems.remove(i);
	}
}
