package com.evilnotch.lib.minecraft.content.pcapability;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CapabilityHandler {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void capabilityReader(PlayerEvent.LoadFromFile e)
	{
		if(CapabilityReg.reg.size() == 0)
			return;
		EntityPlayerMP p = (EntityPlayerMP) e.getEntityPlayer();
		CapabilityReg.registerEntity(p);
		CapabilityReg.readFromFile(p);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void capabilityRemove(PlayerLoggedOutEvent e)
	{
		if(CapabilityReg.reg.size() == 0 || !(e.player instanceof EntityPlayerMP))
			return;
		CapabilityReg.saveToFile(e.player);
		CapabilityReg.removeCapailityContainer(e.player);
	}
	/**
	 * used for capabilities that require on tick but, don't want to be unoptimized and grab the container every time
	 */
	@SubscribeEvent
	public void tickCap(PlayerTickEvent e)
	{
		if(e.phase != Phase.END)
			return;
		CapContainer c = CapabilityReg.getCapabilityConatainer(e.player);
		if(c == null || c.ticks.size() == 0)
			return;
		c.tick(e.player);
	}
	
}
