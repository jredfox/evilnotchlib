package com.evilnotch.lib.minecraft.content.tick;

public interface ITick {
	
	public void tick();
	
	/**
	 * called when server or client closes/leaves
	 */
	public void garbageCollect();

}
