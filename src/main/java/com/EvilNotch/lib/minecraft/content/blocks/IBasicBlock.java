package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.List;
import java.util.Set;

import com.EvilNotch.lib.minecraft.content.items.IBasicItem;

import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemBlock;

public interface IBasicBlock extends IBasicItem{
	
	public ItemBlock getItemBlock();
	public boolean hasItemBlock();
	public List<String> getModelStates();
	public List<String> getBlockStatesNames();
	public IProperty getStateProperty();
	public Set<Integer> getValuesOfProperty(IProperty p);
	
	default public boolean isMeta(){
		return getBlockStatesNames().size() > 1;
	}
}
