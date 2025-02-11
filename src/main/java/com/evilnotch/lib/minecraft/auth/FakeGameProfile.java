package com.evilnotch.lib.minecraft.auth;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

public class FakeGameProfile extends GameProfile {

	public FakeGameProfile(UUID id, String name) {
		super(id, name);
	}

}
