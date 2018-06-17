package com.EvilNotch.lib.util.number;

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
		return (byte)this.integer;
	}
	@Override
	public short shortValue(){
		return (short)this.integer;
	}

	@Override
	public void setInt(int i) {
		this.integer = i;
	}

	@Override
	public void setLong(long l) {
		this.integer = (int)l;
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
		this.integer = (int)f;
	}

	@Override
	public void setDouble(double d) {
		this.integer = (int)d;
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
