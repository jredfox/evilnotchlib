package com.evilnotch.lib.util.line;

import java.util.List;

import com.evilnotch.lib.util.JavaUtil;
/**
 * used in LineArray for multi index heads
 * @author jredfox
 *
 */
public interface ILineHeadArray extends ILineHead{
	
	public List<Object> getHeads();
	public Object getHead(int index);
	public void setHead(Object obj,int index);
	public void addHead(Object obj);
	public int size();
	
	/**
	 * get an inner array
	 */
	public List<Object> getHeadList(int index);
	
	/**
	 * for people trying to use this as ILineHead instead
	 */
	@Override
	public default void setHead(Object obj){
		this.setHead(obj, 0);
	}
	/**
	 * for people trying to use this as ILineHead instead
	 */
	@Override
	public default Object getHead(){
		return this.getHead(0);
	}
	
	default public int getInt(int index){
		return JavaUtil.getInt((Number)this.getHead(index));
	}
	default public short getShort(int index){
		return JavaUtil.getShort((Number)this.getHead(index));
	}
	default public long getLong(int index){
		return JavaUtil.getLong((Number)this.getHead(index));
	}
	default public byte getByte(int index){
		return JavaUtil.getByte((Number)this.getHead(index));
	}
	default public float getFloat(int index){
		return JavaUtil.getFloat((Number)this.getHead(index));
	}
	default public double getDouble(int index){
		return JavaUtil.getDouble((Number)this.getHead(index));
	}
	default public String getString(int index){
		return (String)this.getHead(index);
	}
	default public boolean getBoolean(int index){
		return (Boolean)this.getHead(index);
	}
	
	default public int getInt(List<Object> obj,int index){
		return JavaUtil.getInt((Number)obj.get(index));
	}
	default public short getShort(List<Object> obj,int index){
		return JavaUtil.getShort((Number)obj.get(index));
	}
	default public long getLong(List<Object> obj,int index){
		return JavaUtil.getLong((Number)obj.get(index));
	}
	default public byte getByte(List<Object> obj,int index){
		return JavaUtil.getByte((Number)obj.get(index));
	}
	default public float getFloat(List<Object> obj,int index){
		return JavaUtil.getFloat((Number)obj.get(index));
	}
	default public double getDouble(List<Object> obj,int index){
		return JavaUtil.getDouble((Number)obj.get(index));
	}
	default public String getString(List<Object> obj,int index){
		return (String)obj.get(index);
	}
	default public boolean getBoolean(List<Object> obj,int index){
		return (Boolean)obj.get(index);
	}
	/**
	 * unlike get head list this will return another list at the inner arraylist you already had gotten
	 */
	default public List<Object> getHeadList(List<Object> obj,int index){
		return (List<Object>)obj.get(index);
	}
}
