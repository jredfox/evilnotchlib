package com.evilnotch.lib.api.mcp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.main.loader.LoadingStage;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.csve.CSV;
import com.evilnotch.lib.util.csve.CSVE;
import com.evilnotch.lib.util.line.Line;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigLine;

import net.minecraftforge.common.MinecraftForge;

/**
 * get srg name in deob from class and field without having to constantly look everything up yourselves.
 * This is for deob only however ObfHelper to get deob and ob class names based on what forge has. This is for fields and methods
 * @author jredfox
 */
public class MCPMappings {
	
	//MCPMAPPINGS API hashmaps cached here only on pre-init
    public static File dirmappings = null;
    /**
     * is null before or after pre init
     */
	public static List<MCPEntry> fields = new ArrayList();
	/**
	 * is null before or after pre init
	 */
	public static List<MCPEntry> methods = new ArrayList();
	public static boolean isCached = false;
	
	/**
	 * the cached data you grabbed
	 */
	public static Set<MCPEntry> cached = new HashSet<MCPEntry>();
	
	/**
	 * get the srg name without constant lookup of the file in deob only after you get srgname use MCPSidedString(deob,ob) instead
	 */
	public static String getSRGField(Class clazz, String name)
	{
		throwExceptions();
		
		MCPEntry entry = getEntry(clazz, name, fields);
		if(entry != null)
		{
			cached.add(new MCPEntry(clazz, entry.mcp) );
			return entry.mcp.ob;
		}
		return null;
	}
	
	/**
	 * get the srg name without constant lookup of the file in deob only after you get srgname use MCPSidedString(deob,ob) instead
	 */
	public static String getSRGMethod(Class clazz, String name, Class... params)
	{
		throwExceptions();
		
		MCPEntry entry = getEntry(clazz, name, methods);
		if(entry != null)
		{
			cached.add(entry);
			return entry.mcp.ob;
		}
		return null;
	}
	
	private static void throwExceptions() 
	{
		if(!LoaderMain.isDeObfuscated)
			throw new RuntimeException("This Method Is for deobfuscated use only");
		else if(!LoaderMain.isLoadingStage(LoadingStage.PREINIT))
			throw new RuntimeException("This is for pre init use only to get srg names! After you got the srg use MCPSidedString(deob,ob) instead of doing this");
	}

	public static MCPEntry getEntryFromOb(String ob, ArrayList<MCPEntry> list)
	{
		throwExceptions();
		
		for(MCPEntry e : list)
		{
			if(e.mcp.ob.equals(ob))
				return e;
		}
		return null;
	}
	
	private static MCPEntry getEntry(Class clazz, String field, List<MCPEntry> list)
	{
		for(MCPEntry e : list)
		{
			String name = e.mcp.deob;
			if(name.equals(field))
			{
				ArrayList<Class> clazzes = e.getClasses();
				for(Class c : clazzes)
				{
					if(JavaUtil.isClassExtending(c, clazz))
						return e;
				}
			}
		}
		return null;
	}
	
	public static void cacheMCPApplicable(File dir)
	{
		if(!isCached)
			cacheMCP(dir);
	}
	
	public static void cacheMCP(File dir)
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		isCached = true;
		dirmappings = new File(dir,MainJava.MODID + "/mcp/" + MinecraftForge.MC_VERSION);
		if(!dirmappings.exists())
			dirmappings.mkdirs();
		String strfield = "/assets/" + MainJava.MODID + "/mcp/" + MinecraftForge.MC_VERSION + "/fields_map.csv";
		String strmethod = "/assets/" + MainJava.MODID + "/mcp/" + MinecraftForge.MC_VERSION + "/methods_map.csv";
		String strparams = "/assets/" + MainJava.MODID + "/mcp/" + MinecraftForge.MC_VERSION + "/params.csv";
		File dirFields = new File(dirmappings,"fields_mappings.csv");
		File dirMethods = new File(dirmappings,"methods_mappings.csv");
		File dirParams = new File(dirmappings,"params.csv");
		
		JavaUtil.moveFileFromJar(MainJava.class, strfield,dirFields, false);
		JavaUtil.moveFileFromJar(MainJava.class, strmethod, dirMethods, false);
		CSVE field = new CSVE(dirFields);
		CSVE method = new CSVE(dirMethods);
		
		for(CSV csv : field.list)
		{
			ArrayList list = new ArrayList();
			int i = 0;
			for(String s : csv.list)
			{
				if(i != 0 && i != 1)
					list.add(s);
				i++;
			}
			fields.add(new MCPEntry(csv.list.get(0),csv.list.get(1),list) );
		}
		
		for(CSV csv : method.list)
		{
			ArrayList list = new ArrayList();
			int i = 0;
			for(String s : csv.list)
			{
				if(i != 0 && i != 1)
					list.add(s);
				i++;
			}
			methods.add(new MCPEntry(csv.list.get(0),csv.list.get(1),list) );
		}
		
	}
	
	/**
	 * after pre init these maps don't need to be stored in memory
	 */
	public static void clearMaps() 
	{
		//make the api usable output a user freindly file
		if(LoaderMain.isDeObfuscated && !cached.isEmpty())
		{
			File f = new File(System.getProperty("user.home") + "/Desktop/srgs.txt");
			ConfigLine cfg = new ConfigLine(f);
			for(MCPEntry entry : cached)
			{
				MCPSidedString str = entry.mcp;
				cfg.addLine(new Line(entry.classes.get(0) + ", " + "new MCPSidedString(\"" + str.deob + "\", \"" + str.ob + "\")"));
			}
			cfg.saveConfig();
		}
		MCPMappings.methods.clear();
		MCPMappings.fields.clear();
		MCPMappings.cached.clear();
	}

}
