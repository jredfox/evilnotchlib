package com.EvilNotch.lib.util.Line;

import java.util.ArrayList;
import java.util.Arrays;

import com.EvilNotch.lib.util.ICopy;
import com.EvilNotch.lib.util.JavaUtil;

public class LineEnhanced extends LineItemStackBase implements IMultiHead,IHead{
	
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
			char c = s.charAt(0);
			
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

	protected void parseHeads(String str) 
	{
		String[] parts = getSubs(str);
		for(String weight : parts)
		{
			if(weight.equals(""))
				continue;
			weight = weight.trim();
			char c = weight.charAt(0);
			if(c == '[' || c == '(')
			{				
				parseStaticArray(weight,c);
				this.ids.add(' ');//have the char to describe nothing to sync actual indexes
			}
			else
				heads.add(parseWeight(weight,this.ids) );
		}
	}
	public void parseStaticArray(String weight,char c) 
	{
		weight = weight.substring(1, weight.length()-1);
		String[] parts = getSubs(weight);
		Object[] objs = new Object[parts.length];
		ArrayList<Character> chars = new ArrayList<Character>();
		
		for(int i=0;i<parts.length;i++)
		{
			objs[i] = parseWeight(parts[i],chars);
		}
		this.heads.add(new ArrEntry(objs,chars,c));
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
			if(ch.equals("[") && !insideQuote || ch.equals("(") && !insideQuote)
			{
				boolean param = ch.equals("(");
				int rbracket = LineDynamicLogic.getIndexRBracket(i, str, ch.charAt(0),param ? ')' : ']',this.quote);
				String staticArr = str.substring(i, rbracket+1);
				line += staticArr;
				i += staticArr.length()-1;
				continue;
			}
			if(ch.equals("" + this.quote))
				insideQuote = !insideQuote;
			
			if(ch.equals(",") && !insideQuote)
				ch = "\u00A9";
			line += ch;
		}
		return line.split("\u00A9");
	}
	public Object parseWeight(String weight,ArrayList<Character> ids) {
		String whitespaced = LineBase.toWhiteSpaced(weight);
		String lowerCase = whitespaced.toLowerCase();
		int size = whitespaced.length();
		
		if(lowerCase.equals("true") || lowerCase.equals("false"))
		{
			ids.add(' ');
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
			ids.add(idNum);
			return obj;
		}
		else{
			if(!weight.contains("" + this.quote))
			{
				ids.add(' ');
				return weight.trim();
			}
			else{
				ids.add(this.quote);
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
		if(!compareHead || !(obj instanceof ILine))
			return super.equals(obj, compareHead);
		
		ILine line = (ILine)obj;
		if(!this.hasHead())
		{
			return super.equals(obj,compareHead) && !line.hasHead();//works
		}
		else if (!(obj instanceof LineEnhanced))
		{
			if(line.getHead() == null)
				return super.equals(obj,compareHead) && this.getHead() == null && this.heads.size() <= 1;
			else
				return super.equals(obj,compareHead) && this.getHead().equals(line.getHead()) && this.heads.size() <= 1;
		}
		
		if(obj instanceof LineEnhanced){
			LineEnhanced line2 = (LineEnhanced)obj;
			return super.equals(obj,compareHead) && this.heads.equals(line2.heads);
		}
		return false;
	}
	
	public String getDisplayHeads(boolean comparible) 
	{
		if(this.heads.size() == 0)
			return "";
		String str = LineBase.toWhiteSpaced("" + this.lbracket);
		for(int i=0;i<this.heads.size();i++)
		{
			str += getDisplayHead(i,comparible) + ",";
			
			if(this.fancy)
				str += " ";
		}
		int count = this.fancy ? 2 : 1;
	    return str.substring(0, str.length()-count) + LineBase.toWhiteSpaced("" + this.rbracket);
	}
	public String getDisplayHead(int index) {
		return getDisplayHead(index,false);
	}
	public String getDisplayHead(int index,boolean comparible) 
	{
		String str = "";
		if(index >= this.heads.size())
			return str;
		Object obj = this.heads.get(index);
		if(obj instanceof ArrEntry)
		{
			ArrEntry e = (ArrEntry)obj;
			str = "" + e.lbracket;
			
			for(int i=0;i<e.list.size();i++)
			{
				str += getStringDisplay(e.list.get(i),e.ids,i,comparible) + ",";
				if(this.fancy)
					str += " ";
			}
			int count = this.fancy ? 2 : 1;
			return str.substring(0, str.length()-count) + e.rbracket;
		}
		return getStringDisplay(obj,this.ids,index,comparible);
	}
	
	protected String getStringDisplay(Object obj,ArrayList<Character> ids,int index,boolean comparible) 
	{
		String str = "";
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
	public Object getHead(int index)
	{
		return this.heads.get(index);
	}
	/**
	 * Pos is used for line dynamic logic or another thing but, default pos is 0
	 */
	@Override
	public ArrayList<Object> getHeads() 
	{
		return this.heads;
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
	
	@Override
	public int getInt(int index){
		return JavaUtil.getInt((Number)this.heads.get(index));
	}
	@Override
	public short getShort(int index){
		return JavaUtil.getShort((Number)this.heads.get(index));
	}
	@Override
	public long getLong(int index){
		return JavaUtil.getLong((Number)this.heads.get(index));
	}
	@Override
	public byte getByte(int index){
		return JavaUtil.getByte((Number)this.heads.get(index));
	}
	@Override
	public float getFloat(int index){
		return JavaUtil.getFloat((Number)this.heads.get(index));
	}
	@Override
	public double getDouble(int index){
		return JavaUtil.getDouble((Number)this.heads.get(index));
	}
	@Override
	public String getString(int index) {
		return (String)this.heads.get(index);
	}
	@Override
	public boolean getBoolean(int index) {
		return (Boolean)this.heads.get(index);
	}
	@Override
	public ArrEntry getStaticArray(int index){
		return (ArrEntry) this.heads.get(index);
	}
	@Override
	public int getInt(ArrEntry head,int index){
		return JavaUtil.getInt((Number)head.list.get(index));
	}
	@Override
	public short getShort(ArrEntry head,int index){
		return JavaUtil.getShort((Number)head.list.get(index));
	}
	@Override
	public long getLong(ArrEntry head,int index){
		return JavaUtil.getLong((Number)head.list.get(index));
	}
	@Override
	public byte getByte(ArrEntry head,int index){
		return JavaUtil.getByte((Number)head.list.get(index));
	}
	@Override
	public float getFloat(ArrEntry head,int index){
		return JavaUtil.getFloat((Number)head.list.get(index));
	}
	@Override
	public double getDouble(ArrEntry head,int index){
		return JavaUtil.getDouble((Number)head.list.get(index));
	}
	@Override
	public String getString(ArrEntry head,int index) {
		return (String)head.list.get(index);
	}
	@Override
	public boolean getBoolean(ArrEntry head,int index) {
		return (Boolean)head.list.get(index);
	}
	
	@Override
	public int getInt(){
		return JavaUtil.getInt((Number)this.getHead());
	}
	@Override
	public short getShort(){
		return JavaUtil.getShort((Number)this.getHead());
	}
	@Override
	public long getLong(){
		return JavaUtil.getLong((Number)this.getHead());
	}
	@Override
	public byte getByte(){
		return JavaUtil.getByte((Number)this.getHead());
	}
	@Override
	public float getFloat(){
		return JavaUtil.getFloat((Number)this.getHead());
	}
	@Override
	public double getDouble(){
		return JavaUtil.getDouble((Number)this.getHead());
	}
	@Override
	public String getStringHead() {
		return (String)this.getHead();
	}
	@Override
	public boolean getBoolean() {
		return (Boolean)this.getHead();
	}

}
