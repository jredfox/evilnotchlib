package com.EvilNotch.lib.minecraft.content.pcapabilites;

import net.minecraft.entity.player.EntityPlayer;

public interface ICapabilityProvider {
	
	public void register(EntityPlayer p,PCapabilityContainer c);

}
