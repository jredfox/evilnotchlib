package com.evilnotch.lib.util.csv;

import java.util.ArrayList;

import com.evilnotch.lib.util.JavaUtil;

public class CSV {
	public ArrayList<String> list = new ArrayList<String>();
	public boolean tst;
	public CSV(String s)
	{
		String[] parts = JavaUtil.toWhiteSpaced(s).split(",");
		for(String ss : parts)
			list.add(ss);
	}
	public String toString(){return this.list.toString();}

}
