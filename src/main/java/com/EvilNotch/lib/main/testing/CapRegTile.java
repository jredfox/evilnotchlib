package com.EvilNotch.lib.main.testing;

import com.EvilNotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapRegChunk;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapRegTileEntity;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapRegistry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;

public class CapRegTile implements ICapRegistry<TileEntityMobSpawner>{
	
	public static final ResourceLocation tile = new ResourceLocation("evilnotchlib:has_scanned");
	@Override
	public void register(TileEntityMobSpawner object, CapContainer c) {
		c.registerCapability(tile, new CapBoolean<TileEntity>("hasScanned"));
		c.registerCapability(new ResourceLocation("a:a"), new CapTickTest());
	}

	@Override
	public Class getObjectClass() {
		return TileEntityMobSpawner.class;
	}

}
