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
import com.evilnotch.lib.util.line.Line;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

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
	
	/**
	 * used on player login so it doesn't parse twice will self empty login complete so don't expect data to be here long
	 */
    public static HashMap<UUID,NBTTagCompound> nbts = new HashMap();
    
	public static String getUnlocalizedName(NBTTagCompound data, World w) 
	{
		data = data.copy();
		ResourceLocation loc = new ResourceLocation(data.getString("id"));
		return getUnlocalizedName(EntityUtil.createEntityFromNBTQuietly(loc, data, w));
	}
    
    public static String getUnlocalizedName(Entity e)
    {
    	return "entity." + EntityList.getEntityString(e) + ".name";
    }
	
	public static String TransLateEntity(NBTTagCompound nbt,World w)
	{
	   nbt = nbt.copy();
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
    	if(entity == null || w == null)
    		return null;
    	ResourceLocation loc = getEntityResourceLocation(entity);
    	String strentity = translateEntityCmd(entity,w);
    	
    	//if cmd fails try general name
    	if(strentity == null || isMistranslated(strentity) )
    	{
    		String generalName = translateEntityGeneral(entity,w);
    		//don't check mistranslation of new string since the strentity can be null or is mistranslated to begin with
    		if(generalName != null)
    			strentity = generalName;
    	}
    	if(strentity == null)
    		return null;//if both fail to return null return before a null point exception happens
    	
    	//if general translation fails and cmd fails get rid of the ugly failed text
    	if(isMistranslated(strentity))
    		strentity = strentity.substring(7, strentity.length()-5 );

    	return strentity;
    }
    public static boolean isMistranslated(String str) {
		if(str == null || str.startsWith("entity.") && str.endsWith(".name"))
			return true;
		return false;
	}

	/**
	 * Translates general name for entity so pink sheep will return sheep
	 * Has the entity.entityNameHere.name removed if not translated properly
	 */
	public static String translateEntityGeneral(Entity entity,World world)
	{
		if(entity == null)
			return null;
	   String EntityName = EntityList.getEntityString(entity);
	   try{
		EntityName = I18n.translateToLocal("entity." + EntityName + ".name");
	   }catch(Throwable t){return null;}
	    
	   return EntityName;
	}
	/**
	 * get command sender name and returns null if vanilla does it's funky general thing
	 */
	public static String translateEntityCmd(Entity entity, World world)
	{
		String name = getcommandSenderName(entity);
		if(name != null)
		{
			if(name.equals("generic") || name.equals("entity." + "generic" + ".name") )
				return null;
		}
		return name;
	}
	
	public static String getcommandSenderName(Entity entity) 
	{
		try{
			if(entity == null)
				return null;
			String name = entity.getName();
			if(name != null)
				ent_blacklist_commandsender.remove(EntityUtil.getEntityResourceLocation(entity));
			return name;
		}catch(Throwable t){
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
	   try{
		   Entity e = createEntityByNameQuietly(loc,worldIn,constructor);
		   if(e != null)
			   e.readFromNBT(nbt);
		   return e;
	  	}catch(Throwable e){}
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
    		try{
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
			return entry.getRegistryName();
		return null;
	}
    
    public static void printChat(EntityPlayer player,String c_player, String c_msg, String messege)
	{
		player.sendMessage(new TextComponentString(c_player + player.getName() + " " + c_msg + messege) );
	}
    public static void sendURL(EntityPlayer p ,String messege,String url)
    {
    	TextComponentString str = new TextComponentString(EnumChatFormatting.AQUA + messege + " " + EnumChatFormatting.BLUE + EnumChatFormatting.UNDERLINE + url);
    	str.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    	p.sendMessage(str);
    }
    
    public static void sendClipBoard(String pc,String c,EntityPlayer p ,String messege,String url)
    {
    	sendClipBoard(pc,c,p,messege,url,true);
    }
    public static void sendClipBoard(String pc,String c,EntityPlayer p ,String messege,String url,boolean copyURL)
    {
    	TextComponentString str = new TextComponentString(pc + messege + " " + c + EnumChatFormatting.UNDERLINE + url);
    	str.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, url));
    	p.sendMessage(str);
    	if(copyURL)
    		sendToClientClipBoard(p,url);
    }
    
    public static void sendToClientClipBoard(EntityPlayer p,String message)
    {
    	if(p instanceof EntityPlayerMP)
    	{
            PacketClipBoard packet = new PacketClipBoard(message);
            NetWorkHandler.INSTANCE.sendTo(packet, (EntityPlayerMP)p);
    	}
    	else
    	{
    		JavaUtil.writeToClipboard(message, null);
    	}
    }
    /**
     * Spawn Entity by spawnlistentry from Scratch
     */
	public static boolean spawnEntityEntry(World w, SpawnListEntry entry,double x, double y, double z) {
		try {
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
		   NBTTagCompound compound = advanced.NBT.copy();
		   compound.setString("id", advanced.loc.toString());
		   Entity e = getEntityJockey(compound,w,x,y,z,true,true);
		   return e != null;
		} catch (Exception e) {
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
	public static Entity getEntity(NBTTagCompound nbt,World world,BlockPos pos,boolean useInterface,MobSpawnerBaseLogic logic) {
		Entity e = null;
		if(getEntityProps(nbt).getSize() > 0)
			e = EntityUtil.createEntityFromNBTQuietly(new ResourceLocation(nbt.getString("id")), nbt, world);
		else{
			e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
			if(e instanceof EntityLiving && useInterface)
				if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn((EntityLiving)e, world, JavaUtil.castFloat(e.posX), JavaUtil.castFloat(e.posY), JavaUtil.castFloat(e.posZ), logic))
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

	private static boolean legacySpawnListEntry(SpawnListEntry entry) throws Exception {
		if(!(entry instanceof SpawnListEntryAdvanced) || ((SpawnListEntryAdvanced)entry).NBT == null )
			return true;
		return false;
	}
	public static String getEntityString(Entity e)
	{
		return EntityList.getEntityString(e);
	}
	
	/**
	 * Gets Entity's NBT
	 */
	public static NBTTagCompound getEntityNBT(Entity e)
	{
		if(e == null)
			return null;
		
		return e.writeToNBT(new NBTTagCompound());
	}
	
	public static File getPlayerFile(EntityPlayer player,boolean uuid)
	{
		if(uuid)
			return new File(VanillaBugFixes.playerDataDir,player.getUniqueID().toString() + ".dat");
		else
			return new File(VanillaBugFixes.playerDataNames,player.getName() + ".dat");
	}
	public static File getPlayerFile(String username,boolean uuid)
	{
		if(uuid)
			return new File(VanillaBugFixes.playerDataDir,username + ".dat");
		else
			return new File(VanillaBugFixes.playerDataNames,username + ".dat");
	}
	
	/**
	 * Returns the uuidFile or cached file based on uuid boolean
	 * @param player
	 * @param uuid
	 * @return player file
	 */
	public static File getPlayerFileSafley(EntityPlayer player,boolean uuid)
	{
		File file = getPlayerFile(player,uuid);
		if(uuid)
		{
			if(!file.exists())
			{
				NBTTagCompound nbt = EntityUtil.getEntityNBT(player);
				updatePlayerFile(file,nbt);
			}
			return file;
		}
		else
		{
			if(!file.exists())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("uuid", player.getUniqueID().toString() );
				updatePlayerFile(file,nbt);
			}
			return file;
		}
	}
	public static File getPlayerFileNameSafley(GameProfile profile)
	{
		File file = getPlayerFile(profile.getName(),false);
		if(!file.exists())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("uuid", profile.getId().toString() );
			updatePlayerFile(file,nbt);
		}
		return file;
	}
	/**
	 * Update Player file
	 */
	public static void updatePlayerFile(File file, NBTTagCompound nbt) 
	{
		NBTUtil.updateNBTFileSafley(file,nbt);
	}
	
	public static NBTTagCompound getPlayerFileNBT(String display,EntityPlayerMP player,boolean uuidDir) 
	{
		return getPlayerFileNBT(display,player.mcServer.getPlayerList(),uuidDir);
	}
	/**
	 * Gets cached playerdata from filename don't call unless you have the exact string
	 */
	public static NBTTagCompound getPlayerFileNBT(String display,PlayerList playerlist,boolean uuidDir) 
	{
		FileInputStream stream = null;
		NBTTagCompound nbt = null;
		try
		{
			stream = !uuidDir ? new FileInputStream(new File(VanillaBugFixes.playerDataNames,display + ".dat")) : new FileInputStream(new File(VanillaBugFixes.playerDataDir,display + ".dat"));
			nbt = CompressedStreamTools.readCompressed(stream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			nbt = null;
		}
		finally
		{
			if(stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					System.out.println("unable to close input stream for player:" + display + ".dat");
				}
			}
		}
		//fix player's nbt
		if(nbt != null)
		{
			nbt = getFixedPlayerNBT(playerlist,nbt);
		}
		return nbt;
	}
	
	private static NBTTagCompound getFixedPlayerNBT(PlayerList playerlist,NBTTagCompound nbt) {
		SaveHandler handler = getPlayerDataManager(playerlist);
		nbt = getDataFixer(handler).process(FixTypes.PLAYER, nbt);
		return nbt;
	}

	public static SaveHandler getPlayerDataManager(PlayerList playerList) 
	{
		return (SaveHandler) playerList.playerDataManager;
	}
	
	public static DataFixer getDataFixer(SaveHandler handler) 
	{
		return (DataFixer) handler.dataFixer;
	}
	
	//Returns true for survival mode unless debug mode is on
	public static boolean isSurvival(EntityPlayer player) 
	{
		if(player == null || player.capabilities.isCreativeMode|| !player.capabilities.allowEdit)
			return false;
		return true;
	}
	/**
	 * returns entitymodid:entitymodname
	 * it is legacy do not use unless you know what you are doing
	 */
	@Deprecated
	public static Line getEntityMod(Entity entity)
	{
	   	String modName = "";
	   	String modid = "";
	   	try{
	   		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
	   		ModContainer modC = er.getContainer();
	   		modName = modC.getName();
	   		modid = modC.getModId();
	   	} catch (NullPointerException e){
	   		modName = "Minecraft";
    		modid = "minecraft";
    	}
		return new Line("\"" + modid + ":" + modName + "\"");
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
	private static boolean isNPC(Entity e) {
		return e instanceof INpc || e instanceof IMerchant;
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
	@Deprecated
	public static boolean isPeacefull(Entity e) {
		return !isMonster(e) && !isPassive(e);
	}
	@Deprecated
	public static boolean isPassive(Entity e) {
		return e instanceof EntityPigZombie || e instanceof EntityWolf || e instanceof EntityPolarBear;
	}

	public static boolean isMultiPart(Entity e) {
		return e instanceof IEntityMultiPart;
	}

	public static boolean isProjectile(Entity e) 
	{
		return e instanceof Entity && e instanceof IProjectile || e instanceof EntityFireball || e instanceof EntityShulkerBullet || e instanceof EntityEnderEye || e instanceof EntityFireworkRocket;
	}

	public static boolean isOnlyBase(Entity e) {
		return e instanceof EntityLivingBase && !(e instanceof EntityLiving);
	}

	public static boolean isNonLiving(Entity e) {
		return !(e instanceof EntityLivingBase);
	}

	public static boolean isLiving(Entity e) {
		return e instanceof EntityLiving;
	}

	public static boolean isItem(Entity e) {
		return e instanceof EntityItem;
	}
	public static boolean isEnder(Entity e) {
		ResourceLocation loc = EntityUtil.getEntityResourceLocation(e);
		if(loc == null)
			return false;
		return EntityUtil.end_ents.contains(loc);
	}

	public static boolean isRanged(Entity e) {
		return e instanceof IRangedAttackMob;
	}

	public static boolean isTameable(Entity e) {
		return e instanceof EntityTameable || e instanceof AbstractHorse;
	}

	public static boolean isAreaCloud(Entity e) {
		return e instanceof EntityAreaEffectCloud;
	}

	public static boolean isAmbient(Entity e) {
		boolean ambient = e.isCreatureType(EnumCreatureType.AMBIENT, false) || e instanceof EntityAmbientCreature;
		return ambient;
	}

	public static boolean isCreature(Entity e){
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
	private static boolean isFlying(Entity e) 
	{
		return e instanceof EntityFlying || e instanceof net.minecraft.entity.EntityFlying || e instanceof EntityBat || e instanceof EntityBlaze || e instanceof EntityVex;
	}

	private static boolean isFire(Entity e) {
		return e.isImmuneToFire();
	}

	public static boolean isWater(Entity e) {
		boolean water = e.isCreatureType(EnumCreatureType.WATER_CREATURE, false) || e instanceof EntityGuardian || e instanceof EntityWaterMob;
		if(!water && e instanceof EntityLivingBase)
		{
			water = ((EntityLivingBase)e).canBreatheUnderwater();
		}
		return water;
	}
	
	//Prints Colored Chat from player
	public static void printChat(EntityPlayer player, String color, EnumChatFormatting colormsg, String messege)
	{
		player.sendMessage(new TextComponentString(color + player.getDisplayName() + " " + colormsg + messege));
	}
	/**
	 * Currently Used for only Item Mob Spawners
	 * Only use on client side shadows don't exists on server side for some reason
	 */
	public static float getScaleBasedOnShadow(Entity e,float scale)
	{
		float shadowSize = getShadowSize(e);//isn't object oriented is bound to change via 1.7.10 backport
		
		if(e == null)
			return 0.0F;
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
		if(ent == null || EntityUtil.getEntityString(ent) == null)
			return null;
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
		NBTTagList list = getEntityNBT(ent).getTagList("Equipment", 10);
		if(list.tagCount() < 4)
			return false;
		return list.getCompoundTagAt(4).getInteger("id") == 86;
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
			ge(Biomes.SKY.getSpawnableList(type));
		
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
			
			cacheWorldNeedy(loc);
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
	 public static void ge(List<Biome.SpawnListEntry> mr_renchen_dies)
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
	     for (Iterator it = list.iterator(); it.hasNext();) {
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
	public static void cacheForgeMob(ResourceLocation loc) {
		if(!loc.getResourceDomain().equals("minecraft"))
			forgemobs.add(loc);
	}

	public static void cacheWorldNeedy(ResourceLocation loc) 
	{
		try
		{
			Entity e2 = EntityUtil.createEntityByNameQuietly(loc, null,true);
		}
		catch(Throwable t)
		{
			ents_worldneedy.add(loc);
		}
	}
	/**
	 * teleport an entity to spawn across dimenions support
	 */
    public static void teleportSpawn(Entity ep,MinecraftServer server,int dimension) throws WrongUsageException 
    {
    	World w = server.getWorld(dimension);
    	BlockPos bp = w.provider.getRandomizedSpawnPoint();
    	double x = bp.getX() + 0.5;
		double y = bp.getY();
		double z = bp.getZ() + 0.5;
		ep.setPosition(x, y, z);
		
		if(ep.world.provider.getDimension() != dimension)
			ep.setWorld(w);
		ep.world.getChunkFromBlockCoords(bp);

		while (!ep.world.getCollisionBoxes(ep, ep.getEntityBoundingBox()).isEmpty() && ep.posY < 256.0D)
	    {
	    	ep.setPosition(ep.posX, ep.posY + 1.0D, ep.posZ);
	    }
		EntityUtil.telePortEntitySync(ep, server, ep.posX,ep.posY,ep.posZ, ep.rotationYaw, ep.rotationPitch, dimension);
	}
    
	/**
	 * teleport entire stack
	 */
	public static void teleportStack(Entity index,MinecraftServer server,double x, double y, double z, float yaw, float pitch, int traveldim) throws WrongUsageException
	{
		teleportStackAtIndex(index.getLowestRidingEntity(),server,x,y,z,yaw,pitch,traveldim);
	}
	
	/**
	 * used for /tpdim as requested by micah_laster to save all mounts above you
	 */
	public static void teleportStackAtIndex(Entity entity,MinecraftServer server,double x, double y, double z, float yaw, float pitch, int traveldim) throws WrongUsageException
	{
		if(entity.getLowestRidingEntity() != entity)
			entity.dismountRidingEntity();
		
		//get all passengers and riding entities
        List<Entity> list = (ArrayList<Entity>) JavaUtil.toArray(entity.getRecursivePassengers());
        list.add(0,entity);
       
        List<Entity> toRide = new ArrayList();
        for(Entity e : list)
        	toRide.add(e.getRidingEntity());

        for(int i=0;i<list.size();i++)
        {
        	Entity e = list.get(i);
        	e = EntityUtil.telePortEntity(e,server,x,y,z,yaw,pitch,traveldim);//keep yaw/pitch so they all don't face the same way only the base mob
        	setEntity(e,toRide);
        }
        //remount entities then sync players
        for(int i=0;i<list.size();i++)
        {
        	Entity e = list.get(i);
        	Entity other = toRide.get(i);
        	if(other != null)
        		e.startRiding(other,true);
        	
            if (e instanceof EntityPlayerMP) 
            {
               	updatePassengerClient((EntityPlayerMP)e,e);
            }
        }
	}
    public static void updatePassengerClient(EntityPlayerMP playerMP,Entity e) 
    {
    	if(e != null)
    	{
    		System.out.println("Syncing Packets to:" + playerMP.getName() + " with:" + e.getName());
    		playerMP.connection.sendPacket(new SPacketSetPassengers(e));
    	}
	}

	protected static void setEntity(Entity changed, List<Entity> rides) {
		for(int i=0;i<rides.size();i++)
		{
			Entity e = rides.get(i);
			if(e == null)
			{
				continue;
			}
			if(e.getUniqueID().toString().equals(changed.getUniqueID().toString()) && e != changed )
			{
				rides.set(i, changed);
				System.out.println("changed:" + changed.getName());
			}
		}
	}
	
	/**
	 * work around for the shitty cheat detection system in vanilla
	 */
	public static void captureCoords(EntityPlayerMP player)
	{
		NetHandlerPlayServer connection = player.connection;
		connection.captureCurrentPosition();
		
		connection.lowestRiddenEnt = player.getLowestRidingEntity();
		
		connection.lowestRiddenX = player.posX;
		connection.lowestRiddenY = player.posY;
		connection.lowestRiddenZ = player.posZ;
		
		connection.lowestRiddenX1 = player.posX;
		connection.lowestRiddenY1 = player.posY;
		connection.lowestRiddenZ1 = player.posZ;
	}
	
	/**
	 * first parameter is player to teleport
	 * Doesn't use the vanilla teleport system so set xyz's of player before hand of the new world in the portal as well as yaw pitch and maybe head
	 * @throws WrongUsageException 
	 */
	public static Entity telePortEntity(Entity e,MinecraftServer server, double x, double y, double z,float yaw,float pitch, int traveldim) throws WrongUsageException
	{
		if(e.isDead)
		{
			return e;
		}
    	e.dismountRidingEntity();
    	e.removePassengers();
        int prevDim = e.dimension;
    	
        if(traveldim != prevDim)
        {
        	return teleportEntityInterdimentional(e,server,prevDim,traveldim,x,y,z,yaw,pitch);
        }
        return doTeleport(e, x, y, z,yaw,pitch);
	}
    /**
     * This is the black magic responsible for teleporting players between dimensions!
     */
    public static EntityPlayer teleportPlayerInterdimentional(EntityPlayerMP player, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) 
    {
        WorldServer sourceWorld = server.getWorld(sourceDim);
        WorldServer targetWorld = server.getWorld(targetDim);
        PlayerList playerList = server.getPlayerList();
        
        sourceWorld.playerEntities.remove(player);
    	if(sourceDim == 1)
    		removeDragonBars(sourceWorld);//vanilla bug fix 1.9+

        player.dimension = targetDim;
        player.connection.sendPacket(new SPacketRespawn(player.dimension, targetWorld.getDifficulty(), targetWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
        playerList.updatePermissionLevel(player);
        sourceWorld.removeEntityDangerously(player);
        player.isDead = false;

        //region Transfer to world
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        targetWorld.spawnEntity(player);
        targetWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(targetWorld);

        playerList.preparePlayer(player, sourceWorld);
        if(player.posX != xCoord || player.posY != yCoord|| player.posZ != zCoord || player.rotationYaw != yaw || player.rotationPitch != pitch)
        {
        	System.out.println("mod incompatiblity detected overriding it!");
        	player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        }
        EntityUtil.captureCoords(player);
        
        //update yaw/pitch and sync shoulders
        sysncShoulders(player,yaw,pitch);
        
        player.interactionManager.setWorld(targetWorld);
        player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
        
        //additions to brandon's core
        player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
        player.setHealth(player.getHealth());
        
        playerList.updateTimeAndWeatherForPlayer(player, targetWorld);
        playerList.syncPlayerInventory(player);

        for (PotionEffect potioneffect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, sourceDim, targetDim);
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        
        return player;
    }
    public static void sysncShoulders(EntityPlayerMP player, float yaw, float pitch) {
        player.renderYawOffset = yaw;
        player.prevRenderYawOffset = yaw;
        PacketYawOffset packet = new PacketYawOffset(yaw,player.getEntityId());
        PacketYawPitch packet2 = new PacketYawPitch(yaw,pitch,player.getEntityId());
        NetWorkHandler.INSTANCE.sendTo(packet, player);
        NetWorkHandler.INSTANCE.sendToTracking(packet2, player);
	}

	public static Entity teleportEntityInterdimentional(Entity entity, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) throws WrongUsageException 
    {
        if (entity == null || entity.isDead || entity.world == null || entity.world.isRemote) 
        {
            return null;
        }
        
        if(!DimensionManager.isDimensionRegistered(targetDim))
        	throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        
        if(entity instanceof EntityPlayerMP)
        {
        	return teleportPlayerInterdimentional((EntityPlayerMP)entity,server,sourceDim,targetDim,xCoord,yCoord,zCoord,yaw,pitch);
        }
        
        WorldServer sourceWorld = server.getWorld(sourceDim);
        WorldServer targetWorld = server.getWorld(targetDim);

        //hotfix for entities that don't handle onDeath() for what it's made for dropping actual items
        List<ItemStack> stacks = new ArrayList();
        if (!entity.isDead && entity instanceof IInventory) 
        {
           IInventory inventory = (IInventory)entity;
           for(int i=0;i<inventory.getSizeInventory();i++)
           {
        	   ItemStack stack = inventory.getStackInSlot(i);
        	   stacks.add(stack);
           }
           inventory.clear();
        }

        entity.dimension = targetDim;

        sourceWorld.removeEntity(entity);
        entity.isDead = false;
        entity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        sourceWorld.updateEntityWithOptionalForce(entity, false);

        Entity newEntity = EntityList.newEntity(entity.getClass(), targetWorld);
        if (newEntity != null) 
        {
        	try
        	{
        		newEntity.copyDataFromOld(entity);
        		newEntity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        		//hotfix for minecarts and anything else that breaks onDeath() method
        		if(newEntity instanceof IInventory)
        		{
        			IInventory inventory = (IInventory)newEntity;
        			int index = 0;
        			for(ItemStack stack : stacks)
        			{
        				inventory.setInventorySlotContents(index,stack);
        				index++;
        			}
        			inventory.markDirty();
        		}
        		boolean flag = newEntity.forceSpawn;
        		newEntity.forceSpawn = true;
        		targetWorld.spawnEntity(newEntity);
        		newEntity.forceSpawn = flag;
        		targetWorld.updateEntityWithOptionalForce(newEntity, false);
        	}
        	catch(Throwable t)
        	{
        		t.printStackTrace();
        		return null;
        	}
        }

        entity.isDead = true;
        sourceWorld.resetUpdateEntityTick();
        targetWorld.resetUpdateEntityTick();

        return newEntity;
    }
    /**
     * Call this after player is removed from the world
     */
    public static void removeDragonBars(World end) 
    {
		DragonFightManager fightManager = ((WorldProviderEnd)end.provider).getDragonFightManager();
		if(fightManager != null)
		{
			fightManager.updateplayers();
		}
    }
	/**
     *supports teleporting entities from location to location in the same dimension doesn't support actual cross dimensional teleport
     */
    public static Entity doTeleport(Entity e, double x, double y, double z,float yaw, float pitch)
    {	
        int chunkOldX = (int)e.posX >> 4;
 		int chunkOldZ = (int)e.posZ >> 4;
        int chunkX = (int)x >> 4;
		int chunkZ = (int)z >> 4;
		
        if (e instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);
            EntityPlayerMP player = (EntityPlayerMP)e;
            player.setLocationAndAngles(chunkX, y, chunkZ, yaw, pitch);
            player.connection.setPlayerLocation(x, y, z, yaw, pitch, set);
            EntityUtil.captureCoords(player);
            sysncShoulders(player,yaw,pitch);
        }
        else
        {
            e.setLocationAndAngles(x, y, z, yaw, pitch);    
        }
        
        if (!(e instanceof EntityLivingBase) || !((EntityLivingBase)e).isElytraFlying())
        {
            e.motionY = 0.0D;
            e.onGround = true;
        }

        //vanilla hotfix if entity isn't loaded and not added to the chunk do this don't check players because sometimes the world will randomly remove them causing chunks not to load from players
       if(!(e instanceof EntityPlayer))
       {
        	if(!MinecraftUtil.isChunkLoaded(e.world, chunkX, chunkZ, true))
        	{
        		//remove from old chunk
        		if(chunkOldX != chunkX || chunkOldZ != chunkZ)
        		{
        			Chunk chunkOld = e.world.getChunkFromChunkCoords(chunkOldX,chunkOldZ);
        			chunkOld.removeEntity(e);
        		}
        		Chunk chunk = e.world.getChunkFromChunkCoords(chunkX,chunkZ);
        		/*if(!containsEntity(chunk.getEntityLists(),e))
        		{
        			System.out.println("here adding:" + e.getName() + " \"chunk not loaded\"");
        			chunk.addEntity(e);
        		}*/
        	}
        	e.world.updateEntityWithOptionalForce(e, false);
        }
      	
        return e;
    }

	public static boolean containsEntity(ClassInheritanceMultiMap<Entity>[] list,Entity e) 
	{
		for(int i=0;i<list.length;i++)
		{
			ClassInheritanceMultiMap map = list[i];
			Iterator<Entity> it = map.iterator();
			while(it.hasNext())
			{
				Entity e2 = it.next();
				if(e == e2)
					return true;
			}
		}
		return false;
	}

	public static void kickPlayer(EntityPlayerMP p, int ticks,String msg) 
	{
		LibEvents.kicker.put(p.connection, new PointId(0,ticks,msg) );
	}

	/**
	 * call remove player 1 if your checking this during player logout event since it doesn't get removed from the world as of yet
	 * unused as I now use isPlayerOwner leaving the server and is client
	 */
	public static boolean isWorldsEmptyPlayers(World w,boolean removePlayer1) 
	{
		int size = w.playerEntities.size();
		if(removePlayer1)
			size--;
		
		if(size == 0)
		{
			for(World world : DimensionManager.getWorlds())
			{
				if(world.playerEntities.size() > 0 && w.provider.getDimension() != world.provider.getDimension())
					return false;
			}
			return true;
		}
		return false;
	}

	public static boolean isPlayerOwner(EntityPlayerMP player) {
		if(!LoaderMain.isClient)
			return false;
		return player.getName().equals(player.getServer().getServerOwner());
	}

	public static TextComponentTranslation msgShutdown = null;
	public static void disconnectPlayer(EntityPlayerMP player,TextComponentString msg) 
	{	
		if(isPlayerOwner(player))
		{
			player.mcServer.initiateShutdown();
			msgShutdown = new TextComponentTranslation(msg.getText(),new Object[0]);
		}
		else
		{
			player.connection.disconnect(new TextComponentTranslation(msg.getText(),new Object[0]) );
		}
	}

	public static ItemStack getActiveItemStack(EntityPlayer p,EnumHand hand) 
	{
		return p.getHeldItem(hand);
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

	public static EntityPlayer getPlayer(String key) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getPlayerList().getPlayerByUsername(key);
	}

	/**
	 * set the game type with keeping previous capabilities
	 */
	public static void setGameTypeSafley(EntityPlayerMP p, GameType gameType) 
	{
		NBTTagCompound nbt = new NBTTagCompound();
		p.capabilities.writeCapabilitiesToNBT(nbt);
	    p.setGameType(gameType);
	    p.capabilities.readCapabilitiesFromNBT(nbt);
	    p.sendPlayerAbilities();
	}

	/**
	 * hides the player for all users
	 */
	public static void hidePlayer(EntityPlayerMP p) {
	       p.getServerWorld().getEntityTracker().removePlayerFromTrackers(p);
	       p.getServerWorld().getEntityTracker().untrack(p);
	}
	/**
	 * shows player for all users
	 */
	public static void showPlayer(EntityPlayerMP p) {
	       p.getServerWorld().getEntityTracker().track(p);
	}
	
	public static void broadCastMessege(String msg) 
	{
		LibEvents.msgs.add(msg);
	}
	/**
	 * only do the object will reset on next player.readFromNBT() use only use this if you know what you are doing
	 */
	public static void setEntityUUID(Entity player,UUID uuid) 
	{
		player.entityUniqueID = uuid;
		player.cachedUniqueIdString = uuid.toString();
	}
	/**
	 * this alters both the uuid of the player object and the gameprofile uuid
	 */
	public static void setPlayerUUID(EntityPlayer player,UUID uuid) 
	{
		ReflectionUtil.setFinalObject(player.getGameProfile(), uuid, GameProfile.class, "id");
		setEntityUUID(player,uuid);
	}

	public static UUID getServerPlayerUUID(GameProfile profile) 
	{
		File file = EntityUtil.getPlayerFileNameSafley(profile);//updates player file synced to uuid on login
		NBTTagCompound nbt = NBTUtil.getFileNBT(file);
		return UUID.fromString(nbt.getString("uuid"));
	}

    public static void patchUUID(GameProfile gameprofile) 
    {
    	long time = System.currentTimeMillis();
        //set the inital value of the player's uuid to what it's suppose to be
        UUID init = EntityPlayer.getUUID(gameprofile);
        
        UUID actual = EntityUtil.getServerPlayerUUID(gameprofile);
        System.out.println("Checking UUID:" + gameprofile.getName() + " with:" + actual);
        if(!actual.toString().equals(init.toString()))
        {
        	System.out.println("Patching Player UUID uuidPlayer:" + gameprofile.getId() + " with uuidServer:" + actual);
    		ReflectionUtil.setFinalObject(gameprofile, actual, GameProfile.class, "id");
    		VanillaBugFixes.playerFlags.add(gameprofile.getName());
        }
	}
    /**
     * use this for individual entity telports otherwise use teleport stack or teleport stack at index
     */
	public static void telePortEntitySync(Entity entity, MinecraftServer server, double x, double y, double z,float yaw, float pitch, int traveldim) throws WrongUsageException 
	{	
		Entity riding = null;
		if(entity instanceof EntityPlayerMP)
		{
			riding = entity.getRidingEntity();
		}
		
		EntityUtil.telePortEntity(entity, server, x, y, z, yaw, pitch, traveldim);
		
		if(riding != null)
		{
			updatePassengerClient((EntityPlayerMP) entity, entity);
		}
	}

}
