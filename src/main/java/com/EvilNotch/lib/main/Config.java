package com.EvilNotch.lib.main;

import java.io.File;
import java.util.ArrayList;

import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class Config {

	public static boolean debug = false;
	public static File cfg = null;
	public static ArrayList<ResourceLocation> cmdBlacklist = new ArrayList();
	
	public static void loadConfig(File d){
		if(cfg == null)
			cfg = new File(d,MainJava.MODID + "/" + MainJava.MODID + ".cfg");
		Configuration config = new Configuration(cfg);
		config.load();
		debug = config.get("general", "Debug", false).getBoolean();
		String[] vars = config.getStringList("blacklistCMDNames", "translations", new String[]{"\"modid:mobname\""}, "Blacklist for command sender names so it always uses general when translating input with quotes \"modid:mobname\" ");
		for(String s : vars)
		{
			if(!LineBase.toWhiteSpaced(s).equals(""))
				cmdBlacklist.add((new LineBase(s)).getResourceLocation() );
		}
		config.save();
	}

}
