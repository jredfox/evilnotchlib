package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.minecraft.event.client.ClientDisconnectEvent;
import com.evilnotch.lib.minecraft.tick.ITick;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TickEventClient implements ITick{

	public static boolean notNull = false;
	
	@Override
	public void tick() 
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(notNull && mc.world == null)
		{
			MinecraftForge.EVENT_BUS.post(new ClientDisconnectEvent());
		}
		notNull = mc.world != null;
	}

	/**
	 * unused
	 */
	@Override
	public void garbageCollect() {}

	@Override
	public Phase getPhase() 
	{
		return Phase.END;
	}

}
