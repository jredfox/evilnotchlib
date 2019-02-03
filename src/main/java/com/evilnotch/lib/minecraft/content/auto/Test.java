package com.evilnotch.lib.minecraft.content.auto;

import com.evilnotch.lib.minecraft.content.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.content.auto.lang.LangRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.biome.Biome;

public class Test {
	
	public static void register(Object b,String region)
	{
		registerLang(b,region);
	}

	public static void registerLang(Object b, String region) 
	{
		String pre = "";
		if(b instanceof Block)
			pre = "tile.";
		else if(b instanceof Item)
			pre = "item.";
		else if(b instanceof Entity)
			pre = "entity.";
		else if(b instanceof Enchantment)
			pre = "enchantment.";
		else if(b instanceof CreativeTabs)
			pre = "creativetab.";
		else if(b instanceof Biome)
			pre = "biome.";
		else if(b instanceof Gui)
			pre = "gui.";
		else if(b instanceof Potion)
			pre = "potion.";
		
//		LangRegistry.add(new LangEntry(pre + b.getRegistryName().toString().replaceAll(":", ".") + ".name", regeion));
	}

}
