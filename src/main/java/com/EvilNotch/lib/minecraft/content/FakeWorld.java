package com.EvilNotch.lib.minecraft.content;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.MainJava;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class FakeWorld extends World {
    public static final WorldSettings worldSettings = getFakeWorldSettings();
    public static final WorldInfo worldInfo = new WorldInfo(worldSettings, MainJava.MODID + "_fake_world");
    public static final FakeSaveHandler saveHandler = new FakeSaveHandler();
    public static final WorldProvider worldProvider = new WorldProvider() {
        @Override
        public DimensionType getDimensionType() {
            return DimensionType.OVERWORLD;
        }
        @Override
        public long getWorldTime() {
            return 0;
        }
        @Override
        public Biome getBiomeForCoords(BlockPos pos)
        {
        	return Biomes.PLAINS;
        }
        @Override
        public boolean canCoordinateBeSpawn(int x, int z)
        {
        	return false;
        }
        @Override
        public boolean isSurfaceWorld()
        {
            return true;
        }
        @Override
        public BlockPos getSpawnCoordinate()
        {
        	return new BlockPos(0,4,0);
        }
        @Override
        public BiomeProvider getBiomeProvider()
        {
        	return new net.minecraft.world.biome.BiomeProviderSingle(Biomes.PLAINS);
        }
        @Override
        public int getDimension()
        {
        	return 0;
        }
        @Override
        public BlockPos getRandomizedSpawnPoint()
        {
        	return new BlockPos(0,4,1);
        }
        @Override
        public BlockPos getSpawnPoint()
        {
        	return new BlockPos(0,4,0);
        }
    };

    public FakeWorld() {
        super(saveHandler, worldInfo, worldProvider, new Profiler(), true);
        worldInfo.setDifficulty(EnumDifficulty.NORMAL);
        worldProvider.setWorld(this);
    }

    public static WorldSettings getFakeWorldSettings() {
		WorldSettings settings = new WorldSettings(0, GameType.SURVIVAL, true, false, WorldType.DEFAULT);
		ReflectionUtil.setObject(settings,true,WorldSettings.class,FieldAcess.commandsAllowed);
		return settings;
	}

	@Override
    public DifficultyInstance getDifficultyForLocation(BlockPos pos)
    {
    	return new DifficultyInstance(this.getDifficulty(), 0, 0, 0);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return new IChunkProvider() {
            @Nullable
            @Override
            public Chunk getLoadedChunk(int x, int z) {
                return null;
            }

            @Override
            public Chunk provideChunk(int x, int z) {
                return null;
            }

            @Override
            public boolean tick() {
                return false;
            }

            @Override
            public String makeString() {
                return null;
            }

            @Override
            public boolean isChunkGeneratedAt(int p_191062_1_, int p_191062_2_) {
                return false;
            }
        };
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }

    public static class FakeSaveHandler implements ISaveHandler {

        @Override
        public WorldInfo loadWorldInfo() {
            return worldInfo;
        }

        @Override
        public void checkSessionLock() throws MinecraftException {

        }

        @Override
        public IChunkLoader getChunkLoader(WorldProvider provider) {
            return new IChunkLoader() {
                @Nullable
                @Override
                public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
                    return null;
                }

                @Override
                public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {

                }

                @Override
                public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {

                }

                @Override
                public void chunkTick() {

                }

                @Override
                public void flush() {

                }

                @Override
                public boolean isChunkGeneratedAt(int p_191063_1_, int p_191063_2_) {
                    return false;
                }
            };
        }

        @Override
        public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {

        }

        @Override
        public void saveWorldInfo(WorldInfo worldInformation) {

        }

        @Override
        public IPlayerFileData getPlayerNBTManager() {
            return new IPlayerFileData() {
                @Override
                public void writePlayerData(EntityPlayer player) {

                }

                @Override
                public NBTTagCompound readPlayerData(EntityPlayer player) {
                    return new NBTTagCompound();
                }

                @Override
                public String[] getAvailablePlayerDat() {
                    return new String[0];
                }
            };
        }

        @Override
        public void flush() {

        }

        @Override
        public File getWorldDirectory() {
            return null;
        }

        @Override
        public File getMapFileFromName(String mapName) {
            return null;
        }

        @Override
        public TemplateManager getStructureTemplateManager() {
            return null;
        }
    }
}