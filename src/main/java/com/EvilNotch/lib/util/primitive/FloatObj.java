package com.evilnotch.lib.util.primitive;

import com.evilnotch.lib.util.JavaUtil;

public class FloatObj extends Number implements IModNumber{
	
	public float f;
	public FloatObj(float f){
		this.f = f;
	}

	@Override
	public double doubleValue() {
		return (double)this.f;
	}

	@Override
	public float floatValue() {
		return this.f;
	}

	@Override
	public int intValue() {
		return JavaUtil.castInt(this.f);
	}

	@Override
	public long longValue() {
		return JavaUtil.castLong(this.f);
	}
	
	@Override
	public byte byteValue(){
		return JavaUtil.castByte(this.f);
	}
	@Override
	public short shortValue()
	{
		return JavaUtil.castByte(this.f);
	}

	@Override
	public void setInt(int i) {
		this.f = (float)i;
	}

	@Override
	public void setLong(long l) {
		this.f = (float)l;
	}

	@Override
	public void setByte(byte b) {
		this.f = (float)b;
	}

	@Override
	public void setShort(short s) {
		this.f = (float)s;
	}

	@Override
	public void setFloat(float f) {
		this.f = f;
	}

	@Override
	public void setDouble(double d) {
		this.f = (float)d;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Number))
			return false;
		Number num = (Number)obj;
		return this.floatValue() == num.floatValue();
	}
	@Override 
	public String toString(){
		return "" + this.floatValue();
	}

}
