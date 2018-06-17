package com.EvilNotch.lib.util.number;

public class DoubleObj extends Number implements IModNumber{
	
	public double d;
	
	public DoubleObj(double d){
		this.d = d;
	}

	@Override
	public double doubleValue() {
		return this.d;
	}

	@Override
	public float floatValue() {
		return (float)this.d;
	}

	@Override
	public int intValue() {
		return (int)this.d;
	}

	@Override
	public long longValue() {
		return (long)this.d;
	}
	
	@Override
	public byte byteValue(){
		return (byte)this.d;
	}
	@Override
	public short shortValue(){
		return (short)this.d;
	}

	@Override
	public void setInt(int i) {
		this.d = (double)i;
	}

	@Override
	public void setLong(long l) {
		this.d = (double)l;
	}

	@Override
	public void setByte(byte b) {
		this.d = (double)b;
	}

	@Override
	public void setShort(short s) {
		this.d = (double)s;
	}

	@Override
	public void setFloat(float f) {
		this.d = (double)f;
	}

	@Override
	public void setDouble(double d) {
		this.d = d;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Number))
			return false;
		Number num = (Number)obj;
		return this.doubleValue() == num.doubleValue();
	}
	@Override 
	public String toString(){
		return "" + this.doubleValue();
	}

}
