package com.evilnotch.lib.minecraft.capability.registry;

import com.evilnotch.lib.minecraft.capability.CapContainer;

public interface ICapabilityRegistry<T> {
	
	/**
	 * register all your capabilities to this specified object
	 */
	public void register(T object, CapContainer c);
	
	/**
	 * return the object of the most generic class that you are apply the capability to. 
	 * For example Entity.class for all entities or EntityCreeper.class for only creepers
	 */
	public Class getObjectClass();
}
