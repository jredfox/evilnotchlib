package com.EvilNotch.lib.main.testing;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

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
