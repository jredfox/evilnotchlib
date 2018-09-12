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
	
	public static void a(int i)
	{
		if(i > 1)
		{
			System.out.println();
		}
	}
}
