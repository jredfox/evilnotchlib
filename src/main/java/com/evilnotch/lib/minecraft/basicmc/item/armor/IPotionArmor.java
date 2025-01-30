package com.evilnotch.lib.minecraft.basicmc.item.armor;

import net.minecraft.potion.PotionEffect;

public interface IPotionArmor{
	
	public PotionEffect[] getPotionEffects();
	public boolean containsPotionEffect(PotionEffect p);
	
	default public boolean hasPotionEffects(){
		return getPotionEffects() != null;
	}

}
