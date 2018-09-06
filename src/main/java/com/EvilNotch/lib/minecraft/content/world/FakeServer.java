package com.EvilNotch.lib.minecraft.content.world;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.EvilNotch.lib.main.MainJava;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;

public class FakeServer extends MinecraftServer{

	public FakeServer() {
		super(null, null, null, null, null, null, null);
		// TODO Auto-generated constructor stub
	}
	@Override
    public void setGameType(GameType gameMode)
    {
    	
    }
	@Override
    public void setDifficultyForAllWorlds(EnumDifficulty difficulty)
    {
    	
    }
	@Override
    public String getName()
    {
    	return "fake_server";
    }
	@Override
    public List<String> getTabCompletions(ICommandSender sender, String input, @Nullable BlockPos pos, boolean hasTargetBlock)
    {
    	return new ArrayList(0);
    }
	@Override
    public String getServerModName()
    {
    	return "fake_server";
    }
	@Override
    public String[] getOnlinePlayerNames()
    {
    	return new String[]{""};
    }
	@Override
    public int getMaxPlayers()
    {
		return 1;
    }
	@Override
    public int getCurrentPlayerCount()
    {
    	return 0;
    }
	@Override
    public WorldServer getWorld(int dimension)
    {
    	return null;
    }
	@Override
    public void run()
    {
    	
    }
	@Override
    public void stopServer()
    {
    	
    }
	@Override
    public void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn)
    {
    	
    }
	@Override
    public void initialWorldChunkLoad()
    {
    	
    }
	@Override
    public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions)
    {
    	
    }
	@Override
	public void convertMapIfNeeded(String worldNameIn)
	{
		
	}
	@Override
    public void saveAllWorlds(boolean isSilent)
    {
    	
    }
	@Override
    public void tick()
    {
		
    }
	@Override
    public void updateTimeLightAndEntities()
    {
    	
    }

	@Override
	public boolean init() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canStructuresSpawn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GameType getGameType() {
		// TODO Auto-generated method stub
		return GameType.SURVIVAL;
	}

	@Override
	public EnumDifficulty getDifficulty() {
		// TODO Auto-generated method stub
		return EnumDifficulty.NORMAL;
	}

	@Override
	public boolean isHardcore() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOpPermissionLevel() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDedicatedServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldUseNativeTransport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCommandBlockEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String shareToLAN(GameType type, boolean allowCheats) {
		// TODO Auto-generated method stub
		return "";
	}

}
