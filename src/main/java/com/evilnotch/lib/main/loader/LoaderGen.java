package com.evilnotch.lib.main.loader;

import java.io.File;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.content.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.content.auto.lang.LangRegistry;

public class LoaderGen {
	
	public static File root = null;
	
	public static void load()
	{
		loadLang();
		loadJSON();
	}

	private static void loadJSON() 
	{
		/**
		 * if world capabilities are still not registered by init that is a mod's issue for the world and not mine
		 */
		try
		{
			JsonGen.jsonGen();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}

	private static void loadLang() 
	{
		LangRegistry.registerLang();
	}
	
	public static void checkRootFile() 
	{
		if(root != null)
			return;
		root = new File(Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile(),"src/main/resources/assets");
		if(!root.exists())
			root.mkdirs();
	}

}
