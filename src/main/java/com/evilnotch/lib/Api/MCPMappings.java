package com.evilnotch.lib.Api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraftforge.common.MinecraftForge;

public class MCPMappings {
	
	//MCPMAPPINGS API hashmaps cached here only on pre-init
    public static File dirmappings = null;
	public static List<MCPEntry> fields = new ArrayList();
	public static List<MCPEntry> methods = new ArrayList();
	public static boolean isCached = false;
	
	/**
	 * null proof as long as name isn't null
	 */
	public static String getField(Class clazz, String name)
	{
		if(MainJava.isDeObfuscated)
			return name;
		else
		{
			String ob = getFieldOb(clazz,name);
			if(ob == null)
				return name;
			return ob;
		}
	}
	/**
	 * null proof as long as name isnt' null
	 */
	public static String getMethod(Class clazz, String name)
	{
		if(MainJava.isDeObfuscated)
			return name;
		else
		{
			String ob = getMethodOb(clazz,name);
			if(ob == null)
				return name;
			return ob;
		}
	}
	
	public static String getFieldOb(Class clazz, String strname)
	{
		MCPEntry e = getEntry(clazz,strname, fields);
		if(e != null)
			return e.srg;
		return null;
	}
	public static String getMethodOb(Class clazz, String strname)
	{
		MCPEntry e = getEntry(clazz,strname, methods);
		if(e != null)
			return e.srg;
		return null;
	}
	public static MCPEntry getEntryFromOb(String ob,ArrayList<MCPEntry> list)
	{
		for(MCPEntry e : list)
		{
			if(e.srg.equals(ob))
				return e;
		}
		return null;
	}
	public static MCPEntry getEntry(Class clazz,String field,List<MCPEntry> list)
	{
		for(MCPEntry e : list)
		{
			String name = e.name;
			if(name.equals(field))
			{
				ArrayList<Class> clazzes = e.getClasses();
				for(Class c : clazzes)
				{
					if(c.isAssignableFrom(clazz) )
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
//		JavaUtil.moveFileFromJar(MainJava.class, strparams, dirParams, false);  #unsupported right now
		CSVE field = new CSVE(dirFields);
		CSVE method = new CSVE(dirMethods);
//		CSVE par = new CSVE(dirParams);
		
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

}
