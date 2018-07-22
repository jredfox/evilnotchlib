package com.EvilNotch.lib.minecraft.content.blocks.property;

import net.minecraft.block.properties.PropertyInteger;

public class PropertyInt extends PropertyInteger{
	
	public final int min;
	public final int max;

	protected PropertyInt(String name, int min, int max) {
		super(name, min, max);
		this.min = min;
		this.max = max;
	}

}
