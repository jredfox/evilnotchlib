package com.evilnotch.menulib.test;

import com.evilnotch.lib.minecraft.content.client.gui.BasicPanoramicGui;

import net.minecraft.util.ResourceLocation;

public class MainMenuAetherTest extends BasicPanoramicGui{
	
	public MainMenuAetherTest()
	{
		super(new ResourceLocation[]{
				new ResourceLocation("menulib","aetherii/panorama/panorama_0.png"),
				new ResourceLocation("menulib","aetherii/panorama/panorama_1.png"),
				new ResourceLocation("menulib","aetherii/panorama/panorama_2.png"),
				new ResourceLocation("menulib","aetherii/panorama/panorama_3.png"),
				new ResourceLocation("menulib","aetherii/panorama/panorama_4.png"),
				new ResourceLocation("menulib","aetherii/panorama/panorama_5.png")
				});
	}

}
