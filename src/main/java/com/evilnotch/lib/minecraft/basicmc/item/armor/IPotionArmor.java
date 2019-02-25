package com.evilnotch.lib.minecraft.basicmc.item.armor;

import net.minecraft.item.ItemArmor;
import net.minecraft.potion.PotionEffect;

public interface IPotionArmor<T extends ItemArmor> extends IBasicArmor<T>{
	
	public PotionEffect[] getPotionEffects();
	public boolean containsPotionEffect(PotionEffect p);
	
	default public boolean hasPotionEffects(){
		return getPotionEffects() != null;
	}

}
