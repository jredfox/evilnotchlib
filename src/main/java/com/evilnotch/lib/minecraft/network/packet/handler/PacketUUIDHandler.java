package com.evilnotch.lib.minecraft.network.packet.handler;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;

public class PacketUUIDHandler extends MessegeBase<PacketUUID>{

	@Override
	public void handleClientSide(PacketUUID message, EntityPlayer p) 
	{
		MainJava.proxy.handleUUIDChange(message);
	}

	@Override
	public void handleServerSide(PacketUUID message, EntityPlayer player) 
	{
		
	}

}
