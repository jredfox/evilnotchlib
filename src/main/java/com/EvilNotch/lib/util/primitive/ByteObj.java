package com.evilnotch.lib.util.primitive;

import com.evilnotch.lib.util.JavaUtil;

public class ByteObj extends Number implements IModNumber{
	
	public byte b;
	
	public ByteObj(byte b){
		this.b = b;
	}

	@Override
	public double doubleValue() {
		return (double)this.b;
	}

	@Override
	public float floatValue() {
		return (float)this.b;
	}

	@Override
	public int intValue() {
		return (int)this.b;
	}

	@Override
	public long longValue() {
		return (long)this.b;
	}
	
	@Override
	public byte byteValue(){
		return this.b;
	}
	@Override
	public short shortValue(){
		return (short)this.b;
	}

	@Override
	public void setInt(int i) {
		this.b = JavaUtil.castByte(i);
	}

	@Override
	public void setLong(long l) {
		this.b = JavaUtil.castByte(l);
	}

	@Override
	public void setByte(byte b) {
		this.b = b;
	}

	@Override
	public void setShort(short s) {
		this.b = JavaUtil.castByte(s);
	}

	@Override
	public void setFloat(float f) {
		this.b = JavaUtil.castByte(f);
	}

	@Override
	public void setDouble(double d) {
		this.b = JavaUtil.castByte(d);
	}
	/**
	 * even if it gets truncated the value wouldn't be equal since the value will have different signs so no extra work is needed besides default
	 */
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Number))
			return false;
		Number num = (Number)obj;
		return this.byteValue() == num.byteValue();
	}
	@Override 
	public String toString(){
		return "" + this.byteValue();
	}

}
