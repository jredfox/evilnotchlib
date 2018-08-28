package com.EvilNotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.client.gui.IMenu;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	public static boolean debug = false;
	public static File cfg = null;
	public static boolean isDev = false;
	public static boolean tpAllowCrossDim = false;
	public static boolean replaceTP = true;
	public static int pcapSaveTime = 180;
	
	public static void loadConfig(File d)
	{
		if(cfg == null)
			cfg = new File(d,MainJava.MODID + "/" + MainJava.MODID + ".cfg");
		Configuration config = new Configuration(cfg);
		config.load();
		debug = config.get("general", "Debug", false).getBoolean();
		isDev = config.get("general", "isDev", false).getBoolean();
		tpAllowCrossDim = config.get("general","tpAllowCrossDim",true).getBoolean();
		replaceTP = config.get("general","tpReplace",true).getBoolean();
		pcapSaveTime = config.get("general","pcapSaveTime",180).getInt() * 20;
		config.save();
	}



}
