package com.EvilNotch.lib.util.minecraft;

import javax.annotation.Nullable;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.util.registry.SpawnListEntryAdvanced;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityUtil {
	
	 public static String TransLateEntity(NBTTagCompound nbt,World w)
	 {
	   	nbt = nbt.copy();
	   	nbt.removeTag("CustomName");
	   	String id = nbt.getString("id");
	   	Entity e = createEntityFromNBTQuietly(new ResourceLocation(id), nbt, w);
	   	
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
    	if(loc != null)
    	{
    		if(Config.cmdBlacklist.contains(loc) )
    		{
    			String str = translateEntityGeneral(entity,w);
    			if(str == null)
    				return null;
    			//if mistranslated fix it
    	    	if(isMistranslated(str))
    	    		str = str.substring(7, str.length()-5 );
        		return str;
    		}
    	}
    	
    	String strentity = translateEntityCmd(entity,w);
    	
    	//if cmd fails try general name
    	if(strentity == null || isMistranslated(strentity) )
    	{
    		String generalName = translateEntityGeneral(entity,w);
    		if(generalName != null && !isMistranslated(generalName) )
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
	   String s = EntityList.getEntityString(entity);
	   String EntityName = EntityList.getEntityString(entity);
	   try{
		EntityName = I18n.translateToLocal("entity." + s + ".name");
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
			return entity.getName();
		}catch(Throwable t){
			net.minecraftforge.fml.common.FMLLog.log.error("Entity Has Thrown an Error when entity.getName() Report to mod author:" + EntityList.getEntityString(entity));
		}
		return null;
	}
	 @Nullable
	 public static Entity createEntityFromNBTQuietly(ResourceLocation loc,NBTTagCompound nbt, World worldIn)
	 {
	   try{
		   Entity e = createEntityByNameQuietly(loc,worldIn);
		   if(e != null)
			   e.readFromNBT(nbt);
		   return e;
	  	}catch(Throwable e){}
	  	return null;
	 }
	 
    @Nullable
    public static Entity createEntityByNameQuietly(ResourceLocation loc, World worldIn)
    {
    	try{
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(loc);
        return entry == null ? null : entry.newInstance(worldIn);
    	}catch(Throwable e){}
    	return null;
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
		player.sendMessage(new TextComponentString(c_player + player.getName() + " " + c_msg + messege));
	}
    /**
     * Spawn Entity by spawnlistentry from Scratch
     */
	public static boolean spawnEntityEntry(World w, SpawnListEntry entry,double x, double y, double z) {
		try {
			//if doesn't contain nbt or is legacy do normal spawning
			if(legacySpawnListEntry(entry))
			{
				System.out.println("WHy you HERE LEGACy:");
				EntityLiving living = entry.newInstance(w);
				living.setLocationAndAngles(x, y, z, living.rotationYaw, living.rotationPitch);
				living.onInitialSpawn(w.getDifficultyForLocation(new BlockPos(x,y,z)), (IEntityLivingData)null);
				w.spawnEntity(living);
				return true;
			}
		   SpawnListEntryAdvanced advanced = (SpawnListEntryAdvanced)entry;
		   NBTTagCompound compound = advanced.NBT.copy();
		   compound.setString("id", advanced.loc.toString());
		   Entity e = getEntityJockey(compound,w,x,y,z,true);
		   return e != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Doesn't force nbt if you don't need it to unlike vanilla this is the forum of the /summon command
	 * silkspawners eggs will support multiple indexes but, not to this extent not requring recursion use only when fully supporting new format
	 */
	public static Entity getEntityJockey(NBTTagCompound compound,World worldIn, double x, double y, double z,boolean firstcall) 
	{	
        Entity entity = getEntity(compound,worldIn,new BlockPos(x,y,z),firstcall,true);
        if(entity == null)
        	return null;
        
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
//        entity.forceSpawn = true;
        if(!worldIn.spawnEntity(entity))
        	return null;
        
        if (compound.hasKey("Passengers", 9))
        {
             NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
             for (int i = 0; i < nbttaglist.tagCount(); ++i)
             {
                 Entity entity1 = getEntityJockey(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z,false);
                  if (entity1 != null)
                  {
                      entity1.startRiding(entity, true);
                  }
             }
        }

       return entity;
	}

	/**
	 * first index is to determine if your on the first part of the opening of the nbt if so treat nbt like normal
	 * @return
	 */
	private static Entity getEntity(NBTTagCompound nbt,World world,BlockPos pos,boolean firstIndex,boolean useInterface) {
		Entity e = null;
		if(getEntityProps(nbt).getSize() > 0 || !nbt.hasKey("Passengers") && firstIndex)
			e = EntityUtil.createEntityFromNBTQuietly(new ResourceLocation(nbt.getString("id")), nbt, world);
		else{
			e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
			if(e instanceof EntityLiving && useInterface)
				((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
		}
		return e;
	}

	private static boolean legacySpawnListEntry(SpawnListEntry entry) throws Exception {
		if(!(entry instanceof SpawnListEntryAdvanced) || ((SpawnListEntryAdvanced)entry).NBT == null )
			return true;
		return false;
	}

	private static NBTTagCompound getEntityProps(NBTTagCompound nbt) {
		if(nbt == null)
			return null;
		nbt = nbt.copy();
		nbt.removeTag("Passengers");
		nbt.removeTag("id");
		return nbt;
	}

}
