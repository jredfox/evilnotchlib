package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.loader.LoaderFields;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.content.entity.EntityDefintions;
import com.evilnotch.lib.minecraft.content.entity.EntityDefintions.EntityInfo;
import com.evilnotch.lib.minecraft.content.entity.EntityDefintions.EntityType;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.minecraft.network.packet.PacketYawOffset;
import com.evilnotch.lib.minecraft.network.packet.PacketYawPitch;
import com.evilnotch.lib.minecraft.registry.SpawnListEntryAdvanced;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PointId;
import com.mojang.authlib.GameProfile;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Biomes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class EntityUtil {
	
	public static boolean cached = false;
	public static Set<ResourceLocation> end_ents = new HashSet<ResourceLocation>();
	
	public static Set<ResourceLocation> forgemobs = new HashSet();//forge mobs that are entity living
	public static HashMap<ResourceLocation,String[]> living = new HashMap();
	public static HashMap<ResourceLocation,String[]> nonliving = new HashMap();
	public static HashMap<ResourceLocation,String[]> livingbase = new HashMap();
	
	public static Set<ResourceLocation> ents_worldneedy = new HashSet();//List of entities that need the world how greedy?
	public static Set<ResourceLocation> ent_blacklist = new HashSet();//List of all failed Entities
	public static Set<ResourceLocation> ent_blacklist_commandsender = new HashSet();//List of all failed Entities
	public static Set<ResourceLocation> ent_blacklist_nbt = new HashSet();
    
	public static String getEntityString(Entity e)
	{
		return EntityList.getEntityString(e);
	}
    
    public static String getUnlocalizedName(NBTTagCompound nbt, World w)
    {
    	ResourceLocation loc = new ResourceLocation(nbt.getString("id"));
    	String unlocal = getUnlocalizedName(loc);
    	return unlocal != null ? unlocal : getUnlocalizedName(EntityUtil.createEntityFromNBTQuietly(loc, nbt, w));
    }
    /**
     * pull unlocalized name from the cache
     */
	public static String getUnlocalizedName(ResourceLocation loc) 
	{
		if(living.containsKey(loc))
		{
			return living.get(loc)[0];
		}
		else if(livingbase.containsKey(loc))
		{
			return livingbase.get(loc)[0];
		}
		else if(nonliving.containsKey(loc))
		{
			return nonliving.get(loc)[0];
		}
		return null;
	}
    
    public static String getUnlocalizedName(Entity e)
    {
    	return "entity." + EntityList.getEntityString(e) + ".name";
    }
	
	public static String TransLateEntity(NBTTagCompound nbt,World w)
	{
	   nbt.removeTag("CustomName");
	   String id = nbt.getString("id");
	   Entity e = createEntityFromNBTQuietly(new ResourceLocation(id), nbt, w,true);
	   	
	   return TransLateEntity(e,w);
	}
	 
	 /**
     * Prefer Command Sender Name If applicable else use general name
     */
    public static String TransLateEntity(Entity entity, World w)
    {
    	String cmd = translateEntityCmd(entity);
    	if(cmd != null)
    		return cmd;
    	String generic = translateEntityGeneral(entity);
    	return generic;
    }

	/**
	 * Translates general name for entity so pink sheep will return sheep
	 * Has the entity.entityNameHere.name removed if not translated properly
	 */
	public static String translateEntityGeneral(Entity entity)
	{
	   String entityName = EntityList.getEntityString(entity);
	   try
	   {
		   entityName = I18n.translateToLocal("entity." + entityName + ".name");
	   }
	   catch(Throwable t)
	   {
		   return null;
	   }
	    
	   return entityName;
	}
	/**
	 * get command sender name and returns null if vanilla does it's funky general thing
	 */
	public static String translateEntityCmd(Entity entity)
	{
		String name = getcommandSenderName(entity);
		if(name != null)
		{
			if(name.equals("entity." + "generic" + ".name") )
				return null;
		}
		return name;
	}
	
	public static String getcommandSenderName(Entity entity) 
	{
		try
		{
			String name = entity.getName();
			if(name != null)
			{
				ent_blacklist_commandsender.remove(EntityUtil.getEntityResourceLocation(entity));
			}
			return name;
		}
		catch(Throwable t)
		{
			ent_blacklist_commandsender.add(getEntityResourceLocation(entity));
			LoaderMain.logger.error("Entity Has Thrown an Error when entity.getName() Report to mod author:" + EntityList.getEntityString(entity));
		}
		return null;
	}
	
	public static Entity createEntityFromNBTQuietly(ResourceLocation loc,NBTTagCompound nbt, World worldIn)
	{
		return createEntityFromNBTQuietly(loc,nbt,worldIn,false);
	}
	
	public static Entity createEntityFromNBTQuietly(ResourceLocation loc,NBTTagCompound nbt, World worldIn,boolean constructor)
	{
	   try
	   {
		   Entity e = createEntityByNameQuietly(loc,worldIn,constructor);
		   if(e != null)
		   {
			   e.readFromNBT(nbt);
		   }
		   return e;
	  	}
	    catch(Throwable e)
	    {
	    	
	    }
	  	return null;
	}
	 
	public static Entity createEntityByNameQuietly(ResourceLocation loc, World worldIn)
	{
		return createEntityByNameQuietly(loc,worldIn,false);
	}
	 
    public static Entity createEntityByNameQuietly(ResourceLocation loc, World worldIn,boolean constructor)
    {
    	if(!constructor)
    	{
    		try
    		{
    			net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(loc);
    			return entry == null ? null : entry.newInstance(worldIn);
    		}
    		catch(Throwable e)
    		{
    			if(Config.debug)
    			{
    				e.printStackTrace();
    			}
    			return null;
    		}
    	}
    	else
    	{
    		try
    		{
    			Class clazz = EntityList.getClass(loc);
    			Constructor c = clazz.getConstructor(new Class[] {World.class});
    			return (Entity) c.newInstance(worldIn);
    		}
    		catch(Throwable e)
    		{
    			if(Config.debug)
    			{
    				e.printStackTrace();
    			}
    			return null;
    		}
    	}
    }
    
    public static ResourceLocation getEntityResourceLocation(Entity e)
	{
		net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(e.getClass());
		if(entry != null)
		{
			return entry.getRegistryName();
		}
		return null;
	}
   
    /**
     * Spawn Entity by spawnlistentry from Scratch
     */
	public static boolean spawnEntityEntry(World w, SpawnListEntry entry,double x, double y, double z) 
	{
		try 
		{
			//if doesn't contain nbt or is legacy do normal spawning
			if(legacySpawnListEntry(entry))
			{
				EntityLiving living = entry.newInstance(w);
				living.setLocationAndAngles(x, y, z, living.rotationYaw, living.rotationPitch);
				living.onInitialSpawn(w.getDifficultyForLocation(new BlockPos(x,y,z)), (IEntityLivingData)null);
				w.spawnEntity(living);
				return true;
			}
			SpawnListEntryAdvanced advanced = (SpawnListEntryAdvanced)entry;
			NBTTagCompound compound = advanced.NBT;
			compound.setString("id", advanced.loc.toString());
			Entity e = getEntityJockey(compound,w,x,y,z,true,true);
			return e != null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Doesn't force nbt if you don't need it to unlike vanilla this is the forum of the /summon command
	 * silkspawners eggs will support multiple indexes but, not to this extent not requiring recursion use only when fully supporting new format
	 */
	public static Entity getEntityJockey(NBTTagCompound compound,World worldIn, double x, double y, double z,boolean useInterface,boolean attemptSpawn) 
	{	
		return getEntityJockey(compound,worldIn,x,y,z,useInterface,attemptSpawn,null);
	}
	public static Entity getEntityJockey(NBTTagCompound compound,World worldIn, double x, double y, double z,boolean useInterface,boolean attemptSpawn,MobSpawnerBaseLogic logic) 
	{	
        Entity entity = getEntity(compound,worldIn,new BlockPos(x,y,z),useInterface,logic);
        if(entity == null)
        	return null;
        
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
        
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
                 Entity entity1 = getEntityJockey(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z,useInterface,attemptSpawn,logic);
                  if (entity1 != null)
                  {
                      entity1.startRiding(entity, true);
                  }
             }
        }

       return entity;
	}

	/**
	 * first index is to determine if your on the first part of the opening of the nbt if so treat nbt like normal with forge support
	 */
	public static Entity getEntity(NBTTagCompound nbt,World world,BlockPos pos,boolean useInterface,MobSpawnerBaseLogic logic) 
	{
		Entity e = null;
		if(getEntityProps(nbt) > 0)
		{
			e = EntityUtil.createEntityFromNBTQuietly(new ResourceLocation(nbt.getString("id")), nbt, world);
		}
		else
		{
			e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
			if(e instanceof EntityLiving && useInterface)
			{
				if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn((EntityLiving)e, world, JavaUtil.castFloat(e.posX), JavaUtil.castFloat(e.posY), JavaUtil.castFloat(e.posZ), logic))
				{
					((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
				}
			}
		}
		return e;
	}
	
	private static int getEntityProps(NBTTagCompound nbt) 
	{
		if(nbt == null)
			return 0;
		int size = nbt.getSize();
		if(nbt.hasKey("Passengers"))
			size--;
		if(nbt.hasKey("id"))
			size--;
		return size;
	}

	public static boolean legacySpawnListEntry(SpawnListEntry entry) throws Exception 
	{
		if(!(entry instanceof SpawnListEntryAdvanced) || ((SpawnListEntryAdvanced)entry).NBT == null)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Gets Entity's NBT
	 */
	public static NBTTagCompound getEntityNBT(Entity e)
	{
		return e.writeToNBT(new NBTTagCompound());
	}
	
	
	//Returns true for survival mode unless debug mode is on
	public static boolean isSurvival(EntityPlayer player) 
	{
		if(player == null || player.capabilities.isCreativeMode|| !player.capabilities.allowEdit)
			return false;
		return true;
	}
	
	public static List<EntityDefintions.EntityInfo> getInfos(Entity e)
	{
		List<EntityDefintions.EntityInfo> list = new ArrayList();
		if(isLiving(e))
			list.add(EntityInfo.living);
		if(isOnlyBase(e))
			list.add(EntityInfo.entitybase);
		if(isNonLiving(e))
			list.add(EntityInfo.nonliving);
		if(isItem(e))
			list.add(EntityInfo.item);
		if(isProjectile(e))
			list.add(EntityInfo.projectile);
		if(isMultiPart(e))
			list.add(EntityInfo.multiPart);
		if(isMonster(e))
			list.add(EntityInfo.hostile);
		if(isPassive(e))
			list.add(EntityInfo.passive);
		if(isPeacefull(e))
			list.add(EntityInfo.peacefull);
		
		return list;
	}

	/**
	 * get the entity types to do stuff with it. note multiple types might be thrown into the array
	 */
	public static List<EntityDefintions.EntityType> getTypes(Entity e)
	{
		List<EntityDefintions.EntityType> list = new ArrayList();
		if(isFire(e))
			list.add(EntityType.fire);
		if(isWater(e))
			list.add(EntityType.water);
		if(isFlying(e))
			list.add(EntityType.flying);
		if(isMonster(e))
			list.add(EntityType.monster);
		if(isBoss(e))
			list.add(EntityType.boss);
		if(isCreature(e))
			list.add(EntityType.creature);
		if(isAmbient(e))
			list.add(EntityType.ambient);
		if(isAreaCloud(e))
			list.add(EntityType.areaeffectcloud);
		if(isTameable(e))
			list.add(EntityType.tameable);
		if(isRanged(e))
			list.add(EntityType.ranged);
		if(isEnder(e))
			list.add(EntityType.ender);
		if(isNPC(e))
			list.add(EntityType.npc);
		
		return list;
	}

	/**
	 * gets the array list of types then returns them in an order which makes since
	 */
	public static String getColor(Entity e)
	{
		List<EntityType> types = getTypes(e);
		if(types.contains(EntityType.boss))
			return EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.BOLD;
		else if(types.contains(EntityType.tameable))
			return EnumChatFormatting.DARK_BLUE;
		else if(types.contains(EntityType.ender))
			return EnumChatFormatting.DARK_PURPLE;
		else if(types.contains(EntityType.areaeffectcloud))
			return EnumChatFormatting.DARK_AQUA;
		else if(types.contains(EntityType.flying) && !(e instanceof EntityBat) && !(e instanceof EntityBlaze))
			return EnumChatFormatting.YELLOW;
		else if(types.contains(EntityType.ambient))
			return EnumChatFormatting.DARK_GRAY;
		else if(types.contains(EntityType.ranged) && e instanceof IMob)
			return EnumChatFormatting.DARK_RED;
		else if(types.contains(EntityType.fire))
			return EnumChatFormatting.GOLD;
		else if(types.contains(EntityType.water))
			return EnumChatFormatting.AQUA;
		else if(types.contains(EntityType.monster))
			return EnumChatFormatting.RED;
		else if(types.contains(EntityType.npc) || e instanceof EntityGolem)
			return EnumChatFormatting.GREEN;
		else if(types.contains(EntityType.creature))
			return EnumChatFormatting.LIGHT_PURPLE;
		
		return EnumChatFormatting.WHITE;
	}
	
	public static boolean isNPC(Entity e) 
	{
		return e instanceof INpc || e instanceof IMerchant;
	}
	
	@Deprecated
	public static boolean isPeacefull(Entity e) 
	{
		return !isMonster(e) && !isPassive(e);
	}
	
	@Deprecated
	public static boolean isPassive(Entity e) 
	{
		return e instanceof EntityPigZombie || e instanceof EntityWolf || e instanceof EntityPolarBear;
	}

	public static boolean isMultiPart(Entity e) 
	{
		return e instanceof IEntityMultiPart;
	}

	public static boolean isProjectile(Entity e) 
	{
		return e instanceof Entity && e instanceof IProjectile || e instanceof EntityFireball || e instanceof EntityShulkerBullet || e instanceof EntityEnderEye || e instanceof EntityFireworkRocket;
	}

	public static boolean isOnlyBase(Entity e)
	{
		return e instanceof EntityLivingBase && !(e instanceof EntityLiving);
	}

	public static boolean isNonLiving(Entity e)
	{
		return !(e instanceof EntityLivingBase);
	}

	public static boolean isLiving(Entity e) 
	{
		return e instanceof EntityLiving;
	}

	public static boolean isItem(Entity e) 
	{
		return e instanceof EntityItem;
	}
	
	public static boolean isEnder(Entity e) 
	{
		ResourceLocation loc = EntityUtil.getEntityResourceLocation(e);
		return EntityUtil.end_ents.contains(loc);
	}

	public static boolean isRanged(Entity e)
	{
		return e instanceof IRangedAttackMob;
	}

	public static boolean isTameable(Entity e) 
	{
		return e instanceof EntityTameable || e instanceof AbstractHorse;
	}

	public static boolean isAreaCloud(Entity e)
	{
		return e instanceof EntityAreaEffectCloud;
	}

	public static boolean isAmbient(Entity e) 
	{
		boolean ambient = e.isCreatureType(EnumCreatureType.AMBIENT, false) || e instanceof EntityAmbientCreature;
		return ambient;
	}

	public static boolean isCreature(Entity e)
	{
		boolean creature = e.isCreatureType(EnumCreatureType.CREATURE, false) || e instanceof IAnimals || e instanceof EntityCreature;
		return creature;
	}
	
	public static boolean isMonster(Entity e) 
	{
		boolean monster = e.isCreatureType(EnumCreatureType.MONSTER, false);
		if(!monster)
			return e instanceof IMob;
		return true;
	}
	
	public static boolean isBoss(Entity e)
	{
		return e instanceof EntityWither || e instanceof EntityDragon || e instanceof EntityElderGuardian;
	}
	
	public static boolean isFlying(Entity e) 
	{
		return e instanceof EntityFlying || e instanceof net.minecraft.entity.EntityFlying || e instanceof EntityBat || e instanceof EntityBlaze || e instanceof EntityVex;
	}

	public static boolean isFire(Entity e) 
	{
		return e.isImmuneToFire();
	}

	public static boolean isWater(Entity e) 
	{
		boolean water = e.isCreatureType(EnumCreatureType.WATER_CREATURE, false) || e instanceof EntityGuardian || e instanceof EntityWaterMob;
		if(!water && e instanceof EntityLivingBase)
		{
			water = ((EntityLivingBase)e).canBreatheUnderwater();
		}
		return water;
	}
	
	/**
	 * Currently Used for only Item Mob Spawners
	 * Only use on client side shadows don't exists on server side for some reason
	 */
	public static float getScaleBasedOnShadow(Entity e,float scale)
	{
		float shadowSize = getShadowSize(e);//isn't object oriented is bound to change via 1.7.10 backport
		float f1 = scale;//0.4375F;
        if(shadowSize > 1.5 && shadowSize < 5.0)
            f1 = 0.20F;//0.20F
        if(shadowSize >= 5.0 && shadowSize < 8.0)
        	f1 = 0.125F;
        if(shadowSize >= 8.0)
        	f1 = 0.09F;
        
        return f1;
	}
	/**
	 * since mods almost never overrode this method we should be fine
	 * supports vanilla wither like 1.7.10
	 */
	@Deprecated
	public static float getShadowSize(Entity e) 
	{
		if(e instanceof EntityWither)
		{
			return e.height / 8.0F;
		}
		return e.height / 2.0F;
	}
    /**
     * Returns a copy of the current entity object from nbt. Doesn't set locations or angles just copies from NBT
     * no jockey support
     */
	public static Entity copyEntity(Entity ent,World w) 
	{
		String str = EntityUtil.getEntityString(ent);
		if(ent_blacklist.contains(str))
			return null;
		if(ent_blacklist_nbt.contains(str))
			return EntityUtil.createEntityByNameQuietly(new ResourceLocation(str), w);
		
		NBTTagCompound nbt = EntityUtil.getEntityNBT(ent);
		nbt.removeTag("UUIDMost");
		nbt.removeTag("UUIDLeast");
		nbt.setString("id", EntityList.getEntityString(ent));
		return EntityUtil.createEntityFromNBTQuietly(new ResourceLocation(str), nbt, w,true);
	}
	
	public static boolean isEntityOnFire(Entity ent) 
	{
		NBTTagCompound nbt = getEntityNBT(ent);
		return nbt.getInteger("Fire") > 0;
	}
	
	public static boolean entityHasPumkin(Entity ent) 
	{
		NBTTagList list = getEntityNBT(ent).getTagList("ArmorItems", 10);
		if(list.tagCount() < 4)
			return false;
		return list.getCompoundTagAt(3).getString("id").equals("minecraft:pumpkin");
	}
	
	public static void cacheEnts()
	{
		cacheEnts((List)null,LoaderMain.fake_world,true);
	}
	
	public static void cacheEnts(Set<ResourceLocation> set,World w)
	{
		List<ResourceLocation> list = JavaUtil.asList(set);
		cacheEnts(list,w,false);
	}
	
	/**
	 * Adds a basic level cache from loc to entity without interface
	 * This also checks for broken entities, logs them and then stores their locs in array lists
	 */
	public static void cacheEnts(List<ResourceLocation> list,World world,boolean printLists)
	{
		long time = System.currentTimeMillis();
		if(cached && list == null)
			return;
		if(list == null)
			list = JavaUtil.asList(EntityList.getEntityNameList());
		
		for(EnumCreatureType type : EnumCreatureType.values())
			cacheEndEnts(Biomes.SKY.getSpawnableList(type));
		
		for(ResourceLocation loc : list)
		{
			if(Config.cacheEntDeny.contains(loc.getResourceDomain()) || Config.cacheEntNamesDeny.contains(loc))
			{
				ent_blacklist.add(loc);
				LoaderMain.logger.log(Level.INFO,"Skipping blacklisted entity:" + loc);
				continue;
			}
			Class clazz = EntityList.getClass(loc);
			if(clazz == null)
			{
				LoaderMain.logger.log(Level.ERROR,"Skipping Broken Entity No Class Found Report to mod autoher:" + loc);
				continue;
			}
			boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
			boolean isInterface = Modifier.isInterface(clazz.getModifiers());
			//not a real entity if is interface or abstract so don't even blacklist it
			if(isAbstract || isInterface)
				continue;
			try 
			{
				Constructor k = clazz.getConstructor(new Class[] {World.class});
			} 
			catch (Throwable t)
			{
				ent_blacklist.add(loc);
				LoaderMain.logger.log(Level.ERROR,"Skipping Broken Entity No Default World Constructor Report to mod autoher:" + loc);
				continue;
			}
			Entity e = EntityUtil.createEntityByNameQuietly(loc, world,true);
			if(e instanceof EntityEndermite || e instanceof EntityShulker)
				end_ents.add(loc);
			
			boolean living = e instanceof EntityLiving; 
			boolean base = e instanceof EntityLivingBase && !(e instanceof EntityLiving);
			boolean nonliving = !(e instanceof EntityLivingBase);

			String translation = TransLateEntity(e, world);
			if(e == null || translation == null)
			{
				ent_blacklist.add(loc);//Entity failed cache it's string id for debugging
				LoaderMain.logger.log(Level.ERROR,"Skipping Broken Entity Creation/Translation Failed Report to mod autoher:" + loc);
				continue;
			}
			ent_blacklist.remove(loc);
			NBTTagCompound tag = getEntityNBTSafley(e);
//			cacheWorldNeedy(loc);
			cacheNBTMob(loc,e,tag);
			getcommandSenderName(e);//forces it to error if it is going to
			cacheForgeMob(loc);//is depreciated so I know to change it when backporting
			
			if(living)
			{
				try
				{
					((EntityLiving)e).onInitialSpawn(e.world.getDifficultyForLocation(new BlockPos(0,4,0)), (IEntityLivingData)null);
				}
				catch(Throwable t)
				{
					ent_blacklist.add(loc);
					LoaderMain.logger.log(Level.ERROR,"Skipping broken Entity Failed to read onInitialSpawn() aka onSpawnWithEgg() Report to mod author:" + loc);
				}
			}
			if(!ent_blacklist.contains(loc) && !ent_blacklist_nbt.contains(loc))
			{
				try
				{
					String[] names = new String[3];
					names[0] = EntityUtil.getUnlocalizedName(e);
					names[1] = I18n.translateToLocal(names[0]);
					names[2] = EntityUtil.getColor(e);
				
				if(living)
					EntityUtil.living.put(loc,names);
				else if(base)
					EntityUtil.livingbase.put(loc,names);
				else if(nonliving)
					EntityUtil.nonliving.put(loc,names);
				}
				catch(Throwable t)
				{
					System.out.println("An Entity has thrown an exception when trying to get it's colored text/translating unlocal name:" + loc);
					ent_blacklist.add(loc);
					continue;
				}
			}
		}
		
		EntityUtil.living = orderList(EntityUtil.living);
		EntityUtil.livingbase = orderList(EntityUtil.livingbase);
		EntityUtil.nonliving = orderList(EntityUtil.nonliving);
		
		JavaUtil.printTime(time, "Entity Util Cached Ents:");
		
		if(printLists)
		{
			System.out.println("blacklist:" + ent_blacklist);
			System.out.println("blacklistNBT:" + ent_blacklist_nbt);
			System.out.println("blacklist CMD:" + ent_blacklist_commandsender);
			if(Config.debug)
				System.out.println("worldNeedyMobs:" + ents_worldneedy);
		}
		
		cached = true;
	}
	public static void cacheEndEnts(List<Biome.SpawnListEntry> mr_renchen_dies)
    {
		for (Biome.SpawnListEntry b : mr_renchen_dies)
	  	{
	  		ResourceLocation loc = EntityList.getKey(b.entityClass);
	  		EntityUtil.end_ents.add(loc);
	  	}
    }

	/**
	 * orders list using a custom comparator comparing the [1] index in the value of the hashmap
	 */
	public static HashMap orderList(HashMap<ResourceLocation, String[]> map) 
	{
		List list = new LinkedList(map.entrySet());
		Comparator custom = new Comparator()
		{
	          public int compare(Object obj1, Object obj2) 
	          {
	        	  Map.Entry<Object,String[]> entry1 = (Map.Entry<Object,String[]>)obj1;
	        	  Map.Entry<Object,String[]> entry2 = (Map.Entry<Object,String[]>)obj2;
	        	  String trans1 = entry1.getValue()[1];
	        	  String trans2 = entry2.getValue()[1];
	        	  return trans1.compareTo(trans2);  
	          }
		};
		Collections.sort(list,custom);
		
	     HashMap sortedHashMap = new LinkedHashMap();
	     for (Iterator it = list.iterator(); it.hasNext();) 
	     {
	            Map.Entry entry = (Map.Entry) it.next();
	            sortedHashMap.put(entry.getKey(), entry.getValue());
	     } 
	     return sortedHashMap;
	}

	/**
	 * Is allowed to return null, however it null just means it failed
	 * @param e
	 * @return
	 */
	public static NBTTagCompound getEntityNBTSafley(Entity e) 
	{
		if(e == null)
			return null;
		try
		{
			NBTTagCompound nbt = EntityUtil.getEntityNBT(e);
			return nbt;
		}
		catch(Throwable t)
		{
			return null;
		}
	}

	public static void cacheNBTMob(ResourceLocation loc,Entity e, NBTTagCompound tag) 
	{
		if(tag == null)
		{
			ent_blacklist_nbt.add(loc);
			LoaderMain.logger.log(Level.ERROR,"Entity Serialization Has Been Broken When Reading It's Own NBT Report to mod autoher:" + loc);
			return;
		}
		try
		{
			e.readFromNBT(tag);
			ent_blacklist_nbt.remove(loc);
		}
		catch(Throwable t)
		{
			ent_blacklist_nbt.add(loc);
			LoaderMain.logger.log(Level.ERROR,"Entity Serialization Has Been Broken When Reading It's Own NBT Report to mod autoher:" + loc);
		}
	}

	/**
	 * Warning is bound to change in backported versions
	 */
	@Deprecated
	public static void cacheForgeMob(ResourceLocation loc) 
	{
		if(!loc.getResourceDomain().equals("minecraft"))
			forgemobs.add(loc);
	}

	public static void cacheWorldNeedy(ResourceLocation loc) 
	{
		if(Config.debug_worldNeedy && EntityUtil.createEntityByNameQuietly(loc, null,true) == null)
		{
			ents_worldneedy.add(loc);
		}
	}
	
	/**
	 * only do the object will reset on next player.readFromNBT() use only use this if you know what you are doing
	 */
	public static void setEntityUUID(Entity player,UUID uuid) 
	{
		player.entityUniqueID = uuid;
		player.cachedUniqueIdString = uuid.toString();
	}
	
	public static void disableFire(Entity e)
	{
		e.extinguish();
		try
		{
			LoaderFields.setFlag.setAccessible(true);
			LoaderFields.setFlag.invoke(e, 0, false);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	public static float getRotationYaw(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist3 = nbt.getTagList("Rotation", 5);
		return nbttaglist3.getFloatAt(0);
	}
	public static float getRotationPitch(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist3 = nbt.getTagList("Rotation", 5);
		return nbttaglist3.getFloatAt(1);
	}

}
