package com.EvilNotch.lib.minecraft.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DynamicTranslationEvent extends Event{
	
	public final ItemStack stack;
	public String translation;
	
	/**
	 * this is your last chance to change the item stack's display name do it now
	 */
	public DynamicTranslationEvent(ItemStack stack,String currentTrans)
	{
		this.stack = stack;
		this.translation = currentTrans;
	}

}
