package com.evilnotch.lib.minecraft.capability.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.capability.LoginCap;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PCCapUpload;
import com.evilnotch.lib.minecraft.network.packet.PCCapUploadUpdate;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ClientCapHooks {
	/**
	 * The ResourceLocation ID when the Client Capabilities are on the Server
	 */
	public static final ResourceLocation ID_LOGIN = new ResourceLocation(MainJava.MODID, "ILoginHooks");
	/**
	 * The ResourceLocation ID That links to the NBTTagCompound inside of ILoginHooks tag
	 */
	public static final ResourceLocation ID_CLIENTCAPS = new ResourceLocation(MainJava.MODID, "IClientCaps");
	/**
	 * Login Hooks Registry Allowing Client to Send Server Data on Login before the EntityPlayerMP gets created
	 */
	public static Map<ResourceLocation, ILoginHook> loginHooks = new LinkedHashMap<>();
	/**
	 * The IClientCap Registry of your own Capabilities
	 */
	public static Map<ResourceLocation, IClientCap> clientCaps = new LinkedHashMap<>();
	/**
	 * Player to Client Capabilities for other players
	 */
	public static Map<UUID, Map<ResourceLocation, IClientCap>> others = new HashMap();
	
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
		register(new LoginHookClient());
	}
	
	public static NBTTagCompound login()
	{
		NBTTagCompound root = new NBTTagCompound();
		for(ILoginHook h : loginHooks.values())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			h.write(nbt);
			root.setTag(convertID(h.getId()), nbt);
		}
		return root;
	}
	
	public static void write(NBTTagCompound nbt, Collection<IClientCap> caps)
	{
		for(IClientCap cap : caps)
			cap.write(nbt);
	}
	
	public static void read(NBTTagCompound nbt, Collection<IClientCap> caps)
	{
		for(IClientCap cap : caps)
			cap.read(nbt);
	}
	
	/**
	 * Get Automatically IClientCap of Player regardless of self or other
	 */
	public static IClientCap get(EntityPlayer p, ResourceLocation loc)
	{
		return MainJava.proxy.isClient(p) ? get(loc) : get(p.getUniqueID(), loc);
	}
	
	/**
	 * Get IClientCap of your current Player
	 */
	public static IClientCap get(ResourceLocation loc)
	{
		return clientCaps.get(loc);
	}
	
	/**
	 * Get IClientCap of EntityOtherPlayerMP
	 */
	public static IClientCap get(UUID uuid, ResourceLocation loc)
	{
		return others.containsKey(uuid) ? others.get(uuid).get(loc) : null;
	}
	
	/**
	 * Sets IClientCap of any Player
	 */
	public static void set(EntityPlayer p, ResourceLocation loc, Object o)
	{
		get(p, loc).set(o);
	}
	
	/**
	 * Sets the IClientCap of your Player
	 */
	public static void set(ResourceLocation loc, Object o)
	{
		get(loc).set(o);
	}
	
	/**
	 * Uploads all IClientCaps to the server
	 */
	public static void upload()
	{
		NetWorkHandler.INSTANCE.sendToServer(new PCCapUpload(null));
	}
	
	/**
	 * Uploads only specified IClientCaps to the server
	 */
	public static void uploadUpdate(ResourceLocation... ids)
	{
		NetWorkHandler.INSTANCE.sendToServer(new PCCapUploadUpdate(ids));
	}
	
	/**
	 * Downloads all IClientCaps wiping previous data of the other user
	 */
	public static void download(UUID uuid, NBTTagCompound nbt)
	{
		Map<ResourceLocation, IClientCap> caps = new HashMap();
		dlup(caps, nbt);
		others.put(uuid, caps);
	}
	
	/**
	 * Downloads specified IClientCaps without wiping previous data
	 */
	public static void downloadUpdate(UUID uuid, NBTTagCompound nbt)
	{
		Map<ResourceLocation, IClientCap> caps = others.get(uuid);
		if(caps == null) {
			System.err.println("Error Invalid Update IClientCaps UUID:" + uuid);
			caps = new HashMap();
			others.put(uuid, caps);
		}
		dlup(caps, nbt);
	}
	
	private static void dlup(Map<ResourceLocation, IClientCap> caps, NBTTagCompound nbt) 
	{
		for(String k : nbt.getKeySet())
		{
			ResourceLocation id = convertID(k);
			if(k == null)
			{
				System.err.println("Client Recieved Maulformed Update IClientCap Tag:" + k);
				continue;
			}
			IClientCap cap = clientCaps.get(id);
			if(cap != null)
				caps.put(id, cap.clone(nbt));
			else
			{
				System.err.println("Recieved Unkown IClientCap:" + id);
				caps.put(id, new ClientCap(id, nbt.getTag(k)) );
			}
		}
	}
	
	public static boolean getBoolean(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<Boolean> c = get(p, loc);
		return c != null && c.get();
	}
	
	public static String getString(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<String> c = get(p, loc);
		return c != null ? c.get() : "";
	}
	
	public static long getLong(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<Long> c = get(p, loc);
		return c != null ? c.get() : 0L;
	}
	
	public static int getInt(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<Integer> c = get(p, loc);
		return c != null ? c.get() : 0;
	}
	
	public static short getShort(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<Short> c = get(p, loc);
		return c != null ? c.get() : 0;
	}
	
	public static double getDouble(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<Double> c = get(p, loc);
		return c != null ? c.get() : 0D;
	}
	
	public static float getFloat(EntityPlayer p, ResourceLocation loc) 
	{
		IClientCap<Float> c = get(p, loc);
		return c != null ? c.get() : 0F;
	}
	
	public static LoginCap getLoginCap(EntityPlayerMP p)
	{
		return (LoginCap) CapabilityRegistry.getCapability(p, ID_LOGIN);
	}

	public static void registerServerCap(EntityPlayerMP player, GameProfile profile) 
	{
		if(profile instanceof EvilGameProfile)
			CapabilityRegistry.getCapContainer(player).registerCapability(ID_LOGIN, new LoginCap(((EvilGameProfile)profile).clientCaps));
	}
	
	public static ResourceLocation convertID(String key)
	{
		String[] arr = key.split("_", 2);
		return arr.length >= 2 ? new ResourceLocation(arr[0], arr[1]) : null;
	}
	
	public static String convertID(ResourceLocation id)
	{
		return id.toString().replace(":", "_");
	}
	
	public static NBTTagCompound readNBT(PacketBuffer buf)
	{
		try
		{
			return buf.readCompoundTag();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return new NBTTagCompound();
	}

}