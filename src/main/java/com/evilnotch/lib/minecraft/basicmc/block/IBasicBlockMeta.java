package com.evilnotch.lib.minecraft.basicmc.block;

import net.minecraft.block.properties.IProperty;

public interface IBasicBlockMeta {
	
	/**
	 * this is the primary properties that are used  by jsons
	 */
	public IProperty getProperty();
	
	/**
	 * this is so the itemstack knows what unloacalized name to use
	 */
	public String getPropertyName(int meta);

}
