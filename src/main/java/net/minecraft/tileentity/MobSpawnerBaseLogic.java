package net.minecraft.tileentity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.util.JavaUtil;
import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MobSpawnerBaseLogic
{
    /** The delay to spawn. */
    public int spawnDelay = 20;
    /** List of potential entities to spawn */
    public final List<WeightedSpawnerEntity> potentialSpawns = Lists.<WeightedSpawnerEntity>newArrayList();
    public WeightedSpawnerEntity spawnData = new WeightedSpawnerEntity();
    /** The rotation of the mob inside the mob spawner */
    public double mobRotation;
    /** the previous rotation of the mob inside the mob spawner */
    public double prevMobRotation;
    public int minSpawnDelay = 200;
    public int maxSpawnDelay = 800;
    public int spawnCount = 4;
    /** Cached instance of the entity to render inside the spawner. */
    public Entity cachedEntity;
    public int maxNearbyEntities = 6;
    /** The distance from which a player activates the spawner. */
    public int activatingRangeFromPlayer = 16;
    /** The range coefficient for spawning entities around. */
    public int spawnRange = 4;
    @SideOnly(Side.CLIENT)
	public List<Entity> cachedEntities = new ArrayList();
    @SideOnly(Side.CLIENT)
	public double[] offsets;
    public boolean updated = false;
	public boolean active = false;

    @Nullable
    public ResourceLocation getEntityId()
    {
        String s = this.spawnData.getNbt().getString("id");
        return StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
    }
    
    public void setEntityId(@Nullable ResourceLocation id)
    {
        if (id != null)
        {
            this.spawnData.getNbt().setString("id", id.toString());
        }
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    public boolean isActivated()
    {
        BlockPos blockpos = this.getSpawnerPosition();
        return this.getSpawnerWorld().isAnyPlayerWithinRangeAt((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D, (double)this.activatingRangeFromPlayer);
    }

    public void updateSpawner()
    {
    	updated = true;
        if (!this.isActivated())
        {
            this.prevMobRotation = this.mobRotation;
            active = false;
        }
        else
        {
        	active = true;
            BlockPos blockpos = this.getSpawnerPosition();

            if (this.getSpawnerWorld().isRemote)
            {
            	
                double d3 = (double)((float)blockpos.getX() + this.getSpawnerWorld().rand.nextFloat());
                double d4 = (double)((float)blockpos.getY() + this.getSpawnerWorld().rand.nextFloat());
                double d5 = (double)((float)blockpos.getZ() + this.getSpawnerWorld().rand.nextFloat());
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
            }
            else
            {
                if (this.spawnDelay == -1)
                {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;
                
                NBTTagCompound nbttagcompound = this.spawnData.getNbt();
                
                for (int i = 0; i < this.spawnCount; ++i)
                {
                    	
                    NBTTagList nbttaglist = nbttagcompound.getTagList("Pos", 6);
                    World world = this.getSpawnerWorld();
                    int j = nbttaglist.tagCount();
                    double d0 = j >= 1 ? nbttaglist.getDoubleAt(0) : (double)blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
                    double d1 = j >= 2 ? nbttaglist.getDoubleAt(1) : (double)(blockpos.getY() + world.rand.nextInt(3) - 1);
                    double d2 = j >= 3 ? nbttaglist.getDoubleAt(2) : (double)blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
                    Entity entity = EntityUtil.getEntityJockey(nbttagcompound, world, d0, d1, d2, true,false);

                    if (entity == null)
                    {
                    	this.resetTimer();
                        return;
                    }

                    int k = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), (double)(blockpos.getX() + 1), (double)(blockpos.getY() + 1), (double)(blockpos.getZ() + 1))).grow((double)this.spawnRange)).size();

                    if (k >= this.maxNearbyEntities)
                    {
                        this.resetTimer();
                        return;
                    }

                    EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
                    entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || net.minecraftforge.event.ForgeEventFactory.canEntitySpawnSpawner(entityliving, getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ, this))
                    {
                        if (this.spawnData.getNbt().getSize() == 1 && this.spawnData.getNbt().hasKey("id", 8) && entity instanceof EntityLiving)
                        {
                            if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(entityliving, this.getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ, this))
                            ((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
                        }

                        AnvilChunkLoader.spawnEntity(entity, world);
                        world.playEvent(2004, blockpos, 0);

                        if (entityliving != null)
                        {
                            entityliving.spawnExplosionParticle();
                        }

                        flag = true;
                    }
                }

                if (flag)
                {
                    this.resetTimer();
                }
            }
        }
    }

    public void resetTimer()
    {
        if (this.maxSpawnDelay <= this.minSpawnDelay)
        {
            this.spawnDelay = this.minSpawnDelay;
        }
        else
        {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
        }

        if (!this.potentialSpawns.isEmpty())
        {
            this.setNextSpawnData((WeightedSpawnerEntity)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.potentialSpawns));
        }

        this.broadcastEvent(1);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
    	if(this.getSpawnerWorld() != null)
    		System.out.println("readingFromNBT:" + this.getSpawnerWorld().isRemote);
        this.spawnDelay = nbt.getShort("Delay");
        this.potentialSpawns.clear();

        if (nbt.hasKey("SpawnPotentials", 9))
        {
            NBTTagList nbttaglist = nbt.getTagList("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                this.potentialSpawns.add(new WeightedSpawnerEntity(nbttaglist.getCompoundTagAt(i)));
            }
        }

        if (nbt.hasKey("SpawnData", 10))
        {
            this.setNextSpawnData(new WeightedSpawnerEntity(1, nbt.getCompoundTag("SpawnData")));
        }
        else if (!this.potentialSpawns.isEmpty())
        {
            this.setNextSpawnData((WeightedSpawnerEntity)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.potentialSpawns));
        }

        if (nbt.hasKey("MinSpawnDelay", 99))
        {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.hasKey("MaxNearbyEntities", 99))
        {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.hasKey("SpawnRange", 99))
        {
            this.spawnRange = nbt.getShort("SpawnRange");
        }
        this.cachedEntity = null;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        ResourceLocation resourcelocation = this.getEntityId();

        if (resourcelocation == null)
        {
            return nbt;
        }
        else
        {
        	nbt.setShort("Delay", (short)this.spawnDelay);
        	nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
        	nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        	nbt.setShort("SpawnCount", (short)this.spawnCount);
        	nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
            nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
            nbt.setShort("SpawnRange", (short)this.spawnRange);
            nbt.setTag("SpawnData", this.spawnData.getNbt().copy());
            NBTTagList nbttaglist = new NBTTagList();

            if (this.potentialSpawns.isEmpty())
            {
                nbttaglist.appendTag(this.spawnData.toCompoundTag());
            }
            else
            {
                for (WeightedSpawnerEntity weightedspawnerentity : this.potentialSpawns)
                {
                    nbttaglist.appendTag(weightedspawnerentity.toCompoundTag());
                }
            }

            nbt.setTag("SpawnPotentials", nbttaglist);
            return nbt;
        }
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int delay)
    {
        if (delay == 1 && this.getSpawnerWorld().isRemote)
        {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public Entity getCachedEntity()
    {
        if (this.cachedEntity == null)
        {
            this.cachedEntity = AnvilChunkLoader.readWorldEntity(this.spawnData.getNbt(), this.getSpawnerWorld(), false);

            if (this.spawnData.getNbt().getSize() == 1 && this.spawnData.getNbt().hasKey("id", 8) && this.cachedEntity instanceof EntityLiving)
            {
                ((EntityLiving)this.cachedEntity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(this.cachedEntity)), (IEntityLivingData)null);
            }
        }
        return this.cachedEntity;
    }
    
	/**
	 * Doesn't force nbt on anything unlike vanilla's methods.
	 * Supports silkspawners rendering for skeleton traps
	 */
	public static Entity getEntityJockey(NBTTagCompound compound,World worldIn, double x, double y, double z,boolean useInterface,boolean attemptSpawn) 
	{	
        Entity entity = getEntity(compound,worldIn,new BlockPos(x,y,z),useInterface);
        if(entity == null)
        	return null;
		
        Entity toMount = entity;
		if(new ResourceLocation(compound.getString("id")).toString().equals("minecraft:skeleton_horse"))
		{
			if(compound.hasKey("SkeletonTrap"))
			{
				Entity e2 = EntityUtil.createEntityFromNBTQuietly(new ResourceLocation("skeleton"), NBTUtil.getNBTFromString("{ArmorItems:[{},{},{},{id:iron_helmet,Count:1,tag:{ench:[{lvl:1s,id:33s}]} }],HandItems:[{id:bow,Count:1,tag:{ench:[{lvl:1s,id:33s}]} },{}] }"), worldIn);
				e2.startRiding(entity,true);
				return entity;
			}
		}
        
        if(attemptSpawn)
        {
            entity.forceSpawn = true;
        	if(!worldIn.spawnEntity(entity))
        		return null;
        }
        
        if (compound.hasKey("Passengers", 9))
        {
             NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
             for (int i = 0; i < nbttaglist.tagCount(); ++i)
             {
                 Entity entity1 = getEntityJockey(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z,useInterface,attemptSpawn);
                  if (entity1 != null)
                  {
                      entity1.startRiding(toMount, true);
                  }
             }
        }

       return entity;
	}

	/**
	 * first index is to determine if your on the first part of the opening of the nbt if so treat nbt like normal
	 */
	public static Entity getEntity(NBTTagCompound nbt,World world,BlockPos pos,boolean useInterface) {
		Entity e = null;
		if(getEntityProps(nbt).getSize() > 0)
		{
			e = createEntityFromNBTRender(nbt,pos,world);
		}
		else
		{
			e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
			if(e == null)
				return null;
			e.setLocationAndAngles(0, 0, 0,0.0F, 0.0F);
			NBTTagCompound tag = EntityUtil.getEntityNBT(e);
			
			e.readFromNBT(tag);
			
			if(e instanceof EntityLiving && useInterface || e instanceof EntityShulker)
			{
				((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
			}
		}
		return e;
	}
	/**
	 * create an entity from nbt with full rendering capabilities
	 */
	public static Entity createEntityFromNBTRender(NBTTagCompound nbt,BlockPos pos, World world) {
		Entity e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
		if(e == null)
			return null;
		e.setLocationAndAngles(0,0,0,0.0F,0.0F);
		e.readFromNBT(nbt);
		//hard coded dynamic fix for a shitty thing now it doesn't change it's type only it's render stuffs
		if(e instanceof EntityShulker)
		{
			((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
		}
		return e;
	}

	private static NBTTagCompound getEntityProps(NBTTagCompound nbt) {
		if(nbt == null)
			return null;
		nbt = nbt.copy();
		nbt.removeTag("Passengers");
		nbt.removeTag("id");
		return nbt;
	}
    
    @SideOnly(Side.CLIENT)
    public List<Entity> getCachedEntities()
    {
        if (this.cachedEntity == null)
        {
           this.getCachedEntity();
        }
        return this.cachedEntities;
    }

    public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_)
    {
        this.spawnData = p_184993_1_;
    }

    public abstract void broadcastEvent(int id);

    public abstract World getSpawnerWorld();

    public abstract BlockPos getSpawnerPosition();

    @SideOnly(Side.CLIENT)
    public double getMobRotation()
    {
        return this.mobRotation;
    }

    @SideOnly(Side.CLIENT)
    public double getPrevMobRotation()
    {
        return this.prevMobRotation;
    }

    /* ======================================== FORGE START =====================================*/
    @Nullable public Entity getSpawnerEntity() { return null; }
}