package com.evilnotch.lib.util.line;

import com.evilnotch.lib.util.JavaUtil;
/**
 * does your head have a line implement this
 * @author jredfox
 *
 */
public interface ILineHead extends ILine{
	
	public Object getHead();
	public void setHead(Object obj);
	
	default public int getInt(){
		return JavaUtil.getInt((Number)this.getHead());
	}
	default public short getShort(){
		return JavaUtil.getShort((Number)this.getHead());
	}
	default public long getLong(){
		return JavaUtil.getLong((Number)this.getHead());
	}
	default public byte getByte(){
		return JavaUtil.getByte((Number)this.getHead());
	}
	default public float getFloat(){
		return JavaUtil.getFloat((Number)this.getHead());
	}
	default public double getDouble(){
		return JavaUtil.getDouble((Number)this.getHead());
	}
	default public String getString(){
		return (String)this.getHead();
	}
	default public boolean getBoolean(){
		return (Boolean)this.getHead();
	}
   
	
}
