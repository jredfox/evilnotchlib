package com.EvilNotch.lib.util.minecraft;

import java.math.BigDecimal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

public class SpawnerUtil {
	
	public static void setOffsets(NBTTagCompound nbt, int x, int y, int z)
    {
//    	System.out.println(nbt);
    	NBTTagCompound tag = nbt.getCompoundTag("SpawnData");
    	setOffset(tag,x,y,z);
//    	System.out.println(nbt);
    	NBTTagList list = nbt.getTagList("SpawnPotentials", 10);
    	for(int i=0;i<list.tagCount();i++)
    	{
    		NBTTagCompound compound = list.getCompoundTagAt(i).getCompoundTag("Entity");
    		setOffset(compound,x,y,z);
    	}
//    	System.out.println(nbt);
    }
    public static void setOffset(NBTTagCompound nbt,int x,int y,int z)
    {
    	if(!nbt.hasKey("Pos"))
    		return;
    	NBTTagList list = nbt.getTagList("Pos", 6);
    	NBTTagList offsets = new NBTTagList();
    	offsets.appendTag(new NBTTagDouble(getOffset(list.getDoubleAt(0),x,x)));
    	offsets.appendTag(new NBTTagDouble(getOffset(list.getDoubleAt(1),y,y)));
    	offsets.appendTag(new NBTTagDouble(getOffset(list.getDoubleAt(2),z,z)));
    	nbt.setTag("offsets", offsets);
    	nbt.removeTag("Pos");//makes spawners stack
    }
    public static void reAlignSpawnerPos(NBTTagCompound nbt,int x,int y,int z)
    {
    	NBTTagCompound tag = nbt.getCompoundTag("SpawnData");
    	alignPos(tag,x,y,z);
    	
    	//Does SpawnPotentials
    	NBTTagList list = nbt.getTagList("SpawnPotentials", 10);
    	for(int i=0;i<list.tagCount();i++)
    	{
    		NBTTagCompound compound = list.getCompoundTagAt(i);
    		alignPos(compound.getCompoundTag("Entity"),x,y,z);
    	}
    }
    public static double getOffset(double p, int ox, int nx)
	{
    	BigDecimal pos = new BigDecimal("" + p);
		BigDecimal oldx = new BigDecimal("" + ox);
		BigDecimal newx = new BigDecimal("" + nx);
		BigDecimal offset = oldx.subtract(pos).multiply(new BigDecimal("-1") );
		return offset.doubleValue();
	}
    /**
     * Does not require initial position since the offsets are pre-calculated now because of bigdeci
     */
    public static double recalDouble(int nx, double ofset)
	{
		BigDecimal newx = new BigDecimal("" + nx);
		BigDecimal offset = new BigDecimal("" + ofset);
		return newx.add(offset).doubleValue();
	}
    /**
     * Create pos tags from the offsets
     */
    public static void alignPos(NBTTagCompound tag, int x, int y, int z) 
    {
	    if (!tag.hasKey("offsets"))
	    	return;
	     NBTTagList list = tag.getTagList("offsets", 6);
	     NBTTagList pos = new NBTTagList();
	     int[] li = {x,y,z};
	     for (int i=0;i<3;i++)
	     {
	    	 double offset = list.getDoubleAt(i);
	    	 double new_pos = recalDouble(li[i], offset);
    		 pos.appendTag(new NBTTagDouble(new_pos));
	     }
	    tag.removeTag("offsets");//just in case tile entity has offsets array/tag doesn't effect stack since I modify nbt after I copy it
	    tag.setTag("Pos", pos);
	}
    
	public static boolean isCustomSpawnerPos(NBTTagCompound nbt,String pos)
	{
		if (nbt == null || !(nbt.hasKey("SpawnData") ) && !(nbt.hasKey("SpawnPotentials")) )
			return false;
		if (nbt.getTag("SpawnData") != null)
		{
			NBTTagCompound tag = (NBTTagCompound)nbt.getTag("SpawnData");
			if (tag.hasKey(pos))
				return true;
		}
		if (nbt.getTag("SpawnPotentials") != null)
		{
			NBTTagList list = nbt.getTagList("SpawnPotentials",10);
			if (list.tagCount() > 0)
			{
				for (int i=0;i<list.tagCount();i++)
				{
					NBTTagCompound tag = list.getCompoundTagAt(i);
					NBTTagCompound ent = tag.getCompoundTag("Entity");
					if (ent.hasKey(pos))
						return true;
				}
			}
		}
		return false;
	}
	
	public static NBTTagCompound getJockieNBT(NBTTagCompound nbt) {
		nbt = nbt.copy();
		if(!nbt.hasKey("Passengers"))
			return null;
		NBTTagList list = nbt.getTagList("Passengers", 10);
		NBTTagList actualList = list;
		NBTTagCompound compound = nbt;
		while(list.tagCount() > 0)
		{
			list = compound.getTagList("Passengers", 10);
			if(list.tagCount() > 0)
				actualList = list;
			compound = list.getCompoundTagAt(0);
			if(compound == null)
				break;
		}
		return actualList.getCompoundTagAt(0);
	}

	public static boolean multiIndexSpawner(NBTTagCompound nbt) {
		nbt = nbt.copy();
		if(nbt == null || !nbt.hasKey("SpawnPotentials") || nbt.getTagList("SpawnPotentials", 10).tagCount() == 0)
			return false;
		if(nbt.getTagList("SpawnPotentials", 10).tagCount() == 1)
		{
			NBTTagCompound data = nbt.getCompoundTag("SpawnData");
			NBTTagCompound compare = nbt.getTagList("SpawnPotentials", 10).getCompoundTagAt(0);
			return !data.equals(compare.getCompoundTag("Entity"));
		}
		return true;
	}
	
	 /**
     * Checks for a soft coded non laggy way of detecting if a dropped spawner has custom pos
     */
    public static boolean isStackCurrentCustomPos(NBTTagCompound nbt) {
		return nbt.getCompoundTag("SpawnData").hasKey("offsets");
	}

}
