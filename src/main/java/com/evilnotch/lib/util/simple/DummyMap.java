package com.evilnotch.lib.util.simple;

import java.util.HashMap;
import java.util.Map;

public class DummyMap<K,V> extends HashMap<K,V>{
	
	public DummyMap()
	{
		super(0);
	}
	
	@Override
	public V put(K key, V value)
	{
		return null;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> map)
	{
		
	}
	
	@Override
	public V putIfAbsent(K k , V v)
	{
		return null;
	}
	
	@Override
	public V get(Object k)
	{
		return null;
	}
	
	@Override
	public boolean containsKey(Object obj)
	{
		return false;
	}
	
	@Override
	public boolean containsValue(Object obj)
	{
		return false;
	}

}
