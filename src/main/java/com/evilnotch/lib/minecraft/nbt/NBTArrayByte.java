package com.evilnotch.lib.minecraft.nbt;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.api.MCPSidedString;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.nbt.NBTTagByteArray;

public class NBTArrayByte extends NBTTagByteArray implements INBTWrapperArray{
	
	public List<Byte> bytes = new ArrayList<>();

	public NBTArrayByte() 
	{
		super(new byte[0]);
	}
	
	public void addValue(byte b)
	{
		this.bytes.add(b);
	}
	/**
	 * fill in blanks as well as set the index
	 */
	public void setValue(int index,byte b)
	{
		if(index >= this.bytes.size())
		{
			for(int i=this.bytes.size();i<=index;i++)
				this.bytes.add(b);
		}
		this.bytes.set(index, b);
	}
	@Override
	public void toVanilla()
	{
		String name = new MCPSidedString("data","field_74754_a").toString();
		ReflectionUtil.setObject(this, JavaUtil.arrayToStaticBytes(this.bytes), NBTTagByteArray.class, name);
	}

}
