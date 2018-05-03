package com.EvilNotch.lib.util.Line;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class LineItemStackBase extends LineBase
{
	public int meta = -1;
	public String strmeta = null;
	public NBTTagCompound NBT;
	public boolean hasMeta = false;
	public boolean hasStrMeta = false;
	public boolean legacyStrMeta = false;
	
	/**
	 * Optimized Constructor
	 */
	public LineItemStackBase(LineEnhanced line){
		super(line);
		//LineItemStackBase
		this.meta = line.meta;
		this.strmeta = line.strmeta;
		this.hasMeta = line.hasMeta;
		this.hasStrMeta = line.hasStrMeta;
		this.legacyStrMeta = line.legacyStrMeta;
		this.NBT = line.NBT;
	}
	
	protected LineItemStackBase(){}
	
	public LineItemStackBase(String s,char sep, char q,char... invalid)
	{
		super(s,sep,q,invalid);
		parseMeta(s);
		this.NBT = parseNBT(s);
	}

	public LineItemStackBase(String s)
	{
		super(s);//Initiate modid:block and line string
		parseMeta(s);
		this.NBT = parseNBT(s);
	}
	/**
	 * Returns nbt via String from line format
	 * @param s
	 * @return
	 * @throws NBTException 
	 * @Format "modid:block" <int> {NBT} = int
	 */
	protected NBTTagCompound parseNBT(String s)
	{
		String strnbt = "";
		int first = findFirstNBT(s);
		int last = findLastNBT(s);
		if(first != -1 && last != -1)
			strnbt += s.substring(first, last+1);
		else
			return null;
		
		NBTTagCompound nbt = null;
		try {
			nbt = (NBTTagCompound)JsonToNBT.getTagFromJson(strnbt);
		} 
		catch (NBTException e) {return null;}
		
		return nbt;
	}
	/**
	 * Finds first instanceof "{"
	 * @param s
	 * @return
	 */
	protected int findFirstNBT(String s) 
	{
		return s.indexOf('{');
	}
	/**
	 * Finds last instanceof "}"
	 * @param s
	 * @return
	 */
	protected int findLastNBT(String str) 
	{
		for(int i=str.length()-1;i>0;i--)
		{
			if(str.substring(i, i+1).equals("}"))
				return i;
		}
		return -1;
	}
	
	/**
	 * Gets Meta data from <int> || <"string">
	 */
	protected void parseMeta(String str) 
	{
		String strmeta = LineBase.parseQuotes(str, str.indexOf('<'), '<','>');
		if(!strmeta.equals(""))
		{
			if(LineBase.isStringNum(strmeta))
			{
				this.meta = Integer.parseInt(strmeta);
				this.hasMeta = true;
			}
			else{
				if(!strmeta.contains("" + this.quote))
				{
					this.strmeta = strmeta.trim();
					legacyStrMeta = true;
				}
				else
					this.strmeta = LineBase.parseQuotes(strmeta, strmeta.indexOf(this.quote), "" + this.quote);
				this.hasStrMeta = true;
			}
		}
	 }
	
	@Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof LineItemStackBase))
            return this.NBT == null && !this.hasMeta && super.equals(obj);
        LineItemStackBase line = (LineItemStackBase)obj;
        boolean nbt = false;
        if(this.NBT != null && line.NBT != null)
            nbt = this.NBT.equals(line.NBT);
        if(this.NBT == null)
            nbt = line.NBT == null;
        return super.equals(obj) && this.meta == line.meta && nbt && this.hasMeta == line.hasMeta;
    }

    @Override
    public String toString()
    {
        String strid = super.toString();
        String q = legacyStrMeta ? "" : "" + this.quote;
        if(this.hasMeta)
            strid += " <" + this.meta + ">";
        if(this.hasStrMeta)
        	strid += " <" + q + this.strmeta + q + ">";
        return strid + " " + this.NBT;  
    }
    
    @Override
    public String getString(boolean comparible)
    {
    	String str = super.getString(comparible);
    	String q = legacyStrMeta ? "" : "" + this.quote;
    	if(comparible)
    		q = "";
    	
        if(this.hasMeta)
            str += " <" + this.meta + ">";
        if(this.hasStrMeta)
        	str += " <" + q + this.strmeta + q + ">";
        if(this.NBT != null)
            str += " " + this.NBT.toString();
        return str;
    }

}
