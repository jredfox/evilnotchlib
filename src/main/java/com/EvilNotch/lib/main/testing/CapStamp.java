package com.evilnotch.lib.main.testing;

import com.evilnotch.lib.minecraft.content.capabilites.ICapability;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

public class CapStamp implements ICapability<Chunk> {

	private long timeStamp;

	@Override
	public void writeToNBT(Chunk object, NBTTagCompound nbt, CapContainer c) {
		nbt.setLong("timeStamp", System.currentTimeMillis());
	}

	@Override
	public void readFromNBT(Chunk object, NBTTagCompound nbt, CapContainer c) {
		this.timeStamp = nbt.getLong("timeStamp");
	}

}
