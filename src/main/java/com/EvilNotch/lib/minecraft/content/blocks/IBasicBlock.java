package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.EvilNotch.lib.minecraft.content.client.block.ModelPart;
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
	/**
	 * your primary block state for models on the statemapper and also is server side to
	 */
	public IProperty getStateProperty();
	/**
	 * blockstate to metadata array of all possible values
	 */
	public Set<Integer> getValuesOfProperty(IProperty p);
	/**
	 * a client only method to get resource locations for registration never return null
	 */
	@SideOnly(Side.CLIENT)
	public HashMap<Integer, ModelResourceLocation> getModelMap();
	/**
	 * a client only method to get the right textures if your block is multi sided never return null
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public ModelPart getModelPart();
}
