package com.EvilNotch.lib.util.Line;

import java.lang.reflect.Constructor;

import com.EvilNotch.lib.util.ICopy;

import net.minecraft.util.ResourceLocation;

public class LineBase implements ILine
{
	public String modid;
	public String name;
	public boolean legacyParsed = false;
	
	protected String invalidParsingChars = "";
	protected char seperator = ':';
	protected char quote = '"';
	
	protected LineBase(){}
	/**
	 * Optimized Constructor
	 */
	public LineBase(LineEnhanced line){
		//LineBase
		this.modid = line.modid;
		this.name = line.name;
		this.invalidParsingChars = line.invalidParsingChars;
		this.legacyParsed = line.legacyParsed;
		this.quote = line.quote;
		this.seperator = line.seperator;
	}
	
	public LineBase(String s,char sep,char q,char... invalid)
	{
		this.seperator = sep;
		this.quote = q;
		this.invalidParsingChars = getInvChars();
		for(char c : invalid)
			if(!this.invalidParsingChars.contains("" + c))
				this.invalidParsingChars += c;
		parse(s,sep,q,invalid);
	}
	
	public LineBase(String s) 
	{
		this.invalidParsingChars = getInvChars();//get invalid chars per object override
		parseBase(s);
	}
	protected String getInvChars(){
		return "<{=";
	}
	@Override
	public void parse(String s,char sep, char q, char...invalid) {
		parseBase(s);//for linebase this is all you need since the constructor takes care of the rest
	}
	
	protected void parseBase(String s) {
	try{
		  String[] stack = getStrId(s);
		  if(stack != null)
		  {
			  this.modid = stack[0];
			  if(stack.length > 1)
				  this.name = stack[1];
			  else
				  this.name = null;//For simplistic line parsing line is the line no string
		  }
		  else{
			  this.modid = null;
			  this.name = null;
		    }
	    }
		catch(Exception e)
		{
			e.printStackTrace();
			this.modid = null;
			this.name = null;
		}
	}

	/**
	 * Doesn't support Quotes in modid:block! Returnes modid[0] name[1]
	 * @param s
	 * @return
	 */
	protected String[] getStrId(String s) 
	{
		String compare = s;
		if(s.contains("="))
			compare = s.split("=")[0];//ensures it is to the left of the = sign
		
		String strid = null;
		if(containsQuote(s))
		{
			strid = parseQuotes(compare,0,"" + this.quote);
			return getParts(strid, "" + this.seperator);
		}
		else{
			legacyParsed = true;
			return getParts(parseLoosly(compare),"" + this.seperator);//old format supports no spacing however is easy to edit and create
		}
	}

	protected boolean containsQuote(String s) {
		for(int i=0;i<s.length();i++)
		{
			String chars = s.substring(i, i+1);
			if(invalidParsingChars.contains(chars))
				return false;
			if(chars.equals("" + this.quote))
				return true;
		}
		return false;
	}

	public String parseLoosly(String s) {
		String str = "";
		for(int i=0;i<s.length();i++)
		{
			String charstr = s.substring(i, i+1);
			//check for chars that stop parsing before the = sign
			if(invalidParsingChars.contains("" + charstr))
				return str.trim();
			
			str += charstr;
		}
		return str.trim();
	}
	/**
	 * Separates a string dynamically supports vanilla format
	 * Exactly like resourcelocation but, supports more then just ":"
	 */
	public static String[] getParts(String s, String split)
	{
		if(split.equals(".") || split.equals("|"))
			split = "\\" + split;
		else if(split.equals("||"))
			split = "\\|\\|";
		s = s.replaceFirst(split, "\u00A9");
		return s.split("\u00A9");
	}
	/**
	 * Unlike get parts does the entire string do not use if you don't know what you are doing use getParts() instead
	 */
	public static String[] split(String s, String split){
		if(split.equals(".") || split.equals("|"))
			split = "\\" + split;
		else if(split.equals("||"))
			split = "\\|\\|";
		
		return s.split(split);
	}

	/**
	 * Ejects a string that is whitespaced
	 * @param s
	 * @return
	 */
	public static String toWhiteSpaced(String s)
	{
		return s.replaceAll("\\s+", "");
	}
	/**
	 * Returns true if character is a number
	 * @param s
	 * @return
	 */
	public static boolean isCharNum(String s) 
	{
		if(s.equals("0") || s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4")|| s.equals("5")|| s.equals("6")|| s.equals("7")|| s.equals("8")|| s.equals("9"))
			return true;
		return false;
	}
	public static boolean isStringNum(String s)
	{
		String valid = "1234567890.";
		String valid_endings = "bslfdi";//byte,short,long,float,double,int
		int indexdot = 0;
		if(s.indexOf(".") == 0 || s.indexOf(".") == s.length() - 1)
			return false;
		for(int i=0;i<s.length();i++)
		{
			String character = s.substring(i, i+1);
			boolean lastindex = i == s.length() -1;
			if(character.equals("."))
			{
				if(indexdot >= 1)
					return false;
				indexdot++;
			}
			if(!valid.contains(character))
			{
				if(i + 1 < s.length())
					return false;
				if(lastindex)
					return valid_endings.contains(character.toLowerCase());
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineBase))
			return false;
		LineBase line = (LineBase)obj;
		String line1 = "" + this.modid + ":" + this.name;
		String line2 =  "" + line.modid + ":" + line.name;
		return line1.equals(line2);
	}
	/**
	 * if head != null should line compare head? Used for ConfigBase
	 */
	@Override
	public boolean equals(Object obj,boolean compareHead)
	{
		if(!(obj instanceof ILine) || !compareHead)
			return this.equals(obj);
		
		Object head = ((ILine)obj).getHead();
		if(this.getHead() == null)
			return head == null && this.equals(obj);
		
		return this.getHead().equals(head) && this.equals(obj);
	}
	
	@Override
	public String toString()
	{
		String quote = this.legacyParsed ? "" : "" + this.quote;
		return  quote + this.modid + "" + this.seperator + this.name + quote;	
	}
	/**
	 * Gets lines string for configuration files overridden in LineItemStackBase
	 * @return
	 */
	@Override
	public String getString()
	{
	   return getString(false);
	}
	@Override
	public String getComparible() {
		return this.getString(true);
	}
	public static boolean isDynamicLogic(String s)
	{
		return s.contains("\\|") || s.contains("|");
	}
	public static int findFirstQuote(String s,char q) 
	{
		for(int i=0;i<s.length();i++)
		{
			String str = s.substring(i, i++);
			if(str.equals("" + q))
				return i;
		}
		return -1;
	}
	public static String parseQuotes(String s, int index,String q) 
	{
		if(index == -1)
			return "";
		String strid = "";
		int quote = 0;
		for(int i=index;i<s.length();i++)
		{
			if(quote == 2)
				break; //if end of parsing object stop loop and return getParts(strid,":");
			String tocompare = s.substring(i,i+1);
			boolean contains = q.contains(tocompare);
			
			if(contains)
				quote++;
			if(!contains && quote > 0)
				strid += tocompare;
		}
		return strid;
	}
	
	public static String parseQuotes(String s, int index,char lquote,char rquote) 
	{
		return parseQuotes(s,index,"" + lquote + rquote);
	}
	public static String parseQuotes(String s, int index) 
	{
		return parseQuotes(s,index,"\"");
	}
	@Override
	public String getModPath()
    {
		String strname = "";
		String compare = "" + this.name;
		if(!compare.equals("null") )
			strname = "" + this.seperator + this.name;
		return  new String(this.modid + strname);
    }
	@Override
	public String getModid() {
		return this.modid;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ILine getLineBase() {
		return this;
	}

	@Override
	public Object getHead() {
		return null;
	}
	/**
	 * All LineBase Must override this if It doesn't Have the Default Constructor {string,char,char,char[]}
	 */
	@Override
	public ICopy copy() {
		try {
			Constructor constructor = this.getClass().getConstructor(String.class,char.class,char.class,char[].class);
			return (ICopy)constructor.newInstance(this.getString(),this.seperator,this.quote,this.invalidParsingChars.toCharArray());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public int compareTo(Object arg0) {
		ILine line = (ILine)arg0;
		return this.getComparible().compareTo(line.getComparible());
	}
	@Override
	public boolean hasHead(){
		return this.getHead() != null;
	}
	@Override
	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(this.getModPath());
	}
	
	public String getString(boolean comparable) {
		String quote = this.legacyParsed ? "" : "" + this.quote;
		if(comparable)
			quote = "";
		return quote + getModPath() + quote;
	}
	//getters
	@Override
	public char getSeperator(){return this.seperator;}
	@Override
	public char getQuote(){return this.quote;}
	@Override
	public String getInvalid(){return this.invalidParsingChars;}
	
}
