package com.evilnotch.lib.minecraft.entity;

import java.util.HashSet;
import java.util.Set;

public class EntityDefintions
{
public static final Set<EntityInfo> infos = new HashSet<>();
public static final Set<EntityType> types = new HashSet<>();

public static class EntityInfo
{
	//living are for mods that don't extend anything besides entity but, are living since they wrote their own system
	public static final EntityInfo living = new EntityInfo("living");
	public static final EntityInfo entitybase = new EntityInfo("entitybase");
	public static final EntityInfo nonliving = new EntityInfo("nonliving");
	public static final EntityInfo item = new EntityInfo("item");
	public static final EntityInfo projectile = new EntityInfo("projectile");
	public static final EntityInfo multiPart = new EntityInfo("multipart");
	public static final EntityInfo hostile = new EntityInfo("hostile");
	public static final EntityInfo peacefull = new EntityInfo("peacefull");
	public static final EntityInfo passive = new EntityInfo("passive");

	public final String info;
	public EntityInfo(String s)
	{
		this.info = s;
		infos.add(this);
	}
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof EntityInfo))
			return false;
		return this.info.equals(((EntityInfo)obj).info);
	}
	
	@Override
	public int hashCode()
	{
		return this.info.hashCode();
	}
}

public static class EntityType
{
	public static final EntityType fire = new EntityType("fire");
	public static final EntityType water = new EntityType("water");
	public static final EntityType flying = new EntityType("flying");
	public static final EntityType monster = new EntityType("monster");
	public static final EntityType boss = new EntityType("boss");
	public static final EntityType creature = new EntityType("creature");
	public static final EntityType ambient = new EntityType("ambient");
	public static final EntityType areaeffectcloud = new EntityType("areaeffectcloud");
	public static final EntityType tameable = new EntityType("tameable");
	public static final EntityType ranged = new EntityType("ranged");
	public static final EntityType ender = new EntityType("ender");
	public static final EntityType npc = new EntityType("npc");
	
	public final String type;
	public EntityType(String s)
	{
		this.type = s;
		types.add(this);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof EntityType))
			return false;
		return this.type.equals(((EntityType)obj).type);
	}
	
	@Override
	public int hashCode()
	{
		return this.type.hashCode();
	}
}

	public static EntityInfo getInfo(String s)
	{
		for(EntityInfo i : infos)
			if(i.info.equals(s))
				return i;
		return null;
	}
	
	public static EntityType getType(String s)
	{
		for(EntityType t : types)
			if(t.type.equals(s))
				return t;
		return null;
	}
}