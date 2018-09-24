package com.evilnotch.lib.minecraft.content.item.armor;

import net.minecraft.potion.PotionEffect;

public interface IPotionArmor extends IBasicArmor{
	
	public PotionEffect[] getPotionEffects();
	public boolean containsPotionEffect(PotionEffect p);
	
	default public boolean hasPotionEffects(){
		return getPotionEffects() != null;
	}

}
