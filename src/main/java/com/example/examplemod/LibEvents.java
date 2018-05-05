package com.example.examplemod;

import java.io.File;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class LibEvents {
	
	public static File worlDir = null;
	public static File playerDataNames = null;
	public static File playerDataDir = null;
	
	/**
	 * Attempt to re-instantiate the entity caches for broken entities when the world is no longer fake
	 */
	 @SubscribeEvent
	 public void worldload(WorldEvent.Load e)
	 {
		 World w = e.world;
		 if(!w.isRemote)
		 {
			 //instantiate directories for core minecraft bug fixes
			 worlDir = w.getSaveHandler().getWorldDirectory();
			 playerDataNames = new File(worlDir,"playerdata/names");
			 playerDataNames.mkdirs();
			 playerDataDir = playerDataNames.getParentFile();
		 }
	 }


}
