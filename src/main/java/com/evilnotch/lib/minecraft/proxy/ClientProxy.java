package com.evilnotch.lib.minecraft.proxy;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.eventhandler.ClientEvents;
import com.evilnotch.lib.main.eventhandler.StopSteve;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.main.skin.IStopSteve;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.command.client.ClientUUID;
import com.evilnotch.lib.minecraft.event.client.UUIDChangeEvent;
import com.evilnotch.lib.minecraft.network.IgnoreTilePacket;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.minecraft.util.UUIDPatcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void proxyStart()
	{
		LoaderMain.isClient = true;
	}
	
	@Override
	public void preinit(FMLPreInitializationEvent e) 
	{
		super.preinit(e);
		registerEvents();
		this.cacheUUID();
		//load the skin cache
		SkinCache.init();
	}
	
	@Override
	public void initMod()
	{
		super.initMod();
		//load default skins into vram so game doesn't lag on first launch
		bindTexture(PlayerUtil.STEVE);
		bindTexture(PlayerUtil.ALEX);
	}
	
	@Override
	public void postinit()
	{
		super.postinit();
	}
	
	private void registerEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		if(ConfigCore.asm_stopSteve && Config.stopSteve)
		{
			MinecraftForge.EVENT_BUS.register(new StopSteve());
		}
		if(Config.debug)
		{
			GeneralRegistry.registerClientCommand(new ClientUUID());
		}
	}
	
	public static void disconnect()
	{
		clearClientData();
		MainJava.proxy.handleUUIDChange(new PacketUUID(ClientProxy.org.org));//Undo UUID Changes done from connecting to a server
	}
	
	public static void clearClientData()
	{
		TickRegistry.garbageCollectClient();
		Seeds.clearSeeds();
		IgnoreTilePacket.ignoreTiles.clear();
		StopSteve.m = 0L;
		ClientCapHooks.others.clear();
		VanillaBugFixes.badProfiles.clear();
		System.out.println("disconnecting..........................");
	}

	public static EntityPlayer getPlayer() 
	{
		return Minecraft.getMinecraft().player;
	}

	public static boolean isCtrlDown() 
	{
		return GuiScreen.isCtrlKeyDown();
	}

	public static boolean isCurrentThread() 
	{
		return Minecraft.getMinecraft().isCallingFromMinecraftThread();
	}
	
	public static int perm;
	public static void cachePlayerPermission() 
	{
		perm = Minecraft.getMinecraft().player.getPermissionLevel();
	}
	
	public static void fixPermissionLevel(boolean allowCheats) 
	{
		if(!allowCheats)
			Minecraft.getMinecraft().player.setPermissionLevel(perm);
	}
	
	/**
	 * stores the original UUID and name of the client
	 */
	public static EvilGameProfile org = null;
	
	public static void cacheUUID()
	{
		if(org != null)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		GameProfile p = mc.getSession().getProfile();
		VanillaBugFixes.fixMcProfileProperties();
		org = new EvilGameProfile(UUIDPatcher.getUUID(p), p);
		org.getProperties().removeAll("textures");
	}
	
	@Override
	public void handleUUIDChange(PacketUUID message) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Minecraft mc = Minecraft.getMinecraft();
			Session session = mc.getSession();
			UUID org = UUIDPatcher.getUUID(session.getProfile());
			UUID uuid = message.uuid;
			
			//Set UUID of the Session
			ReflectionUtil.setFinalObject(session, uuid.toString().replace("-", ""), Session.class, new MCPSidedString("playerID", "field_148257_b").toString());//sets uuid of the session
			
			//After Changing the Sessions UUID cache it for further use
			GameProfile profile = session.getProfile();
			
			//Sync GameProfile with Player's
			if(mc.myNetworkManager != null && mc.myNetworkManager.getNetHandler() instanceof NetHandlerLoginClient)
			{
				NetHandlerLoginClient nc = (NetHandlerLoginClient) mc.myNetworkManager.getNetHandler();
				ReflectionUtil.setFinalObject(nc.gameProfile, uuid, GameProfile.class, "id");
			}
			
			EntityPlayerSP player = mc.player;
			if(player != null)
			{
				//Set UUID of the Player's profile
				ReflectionUtil.setFinalObject(player.getGameProfile(), uuid, GameProfile.class, "id");
				//Set UUID of the Player
				player.setUniqueId(uuid);
				//Set UUID of the EntityPlayerSP#connection#gameProfile
				if(player.connection != null)
					ReflectionUtil.setFinalObject(player.connection.getGameProfile(), uuid, GameProfile.class, "id");
			}
			
			if(!org.equals(uuid) || player != null && !org.equals(player.getGameProfile().getId()))
			{
				MinecraftForge.EVENT_BUS.post(new UUIDChangeEvent(org, uuid, player));
			}
		});
	}
	
	@Override
	public void setFoodSaturationLevel(FoodStats fs, float saturationLevel)
	{
		fs.setFoodSaturationLevel(saturationLevel);
	}
	
	@Override
	public void addScheduledTask(Runnable run) 
	{
		Minecraft.getMinecraft().addScheduledTask(run);
	}
	
	/**
	 * @return Minecraft#running on Client and true on Dedicated Server
	 */
	@Override
	public boolean running()
	{
		return Minecraft.getMinecraft().running;
	}
	
	@Override
	public String getUsername()
	{
		return Minecraft.getMinecraft().getSession().getProfile().getName();
	}
	
	@Override
	public boolean isClient() 
	{
		return true;
	}
	
	public boolean isClient(EntityPlayer p)
	{
		return p == Minecraft.getMinecraft().player;
	}
	
	public boolean isClient(UUID id) 
	{
		return Minecraft.getMinecraft().player.getUniqueID().equals(id);
	}
	
	@Override
	public void d(float height)
	{
        GlStateManager.translate(0.0F, height + 0.1F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
	}
	
	@Override
	public void fixMcProfileProperties()
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.profileProperties == null)
			mc.profileProperties = new PropertyMap();
		Session session = mc.getSession();
		
		//sync forge's profile properties with vanilla's. I don't understand why they are not one and the same
		if(session.hasCachedProperties())
			ReflectionUtil.setObject(session, mc.profileProperties, Session.class, "properties");
		else
			session.setProperties(mc.profileProperties);
	}
	
	@Override
	public void bindTexture(ResourceLocation r)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(r);
	}
	
	@Override
	public void deleteTexture(ResourceLocation r) 
	{
        Minecraft.getMinecraft().addScheduledTask(()->
        {
        	Minecraft.getMinecraft().getTextureManager().deleteTexture(r);
        });
	}
	
	@Override
	public int getServerPort(MinecraftServer server)
	{
		try
		{
			IntegratedServer is = (IntegratedServer) server;
			if(is == null || is.lanServerPing == null)
				return 0;
			String address = is.lanServerPing.address;
			int index = address.lastIndexOf(':');
			String port = index != -1 ? address.substring(index + 1) : address;
			return Integer.parseInt(port);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return 0;
	}
	
	public PropertyMap getProperties()
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.profileProperties == null)
			mc.profileProperties = new PropertyMap();
		return mc.profileProperties;
	}
	
	/**
	 * Fires If Skin Has No Download Notify the NetworkPlayerInfo
	 */
	@Override
	public void noSkin(Map map, Object callback)
	{
		if(!(callback instanceof IStopSteve))
			return;
		
		//Notify IStopSteve of noSkin before the skin types can fire
		if(!map.containsKey(Type.SKIN))
			((IStopSteve)callback).skinUnAvailable(Type.SKIN, null, null);
		
		//Notify IStopSteve of noSkin for all other types
		for(Type compare : Type.values())
		{
			if(compare != Type.SKIN && !map.containsKey(compare))
				((IStopSteve)callback).skinUnAvailable(compare, null, null);
		}
	}
	
	@Override
	public void noSkin(Object callback, Type typeIn, ResourceLocation skinLoc, Object skinTexture)
	{
		if(callback instanceof IStopSteve)
		{
			((IStopSteve)callback).skinUnAvailable(typeIn, skinLoc, (MinecraftProfileTexture)skinTexture);
		}
	}
	
	@Override
	public void noSkin(int responseCode, Object callback, Type typeIn, ResourceLocation skinLoc, Object skinTexture) 
	{
		//if not ok skin has failed copied from ThreadDownloadImageData$1.class itself
		if(responseCode / 100 != 2)
		{
			this.noSkin(callback, typeIn, skinLoc, skinTexture);
		}
	}

	@Override
	public void skinElytra(Object s, Map map, Object callback)
	{
		if(map.containsKey(Type.ELYTRA))
			((SkinManager)s).loadSkin( ((Map<Type, MinecraftProfileTexture>)map).get(Type.ELYTRA), Type.ELYTRA, (SkinAvailableCallback) callback);
	}
	
	@Override
	public void dlHook(HttpURLConnection con) 
	{
		super.dlHook(con);
	}
	
	@Override
	public void loadComplete() 
	{
		if(Config.skinCache)
		{
			long maxWeight = 10000L;
			try
			{
				SkinCache.INSTANCE.waitQue(maxWeight);
			}
			catch (InterruptedException e)
			{
				System.err.println("SkinCache Download Que lasted over the Maximum Expected Value Of:" + maxWeight);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Removes a Player's GameProfile from the Vanilla SkinCache.
	 * @removes Skin from {@link SkinManager#skinCacheLoader}
	 * @removes Skin from {@link YggdrasilMinecraftSessionService#insecureProfiles}
	 */
	public static void removeSkinCache(Minecraft mc, AbstractClientPlayer ap)
	{
		GameProfile profile = ap.getGameProfile();
		
		//Remove Skin From SkinManager
		mc.getSkinManager().skinCacheLoader.invalidate(profile);//MorePlayerModels support
		
		//Remove Skin From YggdrasilMinecraftSessionService in case mods always expect the cached profile to be their mojang's profile always and uses the session service cache
		MinecraftSessionService ss = mc.getSessionService();
		if(ss instanceof YggdrasilMinecraftSessionService)
			((YggdrasilMinecraftSessionService)ss).insecureProfiles.invalidate(profile);
		else if(ss != null)
			System.err.println("Unsupported MinecraftSessionService:" + ss.getClass().getName());
	}

	public static void setImageBufferTexture(Type type, MinecraftProfileTexture texture, IImageBuffer ib) 
	{
		if(ib instanceof ImageBufferDownload)
		{
			ImageBufferDownload buf = (ImageBufferDownload) ib;
			buf.evlTexture = texture;
			buf.evlType = type;
		}
	}
	
}
