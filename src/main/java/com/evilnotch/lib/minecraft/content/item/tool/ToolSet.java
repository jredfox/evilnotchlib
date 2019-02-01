package com.evilnotch.lib.minecraft.content.item.tool;

import com.evilnotch.lib.main.loader.LoaderItems;

import net.minecraft.init.Blocks;
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
	public static final ItemStack air = new ItemStack(Blocks.AIR);
	
	public ToolSet(Item pick,Item axe,Item sword,Item shovel,Item hoe)
	{
		this(pick,axe,sword,shovel,hoe,air,air);
	}
	public ToolSet(Item pick,Item axe,Item sword,Item shovel,Item hoe,ItemStack block,ItemStack stick)
	{
		this(pick,axe,sword,shovel,hoe,block,stick,false,false);
	}
	public ToolSet(Item pick,Item axe,Item sword,Item shovel,Item hoe,ItemStack block,ItemStack stick,boolean allMetaBlock,boolean allMetaStick)
	{
		this(new ItemStack(pick),new ItemStack(axe),new ItemStack(sword),new ItemStack(shovel),new ItemStack(hoe),block,stick,allMetaBlock,allMetaStick);
	}
	
	public ToolSet(ItemStack pick,ItemStack axe,ItemStack sword,ItemStack shovel,ItemStack hoe)
	{
		this(pick,axe,sword,shovel,hoe,air,air);
	}
	public ToolSet(ItemStack pick,ItemStack axe,ItemStack sword,ItemStack shovel,ItemStack hoe,ItemStack block,ItemStack stick)
	{
		this(pick,axe,sword,shovel,hoe,block,stick,false,false);
	}
	
	public ToolSet(ItemStack pick,ItemStack axe,ItemStack sword,ItemStack shovel,ItemStack hoe,ItemStack block,ItemStack stick,boolean allMetaBlock,boolean allMetaStick)
	{
		this.pickaxe = pick;
		this.axe = axe;
		this.sword = sword;
		this.shovel = shovel;
		this.hoe = hoe;
		if(!block.isEmpty() && !stick.isEmpty())
		{
			this.block = block;
			this.stick = stick;
			this.allMetaBlock = allMetaBlock;
			this.allMetaStick = allMetaStick;
			LoaderItems.toolsets.add(this);
		}
	}

}
