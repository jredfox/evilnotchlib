package com.evilnotch.lib.main.loader;

import java.io.File;
import java.io.IOException;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;

public class LoaderGen {
	
	public static File root = null;
	
	public static void load()
	{
		if(LoaderMain.isClient)
		{
			LangRegistry.registerLang();
			try {
				JsonGen.genJSONS();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
