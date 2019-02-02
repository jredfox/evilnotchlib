package com.evilnotch.lib.api.mcp;

import java.util.ArrayList;

import com.evilnotch.lib.main.Config;

public class MCPEntry {
	
	public MCPSidedString mcp = null;
	public ArrayList<String> classes = new ArrayList();
	public ArrayList<Class> actualClasses = new ArrayList();
	
	public MCPEntry(String srg, String name,ArrayList<String> clazz)
	{
		try
		{
			this.mcp = MCPSidedString.getMCPSidedStringSRG(srg, name);
			this.classes = clazz;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
					t.printStackTrace();
			}
		}
		this.actualClasses = clazzes;
		return clazzes;
	}

}
