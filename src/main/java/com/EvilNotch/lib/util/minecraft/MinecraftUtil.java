package com.EvilNotch.lib.util.minecraft;

import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;

public class MinecraftUtil {
	
	 public static void addGameRule(GameRules g,String pos, boolean init, ValueType type) {
	    	if(!g.hasRule(pos))
	    		g.addGameRule(pos, "" + init, type);
	    	else
	    		g.addGameRule(pos, "" + g.getBoolean(pos), type);
	}

}
