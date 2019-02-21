package com.evilnotch.lib.api.mcp;

import java.util.ArrayList;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.util.JavaUtil;

public class MCPEntry {
	
	public MCPSidedString mcp = null;
	public MCPSidedString desc = null;
	public ArrayList<String> classes = new ArrayList<String>();
	public ArrayList<Class> actualClasses = new ArrayList<Class>();
	
	/**
	 * legacy contructor for if you have all the data at once
	 */
	public MCPEntry(String deob, String srg, ArrayList<String> clazz)
	{
		this.mcp = new MCPSidedString(deob,srg);
		this.classes = clazz;
	}
	
	/**
	 * field entry contructor
	 */
	public MCPEntry(String deob, String srg, String clazz)
	{
		this.mcp = new MCPSidedString(deob, srg);
		this.classes.add(clazz);
	}
	
	/**
	 * method mcpentry
	 */
	public MCPEntry(String deob, String srg, String descDeob, String descObf, String clazz)
	{
		this(deob,srg,clazz);
		this.desc = new MCPSidedString(descObf, descDeob);
	}
	
	public MCPEntry(Class clazz, MCPSidedString mcp) 
	{
		this.mcp = mcp;
		this.classes = JavaUtil.asArray(clazz);
	}
	
	public boolean isMethod()
	{
		return this.desc != null;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for(String c : classes)
			s += c + ",";
		return this.mcp.ob + "," + this.mcp.deob + "," + s;
	}
	
	/**
	 * hotfix
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof MCPEntry) )
			return false;
		MCPEntry e = (MCPEntry) obj;
		return this.mcp.ob.equals(e.mcp.ob) && this.mcp.deob.equals(e.mcp.deob) && this.classes.equals(e.classes);
	}
	
	public ArrayList<Class> getClasses() 
	{	
		//optimization
		if(this.actualClasses.size() > 0)
			return this.actualClasses;
		
		ArrayList<Class> clazzes = new ArrayList();
		for(String str : this.classes)
		{
			try
			{
				Class c = Class.forName(str.replaceAll("/", "."));
				clazzes.add(c);
			}
			catch(Throwable t)
			{
				if(Config.debug)
					System.out.println("Class Not Found:" + str);
			}
		}
		this.actualClasses = clazzes;
		return clazzes;
	}

}
