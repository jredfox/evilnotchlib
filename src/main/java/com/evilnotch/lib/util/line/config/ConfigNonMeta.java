package com.evilnotch.lib.util.line.config;

import java.io.File;
import java.util.List;

import com.evilnotch.lib.util.line.util.LineUtil;

public class ConfigNonMeta extends ConfigLine{
	
	/**
	 * create config base for only in memory manipulation
	 */
	public ConfigNonMeta()
	{
		
	}

	public ConfigNonMeta(File f) 
	{
		super(f);
	}
	public ConfigNonMeta(String inputStream,File output)
	{
		super(inputStream,output);
	}
	public ConfigNonMeta(File f, List<String> comments) {
		this(f,comments,"");
	}
	
	public ConfigNonMeta(File f,List<String> comments,String header)
	{
		this(f,header,LineUtil.commentDefault,comments);
	}
	public ConfigNonMeta(File f,String header,char commentStart,List<String> comments)
	{
		super(f,header,commentStart,comments);
	}
	public ConfigNonMeta(File f,String header,boolean allowComments,char commentStart,List<String> comments,char[] headerWrappers,char sep,char q,char[] metaBrackets,char[] arrBrackets,String orLogic,String andLogic)
	{
		super(f,header,allowComments,commentStart,comments,headerWrappers,sep,q,metaBrackets,arrBrackets,orLogic,andLogic);
	}
	
	@Override
	public boolean checkMetaByDefault()
	{
		return false;
	}

}
