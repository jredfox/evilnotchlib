package com.evilnotch.lib.minecraft.content.commands;

import com.evilnotch.lib.minecraft.EntityUtil;
import com.evilnotch.lib.minecraft.EnumChatFormatting;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packets.PacketClipBoard;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        return "seed";
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
       return "commands.seed.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
    	if(!(sender instanceof EntityPlayerMP))
    	{
    		throw new WrongUsageException("sender isn't player",new Object[0]);
    	}
        World world = (World)(sender instanceof EntityPlayer ? ((EntityPlayer)sender).world : server.getWorld(0));
        EntityUtil.sendClipBoard(EnumChatFormatting.WHITE,EnumChatFormatting.WHITE, (EntityPlayer)sender, "Seed:","" + world.getSeed(),false);
        EntityUtil.sendToClientClipBoard((EntityPlayerMP)sender,  "Seed:" + world.getSeed());
    }
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
    	return true;
    }

}
