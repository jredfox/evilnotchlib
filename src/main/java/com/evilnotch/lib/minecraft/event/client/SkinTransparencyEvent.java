package com.evilnotch.lib.minecraft.event.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SkinTransparencyEvent extends Event {
	
	public MinecraftProfileTexture.Type type;
	public MinecraftProfileTexture texture;
	public int[] imageData;
	public boolean allowTrans;
	
	public SkinTransparencyEvent(MinecraftProfileTexture.Type type, MinecraftProfileTexture texture, int[] imageData)
	{
		this.type = type;
		this.texture = texture;
		this.imageData = imageData;
		this.allowTrans = false;
	}

	public static boolean allow(ImageBufferDownload img)
	{
		SkinTransparencyEvent event = new SkinTransparencyEvent(img.evlType, img.evlTexture, img.imageData);
		MinecraftForge.EVENT_BUS.post(event);
		return event.allowTrans;
	}

}
