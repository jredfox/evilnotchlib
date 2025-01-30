package jredfox.clfix;

import java.util.Collection;
import java.util.HashSet;

public class DummySet<T> extends HashSet<T> {
	
	public DummySet()
	{
		super(0);
	}
	
	public DummySet(Collection<? extends T> c)
	{
		 super(0);
	}
	
    public DummySet(int initialCapacity, float loadFactor)
    {
        super(0);
    }

    public DummySet(int initialCapacity)
    {
        super(0);
    }
	
	@Override
	public int size()
	{
		return 0;
	}
	
	@Override
	public boolean isEmpty()
	{
		return true;
	}
	
	@Override
	public boolean contains(Object o)
	{
		return false;
	}
	
	@Override
	public boolean add(T e) 
	{
		 return false;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) 
	{
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return c.isEmpty();
	}
	
	@Override
	public boolean remove(Object o)
	{
		return false;
	}

}
