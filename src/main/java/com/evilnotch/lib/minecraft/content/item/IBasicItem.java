package com.evilnotch.lib.minecraft.content.item;

import net.minecraft.util.ResourceLocation;

public interface IBasicItem {

	public boolean register();
	public boolean registerModel();
	public boolean useLangRegistry();
	public boolean useConfigPropterties();
	public ResourceLocation getRegistryName();
	
	default public int getMaxMeta(){
		return 0;
	}
	
	default public boolean isMeta(){
		return this.getMaxMeta() > 0;
	}
	
	default public String getTextureName(){
		return getRegistryName().getResourcePath();
	}

}
