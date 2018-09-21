package com.evilnotch.lib.util.primitive;

import com.evilnotch.lib.util.JavaUtil;

/**
 * a modifyable integer object that can convert not just cast between other primitive values
 * @author jredfox
 */
@SuppressWarnings("serial")
public class IntObj extends Number implements IModNumber{
	
	public int integer;
	
	public IntObj(int i){
		this.integer = i;
	}

	@Override
	public double doubleValue() {
		return (double)this.integer;
	}

	@Override
	public float floatValue() {
		return (float)this.integer;
	}

	@Override
	public int intValue() {
		return this.integer;
	}

	@Override
	public long longValue() {
		return (long)this.integer;
	}
	@Override
	public byte byteValue(){
		return JavaUtil.castByte(this.integer);
	}
	@Override
	public short shortValue(){
		return JavaUtil.castShort(this.integer);
	}

	@Override
	public void setInt(int i) {
		this.integer = i;
	}

	@Override
	public void setLong(long l) {
		this.integer = JavaUtil.castInt(l);
	}

	@Override
	public void setByte(byte b) {
		this.integer = (int)b;
	}

	@Override
	public void setShort(short s) {
		this.integer = (int)s;
	}

	@Override
	public void setFloat(float f) {
		this.integer = JavaUtil.castInt(f);
	}

	@Override
	public void setDouble(double d) {
		this.integer = JavaUtil.castInt(d);
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Number)){
			return false;
		}
		Number num = (Number)obj;
		return this.intValue() == num.intValue();
	}
	@Override 
	public String toString(){
		return "" + this.intValue();
	}

}
