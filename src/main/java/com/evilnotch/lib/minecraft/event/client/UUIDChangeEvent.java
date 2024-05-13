package com.evilnotch.lib.minecraft.event.client;

import java.util.UUID;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class UUIDChangeEvent extends Event {
	
	public final UUID org;
	public final UUID uuid;
	public final EntityPlayerSP player;
	
	public UUIDChangeEvent(UUID org, UUID uuid, EntityPlayerSP player)
	{
		this.org = org;
		this.uuid = uuid;
		this.player = player;
	}

}
