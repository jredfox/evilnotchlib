package com.evilnotch.lib.minecraft.event.client;

import java.util.UUID;

import com.evilnotch.lib.minecraft.proxy.ClientProxy;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class UUIDChangeEvent extends Event {
	
	/**
	 * previous UUID
	 */
	public final UUID org;
	/**
	 * new UUID
	 */
	public final UUID uuid;
	/**
	 * the UUID from the launch of the game
	 */
	public final UUID launch = ClientProxy.org.getId();
	/**
	 * may be null
	 */
	public final EntityPlayerSP player;
	
	public UUIDChangeEvent(UUID org, UUID uuid, EntityPlayerSP player)
	{
		this.org = org;
		this.uuid = uuid;
		this.player = player;
	}

}
