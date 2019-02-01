package com.evilnotch.lib.minecraft.content.item.armor;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.loader.LoaderItems;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ArmorSet {
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public ItemStack block;
	public boolean allMetaBlock = true;
	public boolean hasRecipe = true;
	public static final ItemStack air = new ItemStack(Blocks.AIR);
	
	/**
	 * use this constructor if you don't want recipes automated
	 */
	public ArmorSet(Item h, Item c, Item l,Item b)
	{
		this(h,c,l,b,air);
	}
	public ArmorSet(Item h, Item c, Item l,Item b,ItemStack block)
	{
		this(h,c,l,b,block,false);
	}
	public ArmorSet(Item h, Item c, Item l,Item b,ItemStack block, boolean allMeta)
	{
		this(h,c,l,b,block,allMeta,true);
	}
	public ArmorSet(Item h, Item c, Item l,Item b,ItemStack block, boolean allMeta,boolean register)
	{
		this(new ItemStack(h),new ItemStack(c),new ItemStack(l),new ItemStack(b),block,allMeta,register);
	}
	
	public ArmorSet(ItemStack h, ItemStack c, ItemStack l,ItemStack b)
	{
		this(h,c,l,b,air);
	}
	public ArmorSet(ItemStack h, ItemStack c, ItemStack l,ItemStack b,ItemStack block)
	{
		this(h,c,l,b,block,false);
	}
	public ArmorSet(ItemStack h, ItemStack c, ItemStack l,ItemStack b,ItemStack block,boolean allMeta)
	{
		this(h,c,l,b,block,allMeta,true);
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
			LoaderItems.armorsets.add(this);
		this.hasRecipe = !block.isEmpty();
	}

}
