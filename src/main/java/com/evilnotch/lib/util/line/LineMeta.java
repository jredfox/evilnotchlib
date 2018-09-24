package com.evilnotch.lib.util.line;

import com.evilnotch.lib.minecraft.util.NBTUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.util.LineUtil;

import net.minecraft.nbt.NBTTagCompound;

public class LineMeta extends Line implements ILineMeta{

	/**
	 * standard metadata for everything that is not an int
	 */
	public String meta = "";
	/**
	 * primitive version when it is a number
	 */
	public Number metaData = null;
	/**
	 * save the id of the primitive/string type if any
	 */
	public char metaDataId = ' ';
	/**
	 * if true this will tell it to put "" between the meta string
	 */
	public boolean metaQuote;
	/**
	 * this in mc is NBTTagCompound in pure java it's either string or json you decide
	 */
	public NBTTagCompound nbt = null;
	/**
	 * this can be customized besides just "<",">" as brackets
	 */
	public char[] metaBrackets;
	
	public LineMeta(String str)
	{
		this(str,LineUtil.sep,LineUtil.quote,LineUtil.metaBrackets,LineUtil.lineInvalid);
	}
	/**
	 * Equivalent to LineItemstack except it's a faster parser
	 */
	public LineMeta(String str, char sep,char quote,char[] metaBrackets,String invalid) 
	{
		super(str,sep,quote,invalid);
		this.metaBrackets = metaBrackets;
		int currentIndex = this.getId().length();
		if(this.hasQuote)
			currentIndex += 2;
		
		this.meta = LineUtil.getFirstBrackets(currentIndex, str,this.quote, this.metaBrackets[0], this.metaBrackets[1]);
		if(this.meta == null)
			this.meta = "";
		
		currentIndex += this.meta.isEmpty() ? 0 : this.meta.length()+2;
		//calculate the current index before parsing string meta first so it goes straight to the nbt
		if(this.meta.startsWith("" + this.quote))
		{
			this.metaQuote = true;
			this.meta = this.meta.substring(1, this.meta.length()-1);
		}
		else if(JavaUtil.isStringNum(this.meta))
		{
			this.metaData  = (Number)LineUtil.parseWeight(this.meta,this.quote);
			this.metaDataId = JavaUtil.getNumId(this.meta);
		}
		
		this.nbt = NBTUtil.getNBTFromString(LineUtil.getBrackets(currentIndex, str,this.quote, '{', '}'));
	}
	
	public boolean hasStringMeta()
	{
		return !this.meta.isEmpty() && this.metaData == null;
	}
	
	public boolean hasMetaDataNum()
	{
		return this.metaData != null;
	}
	
	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}
	@Override
	public boolean equalsMeta(ILine other)
	{
		if(!(other instanceof ILineMeta))
			return LineUtil.isNullMeta(this);
		
		String[] meta = this.getMetaData();
		String[] otherMeta = ((ILineMeta)other).getMetaData();
		
		if(meta.length != otherMeta.length)
			return false;
		
		for(int i=0;i<meta.length;i++)
		{
			String m = meta[i];
			String otherm = otherMeta[i];
			if(m == null)
				m = "";
			if(otherm == null)
				otherm = "";
			if(!m.equals(otherm))
				return false;
		}
		return true;
	}
	
	public int getMetaInt(){
		return (int)this.metaData;
	}
	public long getMetaLong(){
		return (long)this.metaData;
	}
	public double getMetaDouble(){
		return (double)this.metaData;
	}
	public String getMetaString() {
		return this.meta;
	}
	
	@Override
	public String toString(boolean comparible)
	{
		String m = "";
		if(!this.meta.isEmpty())
		{
			String me = this.meta;
			if(this.metaQuote && !comparible)
				me = "\"" + me + (this.metaDataId != ' ' ? this.metaDataId : "") + "\"";
			m += " " + this.metaBrackets[0] + me + "" + this.metaBrackets[1];
		}
		if(this.nbt != null)
			m += " " + this.nbt;
		return super.toString(comparible) + m;
	}
	
	public String getNBTString()
	{
		if(this.nbt == null)
			return null;
		return this.nbt.toString();
	}
	
	@Override
	public String[] getMetaData()
	{
		return new String[]{this.meta,this.getNBTString()};
	}

}
