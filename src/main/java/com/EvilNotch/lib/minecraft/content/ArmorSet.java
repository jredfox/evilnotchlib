package com.EvilNotch.lib.minecraft.content;

import com.EvilNotch.lib.main.MainJava;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorSet {
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public ItemStack block;
	public boolean allMetaBlock = true;
	
	public ArmorSet(Item h, Item c, Item l,Item b,ItemStack block,boolean allMeta)
	{
		this(h,c,l,b,block,allMeta,true);
	}
	
	/**
	 * All meta boolean for the itemstack block
	 */
	public ArmorSet(Item h, Item c, Item l,Item b,ItemStack block,boolean allMeta,boolean register)
	{
		this.helmet = new ItemStack(h);
		this.chestplate = new ItemStack(c);
		this.leggings = new ItemStack(l);
		this.boots = new ItemStack(b);
		this.block = block;
		this.allMetaBlock = allMeta;
		if(register)
			MainJava.armorsets.add(this);
	}
	
	public ArmorSet(ItemStack h, ItemStack c, ItemStack l,ItemStack b,ItemStack block,boolean allMeta)
	{
		this(h, c, l,b,block,allMeta,true);
	}
	
	public ArmorSet(ItemStack h, ItemStack c, ItemStack l,ItemStack b,ItemStack block,boolean allMeta,boolean register)
	{
		this.helmet = h;
		this.chestplate = c;
		this.leggings = l;
		this.boots = b;
		this.block = block;
		this.allMetaBlock = allMeta;
		if(register)
			MainJava.armorsets.add(this);
	}

}
