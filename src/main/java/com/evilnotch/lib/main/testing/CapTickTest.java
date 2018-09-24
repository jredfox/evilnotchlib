package com.evilnotch.lib.main.testing;

import com.evilnotch.lib.minecraft.content.capability.ICapTick;
import com.evilnotch.lib.minecraft.content.capability.ICapability;
import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;

public class CapTickTest implements ICapability<Object>,ICapTick<Object> {

	@Override
	public void writeToNBT(Object object, NBTTagCompound nbt, CapContainer c) {}

	@Override
	public void readFromNBT(Object object, NBTTagCompound nbt, CapContainer c) {}

	@Override
	public void tick(Object object, CapContainer c) 
	{
		if(object instanceof TileEntity)
		{
			TileEntity tile = (TileEntity)object;
//			System.out.println(tile.getWorld().isRemote);
		}
		else if(object instanceof Entity)
		{
			Entity p = (Entity)object;
//			System.out.println(p.world.isRemote);
		}
		else if(object instanceof WorldInfo)
		{
//			System.out.println("tick world info");
		}
		else if(object instanceof World)
		{
			World w = (World)object;
//			System.out.println(w.isRemote);
		}
		else if(object instanceof Chunk)
		{
			
		}
	}
}
