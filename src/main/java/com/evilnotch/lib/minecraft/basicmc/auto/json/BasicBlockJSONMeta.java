package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public class BasicBlockJSONMeta extends BasicBlockJSON implements IBasicBlockMetaJSON {

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
