package com.EvilNotch.lib.minecraft.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.SkinData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SkinFixEvent extends PlayerEvent{
	
	public String newSkin = null;

	public SkinFixEvent(EntityPlayer player) {
		super(player);
	}

}
