package com.evilnotch.lib.minecraft.basicmc.client;

import java.util.UUID;

import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

public class ClientUUID extends CommandBase implements IClientCommand{

	@Override
	public String getName() {
		return "getUUID";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		UUID id = Minecraft.getMinecraft().player.getUniqueID();
		PlayerUtil.sendClipBoard("", "", Minecraft.getMinecraft().player, "UUID:", id.toString());
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return true;
	}

}
