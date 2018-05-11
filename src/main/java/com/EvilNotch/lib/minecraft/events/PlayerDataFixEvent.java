package com.EvilNotch.lib.minecraft.events;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerDataFixEvent extends Event{
	
	public EntityPlayerMP player;
	public String uuidOld;
	public String uuidNew;
	
	public PlayerDataFixEvent(EntityPlayerMP player,String uuidOld,String uuidNew)
	{
		this.player = player;
		this.uuidOld = uuidOld;
		this.uuidNew = uuidNew;
	}

}
