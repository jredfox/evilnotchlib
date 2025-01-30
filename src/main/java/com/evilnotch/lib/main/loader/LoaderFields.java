package com.evilnotch.lib.main.loader;

import java.lang.reflect.Method;

import com.evilnotch.lib.api.mcp.MCPSidedString;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;

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
			chunkLoaded = World.class.getDeclaredMethod(new MCPSidedString("isChunkLoaded", "func_175680_a").toString(), int.class, int.class, boolean.class);
			setFlag = Entity.class.getDeclaredMethod(new MCPSidedString("setFlag", "func_70052_a").toString(), int.class, boolean.class);
			method_setSlimeSize = EntitySlime.class.getDeclaredMethod(new MCPSidedString("setSlimeSize", "func_70799_a").toString(), int.class, boolean.class);
			
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
