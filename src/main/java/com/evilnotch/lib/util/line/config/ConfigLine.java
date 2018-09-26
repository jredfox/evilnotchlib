package com.evilnotch.lib.util.line.config;

import java.io.File;
import java.util.List;

import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.Line;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.LineDynamicLogic;
import com.evilnotch.lib.util.line.LineMeta;
import com.evilnotch.lib.util.line.util.LineUtil;

public class ConfigLine extends ConfigBase{
	
	/**
	 * seperator for all the lines that use them
	 */
	public char sep = LineUtil.sep;
	public char quote = LineUtil.quote;
	/**
	 * brackets for metadata of LineMeta
	 */
	public char[] metaBrackets = LineUtil.metaBrackets;
	/**
	 * brackets for LineArray defaults
	 */
	public char[] arrBrackets = LineUtil.arrBrackets;
	/**
	 * a list of invalid chars for parsing Line in the constructor
	 */
	public String invalid = LineUtil.lineInvalid;
	
	/**
	 * line dynamic logic config "||,&&" operators
	 */
	public String orLogic = LineUtil.orLogic;
	public String andLogic = LineUtil.andLogic;
	
	/**
	 * create config base for only in memory manipulation
	 */
	public ConfigLine()
	{
		
	}
	
	public ConfigLine(String inputStream,File output)
	{
		super(inputStream,output);
	}
	
	public ConfigLine(File f) 
	{
		super(f);
	}
	
	public ConfigLine(File f, List<String> comments) {
		this(f,comments,"");
	}
	
	public ConfigLine(File f,List<String> comments,String header)
	{
		this(f,header,LineUtil.commentDefault,comments);
	}
	
	public ConfigLine(File f,String header,char commentStart,List<String> comments)
	{
		this(f,header,true,commentStart,comments,"</>".toCharArray(),LineUtil.sep,LineUtil.quote,LineUtil.metaBrackets,LineUtil.arrBrackets,LineUtil.orLogic,LineUtil.andLogic);
	}
	public ConfigLine(File f,String header,boolean allowComments,char commentStart,List<String> comments,char[] headerWrappers,char sep,char q,char[] metaBrackets,char[] arrBrackets,String orLogic,String andLogic)
	{
		super(f,header,allowComments,commentStart,comments,headerWrappers);
		this.sep = sep;
		this.quote = q;
		this.metaBrackets = metaBrackets;
		this.arrBrackets = arrBrackets;
		this.invalid = this.metaBrackets[0] + "{=" + this.orLogic + this.andLogic;
		this.orLogic = orLogic;
		this.andLogic = andLogic;
	}

	/**
	 * get a line from string
	 */
	@Override
	public ILine getLineFromString(String str) 
	{
		if(str.contains(this.orLogic) || str.contains(this.andLogic))
			return new LineDynamicLogic(str, this.orLogic, this.andLogic, this.sep, this.quote, this.metaBrackets, this.arrBrackets, this.invalid);
		else if(str.contains("="))
		{
			return new LineArray(str,this.sep,this.quote,this.metaBrackets,this.arrBrackets,this.invalid);
		}
		else if(str.contains("" + this.metaBrackets[0]) || str.contains("{"))
			return new LineMeta(str,this.sep,this.quote,this.metaBrackets,this.invalid);

		return new Line(str,this.sep,this.quote,this.invalid);
	}
}
