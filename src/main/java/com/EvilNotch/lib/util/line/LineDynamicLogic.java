package com.evilnotch.lib.util.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.util.LineUtil;

public class LineDynamicLogic extends LineComment implements ILine,ILineMeta{

	public HashMap<Integer,List<ILine>> lines = new HashMap<Integer,List<ILine>>();
	public String orLogic;
	public String andLogic;
	
	//line char config
	public char sep;
	public char quote;
	public char[] metaBrackets;
	public char[] lrBrackets;
	public String invalid;
	
	public LineDynamicLogic(String str)
	{
		this(str,LineUtil.orLogic,LineUtil.andLogic,LineUtil.sep,LineUtil.quote,LineUtil.metaBrackets,LineUtil.arrBrackets,LineUtil.lineInvalid);
	}
	
	public LineDynamicLogic(String str,String orLogic,String andLogic,char sep,char q,char[] mBrackets,char[] lrBrackets,String invalid)
	{
		this.orLogic = orLogic;
		this.andLogic = andLogic;
		
		this.sep = sep;
		this.quote = q;
		this.metaBrackets = mBrackets;
		this.lrBrackets = lrBrackets;
		this.invalid = invalid;
		
		this.parse(str);
	}
	
	public void parse(String str) 
	{
		String[] ores = LineUtil.selectString(str, this.orLogic, this.quote, this.metaBrackets[0] + "{",  this.metaBrackets[1] + "}");
		for(int oreIndex=0;oreIndex<ores.length;oreIndex++)
		{
			String section = ores[oreIndex];
			String[] parts = LineUtil.selectString(section, this.andLogic,this.quote, this.metaBrackets[0] + "{",  this.metaBrackets[1] + "}");
			
			List<ILine> list = new ArrayList<ILine>();
			for(String line : parts)
			{
				ILine l = LineUtil.getLineFromString(line,this.sep,this.quote,this.metaBrackets,lrBrackets,this.invalid);
				list.add(l);
			}
			this.lines.put(oreIndex,list);
		}
	}

	@Override
	public String getId() 
	{
		if(this.lines.size() == 0)
			return "";
		return this.getLinesAtPos(0).get(0).getId();
	}
	
	public List<ILine> getLinesAtPos(int pos)
	{
		return this.lines.get(pos);
	}

	@Override
	public String getComparible() 
	{
		return this.toString(true);
	}
	
	@Override
	public String toString()
	{
		return this.toString(false);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ILine) || lines.isEmpty())
			return false;
		ILine logic = (ILine)obj;
		return this.getLinesAtPos(0).get(0).equals(logic);
	}
	
	@Override
	public int hashCode()
	{
		if(this.lines.size() == 0)
			return -1;
		return this.getLinesAtPos(0).get(0).hashCode();
	}

	public String toString(boolean comparible) 
	{
		StringBuilder b = new StringBuilder();
		for(int section : this.lines.keySet())
		{
			List<ILine> lines = this.lines.get(section);
			for(int i=0;i<lines.size();i++)
			{
				ILine l = lines.get(i);
				boolean inRange = i+1 < lines.size();
				String comma = inRange ? " " + this.andLogic + " " : "";
				if(comparible)
					b.append(l.getComparible() + comma);
				else
					b.append(l.toString() + comma);
			}
			if(section+1 < this.lines.size())
				b.append(" " + this.orLogic + " ");
		}
		return b.toString();
	}
	/**
	 * return this.toString(true) meaning the meta will be compared without the head values
	 */
	@Override
	public String[] getMetaData()
	{
		List<String> list = new ArrayList<String>();
		for(List<ILine> lines : this.lines.values())
		{
			for(ILine line : lines)
			{
				StringBuilder b = new StringBuilder();
				b.append(line.getId());
				if(line instanceof ILineMeta)
				{
					b.append(" ");
					for(String s : ((ILineMeta)line).getMetaData())
						b.append(s);
				}
				list.add(b.toString());
			}
		}
		String[] parts = JavaUtil.toStaticStringArray(list);
		return parts;
	}

	@Override
	public boolean equalsMeta(ILine o) 
	{
		if(!(o instanceof ILineMeta))
			return LineUtil.isNullMeta(this);
		
		String[] meta = this.getMetaData();
		String[] other = ((ILineMeta)o).getMetaData();
		if(meta.length != other.length)
			return false;
		for(int i=0;i<meta.length;i++)
		{
			if(!meta[i].equals(other[i]))
				return false;
		}
		return true;
	}
	
	public ILine getLine(int sector,int index){
		return this.lines.get(sector).get(index);
	}

}
