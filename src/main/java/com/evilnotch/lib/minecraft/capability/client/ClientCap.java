package com.evilnotch.lib.minecraft.capability.client;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class ClientCap<T> implements IClientCap<T> {
	
	public ResourceLocation id;
	public T value;
	public final Type type;
	
	public ClientCap(ResourceLocation loc, T o)
	{
		this.id = loc;
		this.value = o;
		if(value instanceof Boolean)
			type = Type.BOOLEAN;
		else if(value instanceof String)
			type = Type.STRING;
		else if(value instanceof Long)
			type = Type.LONG;
		else if(value instanceof Integer)
			type = Type.INT;
		else if(value instanceof Short)
			type = Type.SHORT;
		else if(value instanceof Double)
			type = Type.DOUBLE;
		else if(value instanceof Float)
			type = Type.FLOAT;
		else
			type = null;
	}
	
	public ClientCap(ResourceLocation loc, NBTBase base)
	{
		this.id = loc;
		if(base instanceof NBTTagString)
		{
			this.value = (T) base.toString();
			this.type = Type.STRING;
		}
		else if(base instanceof NBTTagByte)
		{
			Boolean b = ((NBTPrimitive) base).getByte() != 0;
			this.value = (T) b;
			this.type = Type.BOOLEAN;
		}
		else if(base instanceof NBTTagDouble)
		{
			this.value = (T) (Double) ((NBTPrimitive) base).getDouble();
			this.type = Type.DOUBLE;
		}
		else if(base instanceof NBTTagFloat)
		{
			this.value = (T) (Float) ((NBTPrimitive) base).getFloat();
			this.type = Type.FLOAT;
		}
		else if(base instanceof NBTTagLong)
		{
			this.value = (T) (Long) ((NBTPrimitive) base).getLong();
			this.type = Type.LONG;
		}
		else if(base instanceof NBTTagInt)
		{
			this.value = (T) (Integer) ((NBTPrimitive) base).getInt();
			this.type = Type.INT;
		} 
		else if(base instanceof NBTTagShort)
		{
			this.value = (T) (Short) ((NBTPrimitive) base).getShort();
			this.type = Type.SHORT;
		} 
		else
			this.type = null;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public T get() {
		return this.value;
	}

	@Override
	public void set(T o) {
		this.value = o;
	}

	@Override
	public IClientCap.Type type() {
		return this.type;
	}

	@Override
	public void write(NBTTagCompound nbt) 
	{
		String id = ClientCapHooks.convertID(this.getId());
		switch(this.type)
		{
			case STRING:
				nbt.setString(id, (String) this.get() );
			break;
			
			case BOOLEAN:
				nbt.setBoolean(id, (Boolean) this.get());
			break;
			
			case DOUBLE:
				nbt.setDouble(id, (Double) this.get());
			break;
			
			case FLOAT:
				nbt.setFloat(id, (Float) this.get());
			break;
			
			case INT:
				nbt.setInteger(id, (Integer) this.get());
			break;
				
			case LONG:
				nbt.setLong(id, (Long) this.get());
			break;
			
			case SHORT:
				nbt.setShort(id, (Short) this.get());
			break;
		}
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		String id = ClientCapHooks.convertID(this.getId());
		switch(this.type)
		{
			case STRING:
				this.set((T) nbt.getString(id));
			break;
			
			case BOOLEAN:
				this.set((T) (Boolean) nbt.getBoolean(id));
			break;
			
			case DOUBLE:
				this.set((T) (Double) nbt.getDouble(id));
			break;
			
			case FLOAT:
				this.set((T) (Float) nbt.getFloat(id));
			break;
			
			case INT:
				this.set((T) (Integer) nbt.getInteger(id));
			break;
				
			case LONG:
				this.set((T) (Long) nbt.getLong(id));
			break;
			
			case SHORT:
				this.set((T) (Short) nbt.getShort(id));
			break;
		}
	}

	@Override
	public IClientCap<T> clone(NBTTagCompound nbt) 
	{
		String idKey = ClientCapHooks.convertID(this.getId());
		switch(this.type)
		{
			case STRING:
				return new ClientCap(this.id, nbt.getString(idKey));
			
			case BOOLEAN:
				return new ClientCap(this.id, nbt.getBoolean(idKey));
			
			case DOUBLE:
				return new ClientCap(this.id, nbt.getDouble(idKey));
			
			case FLOAT:
				return new ClientCap(this.id, nbt.getFloat(idKey));
			
			case INT:
				return new ClientCap(this.id, nbt.getInteger(idKey));
				
			case LONG:
				return new ClientCap(this.id, nbt.getLong(idKey));
			
			case SHORT:
				return new ClientCap(this.id, nbt.getShort(idKey));
		}
		return null;
	}

}
