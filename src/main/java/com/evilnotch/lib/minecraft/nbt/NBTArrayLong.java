package com.evilnotch.lib.minecraft.nbt;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.util.JavaUtil;

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
		this.data = JavaUtil.arrayToStaticLong(this.values);
	}
}
