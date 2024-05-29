package com.evilnotch.lib.main.skin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

public interface IStopSteve extends SkinManager.SkinAvailableCallback {
	
	public void skinUnAvailable(Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture);

}
