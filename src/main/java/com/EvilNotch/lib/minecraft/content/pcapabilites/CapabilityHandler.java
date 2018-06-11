package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.EvilNotch.lib.main.eventhandlers.LibEvents;
import com.EvilNotch.lib.main.eventhandlers.UUIDFixer;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.events.PlayerDataFixEvent;

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
		File caps = new File(LibEvents.playerDataDir,"caps/" + e.getEntityPlayer().getUniqueID().toString() + ".dat");
		NBTTagCompound nbt = NBTUtil.getFileNBTSafley(caps);
		if(nbt == null)
		{
			nbt = new NBTTagCompound();
			System.out.println("Unable to get nbt tag data creating blank tag data will wipe");
		}
		CapabilityReg.read(p,nbt);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void capabilityRemove(PlayerLoggedOutEvent e)
	{
		if(CapabilityReg.reg.size() == 0 || !(e.player instanceof EntityPlayerMP))
			return;
		if(CapabilityReg.getCapabilityConatainer(e.player) == null)
		{
			System.out.println("returning player already saved:" + e.player.getName() );
			return;
		}
		File f = new File(LibEvents.playerDataDir,"caps/" + e.player.getUniqueID().toString() + ".dat");
		NBTTagCompound nbt = new NBTTagCompound();
		EntityPlayerMP p = (EntityPlayerMP) e.player;
		CapabilityReg.save(p,nbt);
		NBTUtil.updateNBTFileSafley(f, nbt);
		CapabilityReg.capabilities.remove(p.getName() );
		System.out.println("saved player from logout:" + p.getName() + " toFile:" + f);
	}
	/**
	 * used for capabilities that require on tick but, don't want to be unoptimized and grab the container every time
	 */
	@SubscribeEvent
	public void tickCap(PlayerTickEvent e)
	{
		if(e.phase != Phase.END)
			return;
		CapabilityContainer c = CapabilityReg.getCapabilityConatainer(e.player);
		if(c == null || c.ticks.size() == 0)
			return;
		
		c.tick(e.player);
	}
	/*
	@SubscribeEvent
	public void tickCap(ServerTickEvent e)
	{
		if(e.phase != Phase.END)
			return;
		Iterator<Map.Entry<String, CapabilityContainer>> it = CapabilityReg.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String, CapabilityContainer> pair = it.next();
			CapabilityContainer c = pair.getValue();
			if(c.ticks.size() == 0)
				continue;
			String n = pair.getKey();
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			EntityPlayerMP p = server.getPlayerList().getPlayerByUsername(n);
			c.tick(p);
		}
	}*/
	
	/**
	 * syncs over old capabilities from uuidchange
	 */
	@SubscribeEvent
	public void capabilitySync(PlayerDataFixEvent e)
	{
		/*System.out.println("Patching Lib Player Capbilities to new uuid:" + e.uuidNew);
		File caps = new File(LibEvents.playerDataDir,"caps/" + e.uuidOld.toString() + ".dat");
		NBTTagCompound nbt = NBTUtil.getFileNBTSafley(caps);
		if(nbt == null)
		{
			nbt = new NBTTagCompound();
			System.out.println("Unable to get nbt tag data creating blank tag data will wipe");
		}
		CapabilityReg.read(e.player, nbt);*/
	}
	
}
