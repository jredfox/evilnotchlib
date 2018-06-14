package com.EvilNotch.lib.minecraft.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class CapeFixEvent extends PlayerEvent{
	
	public String url = "";
	public boolean overrideCape = false;

	public CapeFixEvent(EntityPlayer player) {
		super(player);
	}

}
