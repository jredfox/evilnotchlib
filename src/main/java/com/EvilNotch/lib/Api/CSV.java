package com.EvilNotch.lib.Api;

import java.util.ArrayList;

public class CSV {
	ArrayList<String> list = new ArrayList<String>();
	public boolean tst;
	public CSV(String s)
	{
		String[] parts = toWhiteSpaced(s).split(",");
		for(String ss : parts)
			list.add(ss);
	}
	public String toString(){return this.list.toString();}
	
	/**
	* Ejects a string that is whitespaced
	* @param s
	* @return
	*/
	public static String toWhiteSpaced(String s)
	{
		return s.replaceAll("\\s+", "");
	}

}
