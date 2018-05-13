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
	
	/**
	 * if fix type is UUIDFIX is patching vanilla bug from old file to new file
	 * if fix type is UUIDLEVELDAT is a patch for when players swap worlds but, you should still be syncing your external patches over as well regardless event only fires on SP
	 */
	public PlayerDataFixEvent(EntityPlayerMP player,String uuidOld,String uuidNew, Types type)
	{
		this.player = player;
		this.uuidOld = uuidOld;
		this.uuidNew = uuidNew;
		this.type = type;
	}

}
