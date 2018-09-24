package com.evilnotch.lib.minecraft.content.block;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.evilnotch.lib.minecraft.content.client.block.ModelPart;
import com.evilnotch.lib.minecraft.content.item.IBasicItem;

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
	 * null is allowed just make sure you don't do anything with it yourself
	 */
	public BlockProperties getBlockProperties();
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
