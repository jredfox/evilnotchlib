package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public class BasicBlockJSONMeta extends BasicBlockJSON implements IBasicBlockMetaJSON {

	public IProperty property;
	public String[] names = null;

	public BasicBlockJSONMeta(Block b, ModelPart part, IProperty p) 
	{
		this(b, part, p, null);
	}
	
	public BasicBlockJSONMeta(Block b, ModelPart part, IProperty p, String[] list)
	{
		super(b, part);
		this.property = p;
		this.names = list;
	}

	@Override
	public IProperty getProperty() 
	{
		return this.property;
	}

}
