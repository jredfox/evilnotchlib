package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.Block;

public interface IBasicBlockJSON<T extends Block> extends IBasicItemJSON<T>{
	
	public ModelPart getModelPart();
	
}
