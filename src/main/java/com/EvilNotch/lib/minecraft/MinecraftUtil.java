package com.EvilNotch.lib.minecraft;

import java.lang.reflect.Method;

import com.EvilNotch.lib.Api.FieldAcess;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;

public class MinecraftUtil {
	
   public static void addGameRule(GameRules g,String pos, boolean init, ValueType type) 
   {
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
   
   /**
    * uses reflection since the world method is protected
    */
   public static boolean isChunkLoaded(World w,int x,int z,boolean allowEmpty)
   {
	   try
	   {
		   Method m = FieldAcess.chunkLoaded;
		   m.setAccessible(true);
		   return (Boolean) m.invoke(w, x,z,allowEmpty);
	   }
	   catch(Throwable t)
	   {
		   t.printStackTrace();
	   }
	   return false;
   }

}
