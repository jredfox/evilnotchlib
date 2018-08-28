package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.List;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.minecraft.content.tick.ITickServer;
import com.EvilNotch.lib.minecraft.content.tick.TickReg;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TickSaver implements ITickServer{

	@Override
	public void tick() 
	{
		if(CapabilityReg.capabilities.size() == 0)
			return;
		if(TickReg.isRightCountServer(Config.pcapSaveTime))
		{
			System.out.println("Saving Player Capabilities:" + Config.pcapSaveTime/20);
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
			for(EntityPlayerMP p : players)
			{
				CapabilityReg.saveToFile(p);
			}
		}
	}

	@Override
	public void garbageCollect() {}

}
