package com.EvilNotch.lib.minecraft.content.capabilites.registry;

public interface ICapRegistry<T> {
	
	/**
	 * allows you to determine whether or not the object should have the capability registered
	 */
	public void register(CapContainer c,T object);
	/**
	 * if is entity return Entity.class to be as generic as possible
	 * unless you only want a specific entity having the capability. 
	 * if don't want anything extending it then in your register method you can simply check what class is what
	 * The list of classes currently planned to be hooked are Entity,Chunk,World,TileEntity,and itemstack
	 */
	public Class getObjectClass();
}
