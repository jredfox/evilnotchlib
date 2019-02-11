package com.evilnotch.lib.minecraft.basicmc.block.property;

import java.util.Set;

public interface IPropertyName {
	
	public Set<Integer> getMetaDataValues();
	public Enum getValue(int meta);
}
