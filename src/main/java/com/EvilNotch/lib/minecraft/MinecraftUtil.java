package com.EvilNotch.lib.minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.minecraft.content.SkinData;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityContainer;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityReg;
import com.EvilNotch.lib.minecraft.events.SkinFixEvent;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
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
