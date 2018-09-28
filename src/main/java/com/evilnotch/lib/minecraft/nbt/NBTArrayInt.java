package com.evilnotch.lib.minecraft.nbt;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.api.MCPSidedString;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.nbt.NBTTagIntArray;

public class NBTArrayInt extends NBTTagIntArray implements INBTWrapperArray{
	
	public List<Integer> values = new ArrayList<>();

	public NBTArrayInt() 
	{
		super(new int[0]);
	}
	
	public void addValue(int b)
	{
		this.values.add(b);
	}
	public void setValue(int index,int b)
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
		String name = new MCPSidedString("intArray","field_74749_a").toString();
		ReflectionUtil.setObject(this, JavaUtil.arrayToStaticInts(this.values), NBTTagIntArray.class, name);
	}

}
