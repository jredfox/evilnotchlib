package com.evilnotch.lib.util.line;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.util.LineUtil;

public class LineArray extends LineMeta implements ILineHeadArray{

	public List<Object> heads = new ArrayList<Object>();
	public char lbracket = ' ';
	public char rbracket = ' ';
	
	public LineArray(String str)
	{
		this(str,LineUtil.sep,LineUtil.quote,LineUtil.metaBrackets,LineUtil.arrBrackets,LineUtil.lineInvalid);
	}
	
	public LineArray(String str, char sep,char q,char[] metaBrackets,char[] brackets,String invalid) 
	{
		super(str, sep,q,metaBrackets,invalid);
		if(str.contains("="))
		{
			String[] parts = LineUtil.selectString(str, "=", q, "{" + metaBrackets[0], "}" + metaBrackets[1]);
			if(parts.length > 1)
			{
				str = parts[1].trim();
			
				if(str.startsWith("" + brackets[0]))
				{
					this.lbracket = brackets[0];
					this.rbracket = brackets[1];
					str = str.substring(1, str.length()-1);
				}
			
				String[] toParse = LineUtil.selectString(str, ',',q,this.lbracket,this.rbracket);
				parseHead(toParse,this.heads);
			}
		}
	}
	@Override
	public List<Object> getHeads() 
	{
		return heads;
	}
	/**
	 * return actual objects when applicable
	 */
	@Override
	public Object getHead(int index) 
	{
		return this.heads.get(index);
	}
	/**
	 * set the lines value
	 */
	@Override
	public void setHead(Object obj,int index) 
	{
		if(this.heads.isEmpty())
			this.addHead(obj);
		else
			this.heads.set(index, obj);
	}
	@Override
	public void addHead(Object obj)
	{
		this.heads.add(obj);
	}
	@Override
	public int size()
	{
		return this.heads.size();
	}
	@Override
	public String toString(boolean comparible)
	{
		//don't compare head values when organizing the lines alphabetically
		if(comparible)
			return super.toString(comparible);
		String str = super.toString(comparible);
		if(!this.heads.isEmpty())
		{
			str += " = ";
			str += this.getListString(this.heads);
		}
		return str;
	}
	/**
	 * calls itself recursively until all lists are converted into strings
	 */
	@SuppressWarnings("unchecked")
	public String getListString(List<Object> li) 
	{
		String str = "";
        if(this.lbracket != ' ')
            str += "" + this.lbracket;
		for(Object obj : li)
		{
			if(obj instanceof List)
                str += this.getListString((List<Object>)obj) + ",";
			else if(obj instanceof Number && !(obj instanceof Integer) && !(obj instanceof Double))
                str += obj.toString() + JavaUtil.getNumId((Number)obj) + ",";
            else if (obj instanceof String)
                str += "\"" + obj + "\",";
            else
                str += obj.toString() + ",";
		}
		str = str.substring(0, str.length()-1);
        if(this.rbracket != ' ')
            str += "" + this.rbracket;
		return str;
	}

	/**
	 * Recursively populate the array from string to actual objects
	 */
	public void parseHead(String[] str,List<Object> list) 
	{
		for(String s : str)
		{
			if(s.startsWith("" + this.lbracket) && this.lbracket != ' ')
			{
				List<Object> newList = new ArrayList<Object>();
				list.add(newList);
				//step 1 remove braces
				s = s.trim();
				s = s.substring(1, s.length()-1);
				//step 2 select string
				String[] select = LineUtil.selectString(s, ',', this.quote,this.lbracket,this.rbracket);
				this.parseHead(select, newList);
			}
			else
				list.add(parseWeight(s));
		}
	}
	/**
	 * get the primitive object from the string
	 */
	public Object parseWeight(String weight) 
	{
		return LineUtil.parseWeight(weight, this.quote);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getHeadList(int index) 
	{
		return (List<Object>) this.heads.get(index);
	}
	
}
