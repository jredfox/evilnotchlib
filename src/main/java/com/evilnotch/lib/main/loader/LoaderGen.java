package com.evilnotch.lib.main.loader;

import java.io.File;
import java.io.IOException;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;

public class LoaderGen {
	
	public static File root = null;
	public static File root_sync = null;
	
	public static void load()
	{
		if(LoaderMain.isClient)
		{
			LangRegistry.generateLang();
			try
			{
				JsonGen.genJSONS();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void checkRootFile() 
	{
		if(root != null && root_sync != null)
			return;
		File mdk = Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile();
		root = new File(mdk,"src/main/resources/assets");
		root_sync = new File(mdk,"bin/assets");
		if(!root.exists())
			root.mkdirs();
		if(!root_sync.exists())
			root.mkdirs();
	}
	
	/**
	 * input the original root dir of any file src/main/resources/assets/file.* and get bin/assets/file.*
	 */
	public static File getSyncFile(File root)
	{
		return new File(LoaderGen.root_sync,root.getAbsolutePath().substring(LoaderGen.root.getAbsolutePath().length(), root.getAbsolutePath().length()) );
	}

}
