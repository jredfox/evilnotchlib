package com.EvilNotch.lib.util.number;

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
		return (byte)this.s;
	}
	@Override
	public short shortValue(){
		return this.s;
	}

	@Override
	public void setInt(int i) {
		this.s = (short)i;
	}

	@Override
	public void setLong(long l) {
		this.s = (short)l;
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
		this.s = (short)f;
	}

	@Override
	public void setDouble(double d) {
		this.s = (short)d;
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
