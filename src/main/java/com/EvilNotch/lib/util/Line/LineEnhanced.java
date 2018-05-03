package com.EvilNotch.lib.util.Line;

import java.util.ArrayList;

import com.EvilNotch.lib.util.ICopy;
import com.EvilNotch.lib.util.JavaUtil;

public class LineEnhanced extends LineItemStackBase implements IMultiHead{
	
	public ArrayList<Object> heads = new ArrayList();//array of primitive values and strings
	public ArrayList<Character> ids = new ArrayList();//char identifiers for all the primitive values
	public boolean fancy = false;
	protected char lbracket = '[';
	protected char rbracket = ']';
	
	public LineEnhanced(String s,char sep, char q,char... invalid){
		this(s,sep,q,'[',']',false,invalid);
	}
	public LineEnhanced(String s,char sep, char q,char lbracket,char rbracket,boolean fancyLine,char... invalid){
		super(s,sep,q,invalid);
		init(lbracket,rbracket);
		if(s.contains("="))
		{
			s = LineBase.getParts(s, "=")[1];
			
			//remove brackets before parsing
			s = s.trim();
			char c = s.substring(0, 1).charAt(0);
			
			if(JavaUtil.isSpecialChar(c) && c != q || c == lbracket)
				s = s.substring(1, s.length()-1);//remove brackets
			this.lbracket = lbracket;
			this.rbracket = rbracket;
			parseHeads(s);
		}
		this.fancy = fancyLine;
	}
	/**
	 * Line Dynamic Logic patches
	 */
	protected void init(char lbracket, char rbracket) {
		if(lbracket == ' ')
			return;
		//dynamic support for object oriented fixes and patches
		String bracket = "" + lbracket + rbracket;
		if(!LineDynamicLogic.nbts.contains(bracket))
			LineDynamicLogic.nbts.add(bracket);
	}
	public LineEnhanced(String s){
		this(s,':','"','#');
	}
	public LineEnhanced(String s, boolean b){
		this(s);
		this.fancy  = b;
	}
	public LineEnhanced(String s,boolean b,char sep, char q,char... invalid){
		this(s,sep,q,invalid);
		this.fancy = b;
	}

	protected void parseHeads(String str) {
		String[] parts = getSubs(str);
		for(String weight : parts)
			if(!weight.equals(""))
				heads.add(parseWeight(weight) );
	}
	/**
	 * Basic string filter replace all , if not inside quote then split them
	 */
	protected String[] getSubs(String str) {
		String line = "";
		boolean insideQuote = false;
		for(int i=0;i<str.length();i++)
		{
			String ch = str.substring(i, i+1);
			if(ch.equals("" + this.quote))
				insideQuote = !insideQuote;
			
			if(ch.equals(",") && !insideQuote)
				ch = "\u00A9";
			line += ch;
		}
		return line.split("\u00A9");
	}
	public Object parseWeight(String weight) {
		String whitespaced = LineBase.toWhiteSpaced(weight);
		String lowerCase = whitespaced.toLowerCase();
		int size = whitespaced.length();
		
		if(lowerCase.equals("true") || lowerCase.equals("false"))
		{
			this.ids.add(' ');
			return Boolean.parseBoolean(whitespaced);
		}
		if(LineBase.isStringNum(whitespaced))
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
				obj = Long.parseLong(num);
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
			if(flag)
				idNum = JavaUtil.toUpperCaseChar(idNum);
			this.ids.add(idNum);
			return obj;
		}
		else{
			if(!weight.contains("" + this.quote))
			{
				this.ids.add(' ');
				return weight.trim();
			}
			else{
				this.ids.add(this.quote);
				return LineBase.parseQuotes(weight, weight.indexOf(this.quote), "" + this.quote);
			}
		}
	}
	/**
	 * Only returns first head when there might be many
	 */
	@Override
	public Object getHead()
	{
		if(this.heads.size() > 0)
			return this.heads.get(0);
		return null;
	}
	
	@Override
	public String getString(boolean comparible){
		String head = getDisplayHeads(comparible);
		String equals = "";
		if(this.heads.size() > 0)
			equals = " = ";
		return super.getString(comparible) + equals + head;
	}
	@Override
	public String toString(){
		String head = getDisplayHeads(false);
		String equals = "";
		if(this.heads.size() > 0)
			equals = " = ";
		return super.toString() + equals + head;
	}
	
	@Override
	public boolean equals(Object obj,boolean compareHead)
	{
		if(!compareHead)
			return super.equals(obj, compareHead);
		ILine compare = (ILine)obj;
		
		if(!this.hasHead() || !(obj instanceof LineEnhanced))
			return super.equals(obj,compareHead) && this.heads.size() <= 1;
		
		if(obj instanceof LineEnhanced){
			LineEnhanced line = (LineEnhanced)obj;
			return super.equals(obj,compareHead) && this.heads.equals(line.heads);
		}
		return false;
	}
	
	public String getDisplayHeads(boolean comparible) {
		String str = LineBase.toWhiteSpaced("" + this.lbracket);
		for(int i=0;i<this.heads.size();i++)
		{
			str += getDisplayHead(i,comparible) + ",";
			if(this.fancy)
				str += " ";
		}
		int count = this.fancy ? 2 : 1;
		if(!str.equals(""))
			str = str.substring(0, str.length()-count);
		return str + LineBase.toWhiteSpaced("" + this.rbracket);
	}
	public String getDisplayHead(int index) {
		return getDisplayHead(index,false);
	}
	public String getDisplayHead(int index,boolean comparible) {
		String str = "";
		if(index >= this.heads.size())
			return str;
		Object obj = this.heads.get(index);
		String head = String.valueOf(obj);
		String id = LineBase.toWhiteSpaced("" + ids.get(index));
		if(obj instanceof String && !id.equals(""))
		{
			if(comparible)
				id = "";
			str += id + head + id;
		}
		else
			str += head + id;

		return str;
	}
	@Override
	public Object getHead(int index, int pos){
		if(pos == 0)
			return this.heads.get(index);
		return null;
	}

	/**
	 * Pos is used for line dynamic logic or another thing but, default pos is 0
	 */
	@Override
	public ArrayList<Object> getHeads(int pos) {
		if(pos == 0)
			return this.heads;
		return null;
	}

	@Override
	public int posSize() {
		return this.heads.size() > 0 ? 1 : 0;
	}
	
	/**
	 * Overridden as additional data is necessary here
	 */
	@Override
	public ICopy copy(){
		return new LineEnhanced(this.getString(),this.seperator,this.quote,this.lbracket,this.rbracket,this.fancy,this.invalidParsingChars.toCharArray());
	}
	
	public char getLBracket(){return this.lbracket;}
	public char getRBracket(){return this.rbracket;}

}
