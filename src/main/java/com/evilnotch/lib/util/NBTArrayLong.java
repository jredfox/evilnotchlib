package com.evilnotch.lib.util;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.api.MCPSidedString;
import com.evilnotch.lib.api.ReflectionUtil;

import net.minecraft.nbt.NBTTagLongArray;

public class NBTArrayLong extends NBTTagLongArray implements INBTWrapperArray{

	public List<Long> values = new ArrayList<>();
	
	public NBTArrayLong() 
	{
		super(new long[0]);
	}
	
	public void addValue(long b)
	{
		this.values.add(b);
	}
	public void setValue(int index,long b)
	{
		if(index >= this.values.size())
		{
			for(int i=this.values.size();i<=index;i++)
				this.values.add(b);
		}
		this.values.set(index, b);
	}
	@Override
	public void toVanilla()
	{
		String name = new MCPSidedString("data","field_193587_b").toString();
		ReflectionUtil.setObject(this, arrayToStaticLong(this.values), NBTTagLongArray.class, name);
	}

	public long[] arrayToStaticLong(List<Long> li) {
		long[] list = new long[li.size()];
		for(int i=0;i<li.size();i++)
			list[i] = li.get(i);
		return list;
	}

}
