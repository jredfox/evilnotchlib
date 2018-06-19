package com.EvilNotch.lib.minecraft.content.commands;

import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.EnumChatFormatting;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.IClientCommand;

public class CMDSeedGet extends CommandBase {
	
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "seedGet";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "commands.reload.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        World world = (World)(sender instanceof EntityPlayer ? ((EntityPlayer)sender).world : server.getWorld(0));
        EntityUtil.sendClipBoard(EnumChatFormatting.WHITE,EnumChatFormatting.WHITE, (EntityPlayer)sender, "Seed:" , "" + world.getSeed());
    }
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
    	return true;
    }

}
