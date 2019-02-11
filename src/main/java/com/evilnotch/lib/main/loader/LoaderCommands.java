package com.evilnotch.lib.main.loader;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.command.CMDDim;
import com.evilnotch.lib.minecraft.command.CMDKick;
import com.evilnotch.lib.minecraft.command.CMDSeedGet;
import com.evilnotch.lib.minecraft.command.CMDStack;
import com.evilnotch.lib.minecraft.command.CMDTP;
import com.evilnotch.lib.minecraft.command.CMDTeleport;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;

import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class LoaderCommands {
	
	public static void load()
	{
		//new commands
		GeneralRegistry.registerCommand(new CMDDim());
		GeneralRegistry.registerCommand(new CMDStack());
		GeneralRegistry.registerCommand(new CMDKick());
		
		//replacements
		GeneralRegistry.replaceVanillaCommand("seed", new CMDSeedGet());
		if(Config.replaceTP)
		{
			GeneralRegistry.replaceVanillaCommand("tp",new CMDTP());
			GeneralRegistry.replaceVanillaCommand("teleport",new CMDTeleport());
		}
	}

	public static void registerToWorld(FMLServerStartingEvent e) 
	{
		MinecraftServer server = e.getServer();
		GeneralRegistry.removeActiveCommands(server);
		for(ICommand cmd : GeneralRegistry.getCmdList())
			e.registerServerCommand(cmd);
		
		GameRules g = server.getEntityWorld().getGameRules();
		GeneralRegistry.removeActiveGameRules(g);
		GeneralRegistry.injectGameRules(g);
	}

}
