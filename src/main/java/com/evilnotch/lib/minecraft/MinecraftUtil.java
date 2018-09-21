package com.evilnotch.lib.minecraft;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.evilnotch.lib.Api.FieldAcess;
import com.evilnotch.lib.minecraft.content.pcapabilites.CapabilityReg;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class MinecraftUtil {
	
   public static void addGameRule(GameRules g,String ruleName, Object init, ValueType type) 
   {
	   if(init instanceof Boolean)
		   addGameRuleBoolean(g,ruleName,init,type);
	   else if(init instanceof Integer)
		   addGameInteger(g,ruleName,init,type);
   }
	 
   public static void addGameInteger(GameRules g, String ruleName, Object init, ValueType type) 
   {
	   if(!g.hasRule(ruleName))
		   g.addGameRule(ruleName, "" + init, type);
	   else
		   g.addGameRule(ruleName, "" + g.getInt(ruleName), type);
   }

   public static void addGameRuleBoolean(GameRules g, String ruleName, Object init, ValueType type) 
   {
	   if(!g.hasRule(ruleName))
		   g.addGameRule(ruleName, "" + init, type);
	   else
		   g.addGameRule(ruleName, "" + g.getBoolean(ruleName), type);
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
   /**
    * tells whether or not a mod is compiled goes through the entire process so use a cache or something don't check this alot
    */
   public static boolean isModCompiled(String modid)
   {
	   File source = Loader.instance().getIndexedModList().get(modid).getSource();
	   boolean isJar = source == null ? false : source.isFile() && isModExtension(source.getName());
	   return isJar;
   }
   /**
    * supports 1.7.10 .zip and .jar mods
    */
   public static boolean isModExtension(String name) 
   {
	   return name.endsWith(".jar") || name.endsWith(".zip");
   }

}
