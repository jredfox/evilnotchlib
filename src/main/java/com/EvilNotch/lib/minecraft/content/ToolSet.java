package com.EvilNotch.lib.minecraft.content;

import com.EvilNotch.lib.main.MainJava;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ToolSet {
	
	public ItemStack pickaxe;
	public ItemStack axe;
	public ItemStack sword;
	public ItemStack shovel;
	public ItemStack hoe;
	public ItemStack block;
	public ItemStack stick;
	public boolean allMetaBlock = true;
	public boolean allMetaStick = true;
	
	public ToolSet(Item p, Item a, Item s,Item shovel, Item h,ItemStack block,ItemStack stick,boolean allMetaBlock,boolean allMetaStick)
	{
		this(p, a, s,shovel, h,block,stick,allMetaBlock,allMetaStick,true);
	}
	
	public ToolSet(Item p, Item a, Item s,Item shovel, Item h,ItemStack block,ItemStack stick,boolean allMetaBlock,boolean allMetaStick,boolean register)
	{
		this.pickaxe = new ItemStack(p);
		this.axe = new ItemStack(a);
		this.sword = new ItemStack(s);
		this.shovel = new ItemStack(shovel);
		this.hoe = new ItemStack(h);
		this.block = block;
		this.stick = stick;
		this.allMetaBlock = allMetaBlock;
		this.allMetaStick = allMetaStick;
		if(register)
			MainJava.toolsets.add(this);
	}
	
	public ToolSet(ItemStack p, ItemStack a, ItemStack s,ItemStack shovel, ItemStack h,ItemStack block,ItemStack stick,boolean allMetaBlock,boolean allMetaStick)
	{
		this(p, a, s,shovel, h,block,stick,allMetaBlock,allMetaStick,true);
	}
	
	public ToolSet(ItemStack p, ItemStack a, ItemStack s,ItemStack shovel, ItemStack h,ItemStack block,ItemStack stick,boolean allMetaBlock,boolean allMetaStick,boolean register)
	{
		this.pickaxe = p;
		this.axe = a;
		this.sword = s;
		this.shovel = shovel;
		this.hoe = h;
		this.block = block;
		this.stick = stick;
		this.allMetaBlock = allMetaBlock;
		this.allMetaStick = allMetaStick;
		if(register)
			MainJava.toolsets.add(this);
	}

}
