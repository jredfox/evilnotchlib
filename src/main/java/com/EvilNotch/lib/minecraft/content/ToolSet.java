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
	
	public ToolSet(Item pick,Item axe,Item sword,Item shovel,Item hoe)
	{
		this(pick,axe,sword,shovel,hoe,false);
	}
	public ToolSet(Item pick,Item axe,Item sword,Item shovel,Item hoe,boolean recipe)
	{
		this(pick,axe,sword,shovel,hoe,recipe,false,false);
	}
	public ToolSet(Item pick,Item axe,Item sword,Item shovel,Item hoe,boolean recipe,boolean allMetaBlock,boolean allMetaStick)
	{
		this(new ItemStack(pick),new ItemStack(axe),new ItemStack(sword),new ItemStack(shovel),new ItemStack(hoe),recipe,allMetaBlock,allMetaStick);
	}
	
	public ToolSet(ItemStack pick,ItemStack axe,ItemStack sword,ItemStack shovel,ItemStack hoe)
	{
		this(pick,axe,sword,shovel,hoe,false);
	}
	public ToolSet(ItemStack pick,ItemStack axe,ItemStack sword,ItemStack shovel,ItemStack hoe,boolean recipe)
	{
		this(pick,axe,sword,shovel,hoe,recipe,false,false);
	}
	
	public ToolSet(ItemStack pick,ItemStack axe,ItemStack pickaxe,ItemStack shovel,ItemStack hoe,boolean recipe,boolean allMetaBlock,boolean allMetaStick)
	{
		this.pickaxe = pick;
		this.axe = axe;
		this.pickaxe = pickaxe;
		this.shovel = shovel;
		this.hoe = hoe;
		if(recipe)
		{
			this.allMetaBlock = allMetaBlock;
			this.allMetaStick = allMetaStick;
			MainJava.toolsets.add(this);
		}
	}

}
