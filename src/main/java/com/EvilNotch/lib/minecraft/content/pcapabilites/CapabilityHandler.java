package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.io.File;

import com.EvilNotch.lib.main.eventhandlers.LibEvents;
import com.EvilNotch.lib.main.eventhandlers.UUIDFixer;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.events.PlayerDataFixEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class CapabilityHandler {
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void capabilityReader(PlayerEvent.LoadFromFile e)
	{
		if(CapabilityReg.reg.size() == 0)
			return;
		EntityPlayer p = e.getEntityPlayer();
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
	
	@SubscribeEvent
	public void capabilityRemove(PlayerLoggedOutEvent e)
	{
		if(CapabilityReg.reg.size() == 0)
			return;
		if(CapabilityReg.capabilities.get(e.player.getName()) == null)
		{
			System.out.println("returning can't continue player removed from hashmap:" + e.player.getName() );
			return;
		}
		File f = new File(LibEvents.playerDataDir,"caps/" + e.player.getUniqueID().toString() + ".dat");
		NBTTagCompound nbt = new NBTTagCompound();
		EntityPlayer p = e.player;
		CapabilityReg.save(p,nbt);
		NBTUtil.updateNBTFileSafley(f, nbt);
		CapabilityReg.capabilities.remove(p.getName() );
		System.out.println("saved player from logout:" + p.getName() + " toFile:" + f);
	}
	/**
	 * syncs over old capabilities from uuidchange
	 */
	@SubscribeEvent
	public void capabilitySync(PlayerDataFixEvent e)
	{
		System.out.println("Patching Lib Player Capbilities to new uuid:" + e.uuidNew);
		File caps = new File(LibEvents.playerDataDir,"caps/" + e.uuidOld.toString() + ".dat");
		NBTTagCompound nbt = NBTUtil.getFileNBTSafley(caps);
		if(nbt == null)
		{
			nbt = new NBTTagCompound();
			System.out.println("Unable to get nbt tag data creating blank tag data will wipe");
		}
		CapabilityReg.read(e.player, nbt);
	}
	
}
