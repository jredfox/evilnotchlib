package com.evilnotch.lib.minecraft.basicmc.block.property;

import java.util.Set;

import net.minecraft.util.IStringSerializable;

public interface IPropertyName extends IStringSerializable{
	
	public Enum getValue(int meta);
}
