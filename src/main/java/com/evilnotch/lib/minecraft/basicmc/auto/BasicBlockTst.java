package com.evilnotch.lib.minecraft.basicmc.auto;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.evilnotch.lib.minecraft.basicmc.block.BlockProperties;
import com.evilnotch.lib.minecraft.basicmc.block.IBasicBlock;
import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BasicBlockTst implements IBasicBlock{
	
	public Block block = null;
	public BasicBlockTst(Block b)
	{
		this.block = b;
	}

	@Override
	public boolean register() {
		return true;
	}

	@Override
	public boolean registerModel() 
	{
		return true;
	}

	@Override
	public boolean useLangRegistry() {
		return true;
	}

	@Override
	public boolean useConfigPropterties() {
		return true;
	}

	@Override
	public ResourceLocation getRegistryName() 
	{
		return this.block.getRegistryName();
	}

	@Override
	public ItemBlock getItemBlock() 
	{
		return null;
	}

	@Override
	public boolean hasItemBlock() 
	{
		return this.getItemBlock() != null;
	}

	@Override
	public List<String> getModelStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getBlockStatesNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockProperties getBlockProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProperty getStateProperty() 
	{
		return null;
	}

	@Override
	public Set<Integer> getValuesOfProperty(IProperty p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Integer, ModelResourceLocation> getModelMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelPart getModelPart() {
		// TODO Auto-generated method stub
		return null;
	}

}
