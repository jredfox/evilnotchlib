package com.evilnotch.lib.minecraft.auth;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.client.Minecraft;

public class EvilGameProfile extends GameProfile {
	
	public UUID org;

	public EvilGameProfile(UUID id, String name) 
	{
		super(id, name);
		this.org = id;
	}

	public EvilGameProfile(UUID id, GameProfile old) 
	{
		super(id, old.getName());
		this.getProperties().putAll(old.getProperties());
		this.org = old instanceof EvilGameProfile ? ((EvilGameProfile)old).org : id;
	}
}
