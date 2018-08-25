package com.EvilNotch.lib.minecraft.content.capabilites.registry;

public interface ICapRegistry<T> {
	
	/**
	 * allows you to determine wheather or not the object should have the capability registered
	 */
	public void register(CapContainer c,T objecty);
}
