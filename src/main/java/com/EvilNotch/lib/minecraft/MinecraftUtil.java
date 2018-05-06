package com.EvilNotch.lib.minecraft;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;

public class MinecraftUtil {
	
	 public static void addGameRule(GameRules g,String pos, boolean init, ValueType type) {
	    	if(!g.hasRule(pos))
	    		g.addGameRule(pos, "" + init, type);
	    	else
	    		g.addGameRule(pos, "" + g.getBoolean(pos), type);
	}
	 
	 public static SoundEvent getSoundEvent(ResourceLocation soundEventIN) {
		 return SoundEvent.REGISTRY.getObject(soundEventIN);
	}
	 public static ResourceLocation getSoundLoc(SoundEvent sound){
		 return SoundEvent.REGISTRY.getNameForObject(sound);
	 }

}
