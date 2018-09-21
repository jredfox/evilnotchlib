package com.evilnotch.lib.minecraft.content.capabilites.primitive;

import com.evilnotch.lib.minecraft.content.capabilites.ICapability;

public abstract class CapBase<T> implements ICapability<T>{
	
	public String key = null;
	public CapBase(String key)
	{
		this.key = key;
	}

}
