package com.evilnotch.lib.minecraft.basicmc.auto;

import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public interface IBasicBlock<T extends Block> extends IBasicItem<T>{
	
	public ModelPart getModelPart();
	public ItemBlock getItemBlock();
	
}
