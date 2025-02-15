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
	 * The TEMP NBTTagCompound of the player during login it's cached so it doesn't get parsed multiple times
	 */
	public NBTTagCompound login;
	/**
	 * The TEMP NBTTagCompound of login hooks let's the client give the server data during login packet
	 */
	public NBTTagCompound iloginhooks;

	public EvilGameProfile(UUID id, String name) 
	{
		super(id, name);
		this.org = id;
	}

	public EvilGameProfile(UUID id, GameProfile old) 
	{
		super(id, old.getName());
		this.getProperties().putAll(old.getProperties());
		if(old instanceof EvilGameProfile)
		{
			EvilGameProfile ep = (EvilGameProfile) old;
			this.org =         ep.org;
			this.login =  	   ep.login;
			this.iloginhooks = ep.iloginhooks;
		}
		else
			this.org = old.getId();
	}
}
