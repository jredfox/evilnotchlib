package com.EvilNotch.lib.minecraft.content.client.rp;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class Font extends FontRenderer{

	public Font(GameSettings g, ResourceLocation r, TextureManager t, boolean b) 
	{
		super(g, r, t, b);//Custom Font technically
	}

}
