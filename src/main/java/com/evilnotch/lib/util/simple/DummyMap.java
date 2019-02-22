package com.evilnotch.lib.util.simple;

import java.util.HashMap;
import java.util.Map;

public class DummyMap<K,V> extends HashMap<K,V>{
	
	@Override
	public V put(K key, V value)
	{
		return value;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> map)
	{
		
	}
	
	@Override
	public V putIfAbsent(K k , V v)
	{
		return v;
	}

}
