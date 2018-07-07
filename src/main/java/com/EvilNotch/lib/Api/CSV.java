package com.EvilNotch.lib.Api;

import java.util.ArrayList;

import com.EvilNotch.lib.util.Line.LineBase;

public class CSV {
	ArrayList<String> list = new ArrayList<String>();
	public boolean tst;
	public CSV(String s)
	{
		String[] parts = LineBase.toWhiteSpaced(s).split(",");
		for(String ss : parts)
			list.add(ss);
	}
	public String toString(){return this.list.toString();}

}
