package com.evilnotch.lib.minecraft.capability;

/**
 * this allows for your capability objects to tick both on client and server without manually syncing stuff yourself. Prevents massive packet comunication
 * @author jredfox
 *
 * @param <T>
 */
public interface ICapabilityTick<T> extends ICapability{
	
	public void tick(T object, CapContainer c);

}
