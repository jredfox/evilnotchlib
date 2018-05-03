package com.EvilNotch.lib.util.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrEntry {
	
	public List<Object> list = null;
	public ArrayList<Character> ids = new ArrayList();//char identifiers for all the primitive values
	public char lbracket = '[';
	public char rbracket = ']';
	
	public ArrEntry(Object[] objs,ArrayList<Character> ids)
	{
		this(objs,ids,'[');
	}
	
	public ArrEntry(Object[] objs, ArrayList<Character> chars, char c) 
	{
		this.list = Arrays.asList(objs);
		this.ids = chars;
		if(c == '(')
		{
			this.lbracket = '(';
			this.rbracket = ')';
		}
	}

	@Override 
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ArrEntry))
			return false;
		
		ArrEntry arr = (ArrEntry)obj;
		return this.ids.equals(arr.ids) && this.list.equals(arr.list) && this.lbracket == arr.lbracket && this.rbracket == arr.rbracket;
	}
	@Override
	public String toString(){
		return this.list.toString() + " ids:" + this.ids.toString();
	}

}
