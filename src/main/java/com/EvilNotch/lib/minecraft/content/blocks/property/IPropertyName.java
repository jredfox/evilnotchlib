package com.EvilNotch.lib.minecraft.content.blocks.property;

import java.util.Set;

public interface IPropertyName {
	
	public Set<Integer> getMetaDataValues();
	public Enum getValue(int meta);
}
