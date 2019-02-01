package com.evilnotch.lib.main.loader;

import com.evilnotch.lib.main.MainJava;

public class LoaderGen {
	
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
			MainJava.proxy.jsonGen();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}

	private static void loadLang() 
	{
		MainJava.proxy.lang();
	}

}
