package com.evilnotch.lib.minecraft.basicmc.auto.json;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public interface IBasicBlockMetaJSON<T extends Block> extends IBasicBlockJSON<T>{
	
	public IProperty getProperty();

}
