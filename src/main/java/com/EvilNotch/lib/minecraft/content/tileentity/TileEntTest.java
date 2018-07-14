package com.EvilNotch.lib.minecraft.content.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntTest extends TileEntityFurnace {
	
    private int fish;
    
    @Override
	public void readFromNBT(NBTTagCompound compound)
    {
    	super.readFromNBT(compound);
    	this.fish = compound.getInteger("fish");
    	System.out.println("Reading fish:" + this.fish + " valid:" + this.tileEntityInvalid);
    }
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setInteger("fish", this.fish);
		System.out.println("writing fish:" + this.fish);
		return nbt;
	}
	
}
