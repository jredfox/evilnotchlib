package com.EvilNotch.lib.Api;

import java.util.ArrayList;

import com.EvilNotch.lib.main.Config;

public class MCPEntry {
	public String srg = null;
	public String name = null;
	public ArrayList<String> classes = new ArrayList();
	public ArrayList<Class> actualClasses = new ArrayList();
	
	public MCPEntry(String strsrg, String strname,ArrayList<String> clazz)
	{
		try{
			this.srg = strsrg;
			this.name = strname;
			this.classes = clazz;
		}catch(Exception e){e.printStackTrace();}
	}
	@Override
	public String toString()
	{
		String s = "";
		for(String c : classes)
			s += c + ",";
		return this.srg + "," + this.name + "," + s;
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
		return this.srg.equals(e.srg) && this.name.equals(e.name) && this.classes.equals(e.classes);
	}
	public ArrayList<Class> getClasses() {
		
		//optimization
		if(this.actualClasses.size() > 0)
			return this.actualClasses;
		
		ArrayList<Class> clazzes = new ArrayList();
		for(String str : this.classes)
		{
			try{
				Class c = Class.forName(str.replaceAll("/", "."));
				clazzes.add(c);
			}catch(Throwable t){
				if(Config.debug)
					t.printStackTrace();
			}
		}
		this.actualClasses = clazzes;
		return clazzes;
	}

}
