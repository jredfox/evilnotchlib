package com.EvilNotch.lib.util.number;

@SuppressWarnings("serial")
public class LongObj extends Number implements IModNumber{
	
	public long l;
	
	public LongObj(long l){
		this.l = l;
	}

	@Override
	public double doubleValue() {
		return (double)this.l;
	}

	@Override
	public float floatValue() {
		return (float)this.l;
	}

	@Override
	public int intValue() {
		return (int)this.l;
	}

	@Override
	public long longValue() {
		return this.l;
	}
	@Override
	public byte byteValue(){
		return (byte)this.l;
	}
	@Override
	public short shortValue(){
		return (short)this.l;
	}

	@Override
	public void setInt(int i) {
		this.l = (long)i;
	}

	@Override
	public void setLong(long l) {
		this.l = l;
	}

	@Override
	public void setByte(byte b) {
		this.l = (long)b;
	}

	@Override
	public void setShort(short s) {
		this.l = (long)s;
	}

	@Override
	public void setFloat(float f) {
		this.l = (long)f;
	}

	@Override
	public void setDouble(double d) {
		this.l = (long)d;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Number))
			return false;
		Number num = (Number)obj;
		return this.longValue() == num.longValue();
	}
	@Override 
	public String toString(){
		return "" + this.longValue();
	}

}
