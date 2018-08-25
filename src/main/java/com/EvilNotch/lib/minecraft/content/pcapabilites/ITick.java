package com.EvilNotch.lib.minecraft.content.pcapabilites;

import net.minecraft.entity.player.EntityPlayer;

public interface ITick {
	
	public void tick(EntityPlayer player,PCapabilityContainer container);
}
