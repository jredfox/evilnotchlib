package com.evilnotch.lib.util.line.config;

import java.io.File;
import java.util.List;

import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.util.LineUtil;

public class ConfigLineArray extends ConfigLine{
	
	/**
	 * create config base for only in memory manipulation
	 */
	public ConfigLineArray()
	{
		
	}

	public ConfigLineArray(File f) 
	{
		super(f);
	}
	public ConfigLineArray(String inputStream,File output)
	{
		super(inputStream,output);
	}
	public ConfigLineArray(File f, List<String> comments) {
		this(f,comments,"");
	}
	
	public ConfigLineArray(File f,List<String> comments,String header)
	{
		this(f,header,LineUtil.commentDefault,comments);
	}
	public ConfigLineArray(File f,String header,char commentStart,List<String> comments)
	{
		super(f,header,commentStart,comments);
	}
	public ConfigLineArray(File f,String header,boolean allowComments,char commentStart,List<String> comments,char[] headerWrappers,char sep,char q,char[] metaBrackets,char[] arrBrackets,String orLogic,String andLogic)
	{
		super(f,header,allowComments,commentStart,comments,headerWrappers,sep,q,metaBrackets,arrBrackets,orLogic,andLogic);
	}
	
	@Override
	public ILine getLineFromString(String str) 
	{
		return new LineArray(str);
	}

}
