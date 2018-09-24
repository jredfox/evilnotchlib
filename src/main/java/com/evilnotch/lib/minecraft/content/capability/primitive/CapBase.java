package com.evilnotch.lib.minecraft.content.capability.primitive;

import com.evilnotch.lib.minecraft.content.capability.ICapability;

public abstract class CapBase<T> implements ICapability<T>{
	
	public String key = null;
	public CapBase(String key)
	{
		this.key = key;
	}

}
