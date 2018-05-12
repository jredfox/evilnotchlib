package com.EvilNotch.lib.minecraft.events;

import com.EvilNotch.lib.main.eventhandlers.UUIDFixer;
import com.EvilNotch.lib.main.eventhandlers.UUIDFixer.Types;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerDataFixEvent extends Event{
	
	public EntityPlayerMP player;
	public String uuidOld;
	public String uuidNew;
	public UUIDFixer.Types type;
	
	public PlayerDataFixEvent(EntityPlayerMP player,String uuidOld,String uuidNew, Types type)
	{
		this.player = player;
		this.uuidOld = uuidOld;
		this.uuidNew = uuidNew;
		this.type = type;
	}

}
