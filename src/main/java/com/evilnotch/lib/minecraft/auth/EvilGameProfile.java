package com.evilnotch.lib.minecraft.auth;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NBTTagCompound;

public class EvilGameProfile extends GameProfile {
	
	/**
	 * The Original UUID before it get's Patched by the Server
	 */
	public UUID org;
	/**
	 * The NBTTagCompound of the player during login it's cached so it doesn't get parsed multiple times
	 */
	public NBTTagCompound login;

	public EvilGameProfile(UUID id, String name) 
	{
		super(id, name);
		this.org = id;
	}

	public EvilGameProfile(UUID id, GameProfile old) 
	{
		super(id, old.getName());
		this.getProperties().putAll(old.getProperties());
		this.org = old instanceof EvilGameProfile ? ((EvilGameProfile)old).org : old.getId();
	}
}
