package com.evilnotch.lib.minecraft.basicmc.auto;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public interface IBasicBlockMeta<T extends Block> extends IBasicBlock<T>{
	
	public IProperty getProperty();

}
