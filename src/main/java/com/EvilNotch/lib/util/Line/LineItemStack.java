package com.EvilNotch.lib.util.Line;

import com.EvilNotch.lib.util.JavaUtil;

public class LineItemStack extends LineItemStackBase implements IHead{

	public int head = -1;
	public float fhead = -1.0F;
	public double dhead = -1.0D;
	public boolean bhead = false;
	public byte bytehead = -1;
	public short shorthead = -1;
	public long lhead = -1;
	public String strhead = null;
	
	public boolean hasihead = false;
	public boolean hasfhead = false;
	public boolean hasdhead = false;
	public boolean hasbhead = false;
	public boolean hasStringHead = false;
	public boolean hasbytehead = false;
	public boolean hasshorthead = false;
	public boolean haslhead = false;
	public boolean strLegacy = false;
	public char idNum = ' ';
	
	public LineItemStack(String s,char sep, char q,char... invalid)
	{
		super(s,sep,q,invalid);
		if(s.contains("="))
			parseWeight(s);
	}
	
	public LineItemStack(String s) 
	{
		super(s);
		if(s.contains("="))
			parseWeight(s);
	}
	/**
	 * Optimized Constructor
	 */
	public LineItemStack(LineEnhanced line) {
		super(line);		
		if(line.hasHead())
			parseHead(line.getDisplayHead(0));
	}

	/**
	 * Returns the head of the line supports [int,boolean,byte,short,long,float,double,String]
	 */
	protected void parseWeight(String weight) 
	{
		//filter string
		String[] parts = LineBase.getParts(weight, "=");
		weight = parts[1];
		
		parseHead(weight);
	}
	
	protected void parseHead(String weight) 
	{
		String whitespaced = LineBase.toWhiteSpaced(weight);
		String lowerCase = whitespaced.toLowerCase();
		int size = whitespaced.length();
		
		if(lowerCase.equals("true") || lowerCase.equals("false"))
		{
			this.bhead = Boolean.parseBoolean(whitespaced);
			this.hasbhead = true;
		}
		
		if(LineBase.isStringNum(whitespaced))
		{
			String lastChar = lowerCase.substring(size-1, size);
			boolean hasId = "bslfdi".contains(lastChar);
			boolean flag = "BSLFDI".contains(whitespaced.substring(size-1, size));
			boolean dflag = whitespaced.contains(".");
			
			if(hasId)
				this.idNum = lastChar.charAt(0);
			
			String num = hasId ? whitespaced.substring(0, whitespaced.length()-1) : whitespaced;
			
			//do general first
			if(this.idNum == ' ' && !dflag)
			{
				long l = Long.parseLong(num);
				this.lhead = l;
				this.head = (int)l;
				this.shorthead = (short)l;
				this.bytehead = (byte)l;
				this.haslhead = true;
			}
			//byte
			if(this.idNum == 'b'){
				this.bytehead = Byte.parseByte(num);
				this.hasbytehead = true;
			}
			else if(this.idNum == 's'){
				this.shorthead = Short.parseShort(num);
				this.hasshorthead = true;
			}
			else if(this.idNum == 'l'){
				this.lhead = Long.parseLong(num);
				this.haslhead = true;
			}
			else if(this.idNum == 'i'){
				this.head = Integer.parseInt(num);
				this.hasihead = true;
			}
			else if(this.idNum == 'f'){
				this.fhead = Float.parseFloat(num);
				this.hasfhead = true;
			}
			else if(this.idNum == 'd'){
				this.dhead = Double.parseDouble(num);
				this.hasdhead = true;
			}
			else if(dflag){
				this.dhead = Double.parseDouble(num);
				this.fhead = Float.parseFloat(num);
				this.hasdhead = true;
				this.hasfhead = true;
			}
			if(flag)
				this.idNum = JavaUtil.toUpperCaseChar(this.idNum);
		}
		else{
			if(!weight.contains("" + this.quote))
			{
				this.strhead = weight.trim();
				this.strLegacy = true;
			}
			else
				this.strhead = LineBase.parseQuotes(weight, weight.indexOf(this.quote), "" + this.quote);
			this.hasStringHead = true;
		}
		
	}

	@Override
	public String toString()
	{
		return super.toString() + " = " + getDisplayHead(false);
	}
	@Override
	public String getString(boolean comparible)
	{
		String str = super.getString(comparible);
		if(this.getHead() != null)
			str += " = " + this.getDisplayHead(comparible);
		return str;
	}
	/**
	 * Returns one of the following[int,boolean,byte,short,long,float,double,String]
	 */
	@Override
	public Object getHead()
	{
		if(this.haslhead)
			return this.lhead;
		if(this.hasihead)
			return this.head;
		if(this.hasdhead)
			return this.dhead;
		if(this.hasfhead)
			return this.fhead;
		if(this.hasStringHead)
			return this.strhead;
		if(this.hasbhead)
			return this.bhead;
		if(this.hasbytehead)
			return this.bytehead;
		if(this.hasshorthead)
			return this.shorthead;
		return null;
	}
	public String getDisplayHead(boolean comparible)
	{
		String head = String.valueOf(this.getHead());
		String q = "";
		if(this.hasStringHead && !this.strLegacy)
			q = "" + this.quote;
		if(comparible)
			q = "";
		return  q + head + LineBase.toWhiteSpaced("" + this.idNum) + q;
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
