package com.evilnotch.lib.main.loader;

import java.lang.reflect.Method;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPMappings;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.storage.SaveHandler;

public class LoaderFields {

    public static Method chunkLoaded = null;
    public static Method setFlag = null;
    public static Method method_setSlimeSize = null;
	
	public static boolean cached = false;
	
	public static void cacheFields()
	{
		if(cached)
			return;
		try 
		{
			chunkLoaded = World.class.getDeclaredMethod(new MCPSidedString("isChunkLoaded", "func_175680_a").toString(), int.class,int.class,boolean.class);
			setFlag = Entity.class.getDeclaredMethod(new MCPSidedString("setFlag", "func_70052_a").toString(), int.class,boolean.class);
			method_setSlimeSize = EntitySlime.class.getDeclaredMethod(new MCPSidedString("setSlimeSize", "func_70799_a").toString(), int.class,boolean.class);
			
			chunkLoaded.setAccessible(true);
			setFlag.setAccessible(true);
			method_setSlimeSize.setAccessible(true);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		cached = true;
	}

}
