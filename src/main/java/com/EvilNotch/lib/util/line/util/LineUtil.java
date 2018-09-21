package com.evilnotch.lib.util.line.util;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.ILineMeta;
import com.evilnotch.lib.util.line.Line;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.LineMeta;

public class LineUtil {
	//this section is to remove hard coding the same varibles multiple times
	public static final String orLogic = "||";
	public static final String andLogic = "&&";
	
	//line char config
	public static final char sep = ':';
	public static final char quote = '"';
	public static final char[] metaBrackets = "<>".toCharArray();
	public static final char[] arrBrackets = "[]".toCharArray();
	public static final String lineInvalid = "<{=";
	
	public static char commentDefault = '#';
	
	/**
	 * use getLinefromString(String str,char sep,char quote,char[] metaBrackets,char[] arrBrackets) instead
	 */
	@Deprecated
	public static ILine getLineFromString(String str)
	{
		if(str.contains("="))
		{
			return new LineArray(str);
		}
		else if(str.contains("" + metaBrackets[0]) || str.contains("{"))
			return new LineMeta(str);

		return new Line(str);
	}
	public static ILine getLineFromString(String str,char sep,char quote,char[] metaBrackets,char[] lrBrackets,String invalid) 
	{
		if(str.contains("="))
		{
			return new LineArray(str,sep,quote,metaBrackets,lrBrackets,invalid);
		}
		else if(str.contains("" + metaBrackets[0]) || str.contains("{"))
			return new LineMeta(str,sep,quote,metaBrackets,invalid);

		return new Line(str,sep,quote,invalid);
	}
	
	/**
	 * get the primitive object from the string
	 */
	public static Object parseWeight(String weight,char quote) 
	{
		String whitespaced = JavaUtil.toWhiteSpaced(weight);
		String lowerCase = whitespaced.toLowerCase();
		int size = whitespaced.length();
		
		if(lowerCase.equals("true") || lowerCase.equals("false"))
		{
			return Boolean.parseBoolean(whitespaced);
		}
		else if(JavaUtil.isStringNum(whitespaced))
		{
			char idNum = ' ';
			String lastChar = lowerCase.substring(size-1, size);
			boolean hasId = "bslfdi".contains(lastChar);
			boolean flag = "BSLFDI".contains(whitespaced.substring(size-1, size));
			boolean dflag = whitespaced.contains(".");
			if(hasId)
				idNum = lastChar.charAt(0);
			
			String num = hasId ? whitespaced.substring(0, whitespaced.length()-1) : whitespaced;
			Object obj = null;
			
			//do general first
			if(idNum == ' ' && !dflag){
				obj = Integer.parseInt(num);//if greater then max value it equals max value
			}
			//byte
			else if(idNum == 'b'){
				obj = Byte.parseByte(num);
			}
			else if(idNum == 's'){
				obj = Short.parseShort(num);
			}
			else if(idNum == 'l'){
				obj = Long.parseLong(num);
			}
			else if(idNum == 'i'){
				obj = Integer.parseInt(num);
			}
			else if(idNum == 'f'){
				obj = Float.parseFloat(num);
			}
			else if(idNum == 'd'){
				obj = Double.parseDouble(num);
			}
			else if(dflag){
				obj = Double.parseDouble(num);
			}
			return obj;
		}
		return weight.contains("" + quote) ? JavaUtil.parseQuotes(weight, 0, "" + quote) : weight.trim();
	}
	
	/**
	 * string filter more sophisticated then standard split. It filters out the char your looking for only if it's not in quotes or in brackets then splits them
	 * skipping over any brackets
	 */
	public static String[] selectString(String input,char split,char q,char lbracket,char rbracket)
	{
		StringBuilder builder = new StringBuilder();
		boolean insideQuote = false;
		for(int i=0;i<input.length();i++)
		{
			char c = input.charAt(i);
			
			if(c == q && c != ' ')
				insideQuote = !insideQuote;
			
			if(c == lbracket && c != ' ' && !insideQuote)
			{
				int rBracket = getRightBracket(i,input,q,lbracket,rbracket);
				builder.append(input.substring(i,rBracket+1));
				i = rBracket;
				continue;//continue will force i++ thus you need the varible = to what it should be next
			}
			
			if(c == split && !insideQuote)
			{
				builder.append(JavaUtil.uniqueSplitter);
			}
			else
			{
				builder.append(c);
			}
		}
		return builder.toString().split(JavaUtil.uniqueSplitter);
	}
	/**
	 * split a string with it skipping things inside of quotes and insideof lrbrackets
	 */
	public static String[] selectString(String input,String split,char q,String lbrackets,String rbrackets)
	{
		StringBuilder builder = new StringBuilder();
		boolean insideQuote = false;
		for(int i=0;i<input.length();i++)
		{
			char c = input.charAt(i);
			
			if(c == q)
				insideQuote = !insideQuote;
			
			if(split.contains("" + c) && !insideQuote)
			{
				int rIndex = i+split.length();
				if(rIndex > input.length())
					rIndex--;
				String s = input.substring(i, i+split.length());
				if(split.equals(s))
				{
					builder.append(JavaUtil.uniqueSplitter);
					i += split.length()-1;
					continue;
				}
			}
			
			if(lbrackets.contains("" + c) && !insideQuote)
			{
				int index = lbrackets.indexOf(c);
				char lbracket = lbrackets.charAt(index);
				char rbracket = rbrackets.charAt(index);
				
				int rBracket = getRightBracket(i,input,q,lbracket,rbracket);
				builder.append(input.substring(i,rBracket+1));
				
				i = rBracket;
				continue;//continue will force i++ thus you need the varible = to what it should be next
			}
			builder.append(c);
		}
		return builder.toString().split(JavaUtil.uniqueSplitter);
	}
	/**
	 * lindex must be the left bracket starting value
	 */
	public static int getRightBracket(int lindex,String str,char q,char lbracket,char rbracket) 
	{
    	int lb = 0;
    	boolean insideQuote = false;
    	for(int i=lindex;i<str.length();i++)
    	{
    		String ch = str.substring(i, i+1);
    		
    		if(ch.charAt(0) == q)
    			insideQuote = !insideQuote;
			
    		if(ch.equals("" + lbracket) && !insideQuote)
    			lb++;
    		else if(ch.equals("" + rbracket) && !insideQuote)
    			lb--;
    		if(lb == 0)
    			return i;
    	}
		return -1;
	}

	public static boolean isNullMeta(ILineMeta meta) 
	{
		for(String s : meta.getMetaData())
		{
			if(!JavaUtil.isStringNullOrEmpty(s))
				return false;
		}
		return true;
	}
	/**
	 * find out if lines are metadata with checks
	 */
	public static boolean isMetaEqual(ILine left, ILine right) 
	{
		boolean lmeta = true;
		boolean rmeta = true;
		if(left instanceof ILineMeta)
			lmeta = ((ILineMeta)left).equalsMeta(right);
		if(right instanceof ILineMeta)
			rmeta = ((ILineMeta)right).equalsMeta(left);
		return lmeta && rmeta;
	}
	/**
	 * get json/nbt/whatever you need to parse
	 */
	public static String getBrackets(int i, String str,char q, char lbracket, char rbracket) {
		if(!str.contains("" + lbracket))
			return null;
		i = str.indexOf(lbracket);
		int index = getRightBracket(i, str,q, lbracket, rbracket);
		return str.substring(i, index+1);
	}
	/**
	 * unlike getBrackets() it gets the first set of brackets that's not inside of quotes
	 */
	public static String getFirstBrackets(int currentIndex, String str, char quote, char lbracket, char rbracket) 
	{
		boolean hasLBracket = false;
		boolean insideQuote = false;
		StringBuilder builder = new StringBuilder();
		for(int i=currentIndex;i<str.length();i++)
		{
			char c = str.charAt(i);
			if(c == quote)
				insideQuote = !insideQuote;
			
			if(c == rbracket && !insideQuote)
				break;
			
			if(hasLBracket)
				builder.append(c);
			
			if(c == lbracket && !insideQuote)
				hasLBracket = true;
		}
		return builder.toString();
	}
}
