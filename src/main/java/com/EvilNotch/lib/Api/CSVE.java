package com.EvilNotch.lib.Api;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CSVE {
	
	ArrayList<CSV> vars = new ArrayList<CSV>();
	ArrayList<CSV> list = new ArrayList<CSV>();
	
	public CSVE(){}//default object with nothing in it
	
	public CSVE(File f)
	{
		try {
			parseFile(f,this.vars,this.list);
		} catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * returns the csv line
	 */
	public CSV getCSV(String var)
	{
		for(CSV csv : vars)
			if(csv.list.get(0).equals(var))
				return csv;
		for(CSV csv : list)
			if(csv.list.get(0).equals(var))
				return csv;
		return null;
	}
	/**
	 * Returns the value of the variable from the csv 
	 */
	public String getValue(CSV csv,int placement)
	{
		String var = csv.list.get(placement);//placement on the line
		if(!var.contains("\""))
			return var;
		for(CSV c : this.vars)
		{
			String[] parts = c.list.get(0).split(":");
			String name = parts[0];
			int index = 1;
			if(parts.length > 1)
			{
				parts[1] = CSV.toWhiteSpaced(parts[1]);
				index = Integer.parseInt(parts[1]);
			}
			if(name.equals(var))
				return c.list.get(index);
		}
		return null;
	}
	/**
	 * returns the variable based on the value
	 */
	public String getVariable(String path,int index)
	{
		return getValue(getCSV(path),index);
	}
	
	public static void parseFile(File f,List<CSV> vars,List<CSV> list) throws Exception
	{
		List<String> init = Files.readAllLines(f.toPath());
		boolean header = false;
		boolean body = false;
		for(String s : init)
		{
			String str = CSV.toWhiteSpaced(s);
			if(str.equals("") || str.indexOf("#") == 0)
				continue;
			if(str.equals("<Header>"))
			{
				header = true;
				continue;
			}
			if(str.equals("</Header>") )
			{
				header = false;
				body = true;
				continue;
			}
			if(header)
				vars.add(new CSV(str));
			else
				list.add(new CSV(str));
		}
	}
	@Override
	public String toString(){return "Vars:" + this.vars + "\nList:" +  this.list;}

}
