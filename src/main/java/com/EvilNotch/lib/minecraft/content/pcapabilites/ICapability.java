package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;

import com.EvilNotch.lib.util.ICopy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

public interface ICapability extends ICopy{
	
	public void readFromNBT(NBTTagCompound nbt);
	public void writeToNBT(NBTTagCompound nbt);
}
