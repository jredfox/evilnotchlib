package com.evilnotch.lib.util.simple;

public class PairObj<K,V> {
	
	public K obj1;
	public V obj2;
	
	public PairObj(K obj1,V obj2)
	{
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	public K getKey(){
		return this.obj1;
	}
	public V getValue(){
		return this.obj2;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof PairObj))
			return false;
		PairObj compare = (PairObj)obj;
		return this.obj1.equals(compare.obj1) && this.obj2.equals(compare.obj2);
	}
}
