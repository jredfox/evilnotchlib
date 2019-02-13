package com.evilnotch.lib.minecraft.tick;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public interface ITick {
	
	public void tick();
	
	/**
	 * called when server or client closes/leaves
	 */
	public void garbageCollect();
	
	/**
	 * the phase in which the tickevent will tick 
	 */
	public TickEvent.Phase getPhase();

}
