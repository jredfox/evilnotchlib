package com.evilnotch.lib.main.testing;

import com.evilnotch.lib.minecraft.content.capability.ICapability;
import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;

public class CapWorldSeed implements ICapability<WorldInfo>{

	private String season = "spring";

	@Override
	public void writeToNBT(WorldInfo object, NBTTagCompound nbt, CapContainer c) {
		nbt.setString("season", this.season);
	}

	@Override
	public void readFromNBT(WorldInfo object, NBTTagCompound nbt, CapContainer c) {
		if(nbt.hasKey("season"))
			this.season = nbt.getString("season");
	}

}
