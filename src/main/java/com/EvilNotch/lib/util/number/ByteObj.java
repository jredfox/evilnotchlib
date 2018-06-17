package com.EvilNotch.lib.util.number;

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
		this.b = (byte)i;
	}

	@Override
	public void setLong(long l) {
		this.b = (byte)l;
	}

	@Override
	public void setByte(byte b) {
		this.b = b;
	}

	@Override
	public void setShort(short s) {
		this.b = (byte)s;
	}

	@Override
	public void setFloat(float f) {
		this.b = (byte)f;
	}

	@Override
	public void setDouble(double d) {
		this.b = (byte)d;
	}
	
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
