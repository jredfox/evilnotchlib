package com.evilnotch.lib.minecraft.tick;

public interface ITick {
	
	public void tick();
	
	/**
	 * called when server or client closes/leaves
	 */
	public void garbageCollect();

}
