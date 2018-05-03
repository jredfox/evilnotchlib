package com.EvilNotch.lib.minecraft;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class Util {
	
	 public static SoundEvent getSoundEvent(ResourceLocation soundEventIN) {
		 return SoundEvent.REGISTRY.getObject(soundEventIN);
	}
	 public static ResourceLocation getSoundLoc(SoundEvent sound){
		 return SoundEvent.REGISTRY.getNameForObject(sound);
	 }

}
