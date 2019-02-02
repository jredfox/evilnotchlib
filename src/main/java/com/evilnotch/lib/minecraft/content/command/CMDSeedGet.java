package com.evilnotch.lib.minecraft.content.command;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.EnumChatFormatting;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CMDSeedGet extends CommandBase {
	
    /**
     * Gets the name of the command
     */
	@Override
    public String getName()
    {
        return "seed";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel()
    {
        return Config.seedF3 ? 0 : super.getRequiredPermissionLevel();
    }

    /**
     * Gets the usage string for the command.
     */
    @Override
    public String getUsage(ICommandSender sender)
    {
       return "commands.seed.usage";
    }

    /**
     * Callback for when the command is executed
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
    	if(!(sender instanceof EntityPlayerMP))
    	{
    		throw new WrongUsageException("sender isn't player",new Object[0]);
    	}
        World world = (World)(sender instanceof EntityPlayer ? ((EntityPlayer)sender).world : server.getWorld(0));
        PlayerUtil.sendClipBoard(EnumChatFormatting.WHITE,EnumChatFormatting.WHITE, (EntityPlayer)sender, "Seed:","" + world.getSeed(),false);
        PlayerUtil.sendClipBoard((EntityPlayerMP)sender,  "Seed:" + world.getSeed());
    }
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
    	return true;
    }

}
