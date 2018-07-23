package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.EvilNotch.lib.minecraft.content.items.IBasicItem;

import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBasicBlock extends IBasicItem{
	
	public ItemBlock getItemBlock();
	public boolean hasItemBlock();
	public List<String> getModelStates();
	public List<String> getBlockStatesNames();
	public IProperty getStateProperty();
	public Set<Integer> getValuesOfProperty(IProperty p);
	
	@SideOnly(Side.CLIENT)
	public HashMap<Integer, ModelResourceLocation> getModelMap();
}
