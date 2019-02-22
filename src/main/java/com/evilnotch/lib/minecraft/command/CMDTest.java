package com.evilnotch.lib.minecraft.command;

import com.evilnotch.lib.minecraft.util.EnumChatFormatting;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.event.ClickEvent;

public class CMDTest extends CommandBase{
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}

	@Override
	public String getName() {
		return "testCMD";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return this.getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(!(sender instanceof EntityPlayerMP))
			return;
		EntityPlayerMP p = (EntityPlayerMP)sender;
	}

}
