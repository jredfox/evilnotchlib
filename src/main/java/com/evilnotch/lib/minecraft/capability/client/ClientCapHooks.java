package com.evilnotch.lib.minecraft.capability.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.ResourceLocation;

public class ClientCapHooks {
	
	public static Map<ResourceLocation, ILoginHook> loginHooks = new ConcurrentHashMap<>();
	public static Map<ResourceLocation, IClientCap> clientCaps = new ConcurrentHashMap<>();
	
	static
	{
		load();
	}
	
	public static void register(ILoginHook hook)
	{
		loginHooks.put(hook.getId(), hook);
	}

	public static void register(IClientCap cap)
	{
		clientCaps.put(cap.getId(), cap);
	}
	
	public static void load() 
	{
		register(new LoginCapHook());
		register(new ClientCap(new ResourceLocation("skincaptest", "ears"), true));
		register(new ClientCap(new ResourceLocation("skincaptest", "dinnerbone"), false));
	}
	
	/**
	 * fills the ILoginHook from CPacketLoginStart#loginHooks
	 */
	public static List<ILoginHook> hookLogin()
	{
		List<ILoginHook> logins = new ArrayList(loginHooks.size());
		for(ILoginHook h : loginHooks.values())
			logins.add(h.newInstance());
		return logins;
	}

	public static void readLogin(List<ILoginHook> hooks, CPacketLoginStart pck, PacketBuffer buf) 
	{
		for(ILoginHook h : hooks)
			h.read(pck, buf);
	}
	
	public static void writeLogin(List<ILoginHook> hooks, CPacketLoginStart pck, PacketBuffer buf) 
	{
		for(ILoginHook h : hooks)
			h.write(pck, buf);
	}

}
