package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.items.IBasicItem;

import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemBlock;

public interface IBasicBlock extends IBasicItem{
	
	public ItemBlock getItemBlock();
	public boolean hasItemBlock();
	public List<String> getModelStates();
	public List<String> getBlockStatesNames();
	public IProperty getModelProperty();
}
