package com.evilnotch.lib.util.primitive;

import com.evilnotch.lib.util.JavaUtil;

public class ShortObj extends Number implements IModNumber{
	
	public short s;
	
	public ShortObj(short s){
		this.s = s;
	}

	@Override
	public double doubleValue() {
		return (double)this.s;
	}

	@Override
	public float floatValue() {
		return (float)this.s;
	}

	@Override
	public int intValue() {
		return (int)this.s;
	}

	@Override
	public long longValue() {
		return (long)this.s;
	}
	
	@Override
	public byte byteValue(){
		return JavaUtil.castByte(this.s);
	}
	@Override
	public short shortValue(){
		return this.s;
	}

	@Override
	public void setInt(int i) {
		this.s = JavaUtil.castShort(i);
	}

	@Override
	public void setLong(long l) {
		this.s = JavaUtil.castShort(l);
	}

	@Override
	public void setByte(byte b) {
		this.s = (short)b;
	}

	@Override
	public void setShort(short s) {
		this.s = s;
	}

	@Override
	public void setFloat(float f) {
		this.s = JavaUtil.castShort(f);
	}

	@Override
	public void setDouble(double d) {
		this.s = JavaUtil.castShort(d);
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Number))
			return false;
		Number num = (Number)obj;
		return this.shortValue() == num.shortValue();
	}
	@Override 
	public String toString(){
		return "" + this.shortValue();
	}

}
