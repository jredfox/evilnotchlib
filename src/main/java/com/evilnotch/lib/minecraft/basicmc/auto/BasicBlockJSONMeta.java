package com.evilnotch.lib.minecraft.basicmc.auto;

import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public class BasicBlockJSONMeta extends BasicBlockJSON implements IBasicBlockMeta {

	public IProperty property;

	public BasicBlockJSONMeta(Block b, ModelPart part, IProperty p) 
	{
		super(b, part);
		this.property = p;
	}

	@Override
	public IProperty getProperty() 
	{
		return this.property;
	}

}
