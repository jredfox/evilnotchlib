package com.EvilNotch.lib.minecraft.content.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IPlayerFileData;

public class FakePlayerFileData implements IPlayerFileData{

	@Override
	public void writePlayerData(EntityPlayer player) {
	}
	/**
	 * null is allowed here
	 */
	@Override
	public NBTTagCompound readPlayerData(EntityPlayer player) {
		return null;
	}

	/**
	 * vanilla allows null strings in the array but, not the array itself
	 */
	@Override
	public String[] getAvailablePlayerDat() {
		return new String[0];
	}

}
