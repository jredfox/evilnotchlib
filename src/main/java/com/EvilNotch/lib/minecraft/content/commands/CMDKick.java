package com.EvilNotch.lib.minecraft.content.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.EvilNotch.lib.minecraft.EntityUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CMDKick extends CommandBase {
	
	 /**
     * Gets the name of the command
     */
	@Override
    public String getName()
    {
        return "bootPlayer";
    }
    /**
    * Gets the usage string for the command.
    */
    @Override
    public String getUsage(ICommandSender sender)
    {
       return "commands.evilnotchlib.boot.usage";
    }
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		Entity e = getEntity(server, sender, args[0]);	
		if(e instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)e;
			EntityUtil.kickPlayer(player, 40, "booted");
		}
	}
	
    /**
     * Get a list of options for when the user presses the TAB key
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length != 1 ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

}
