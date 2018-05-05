package com.example.examplemod;

import java.io.File;
import java.util.HashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new LibEvents());
        FMLCommonHandler.instance().bus().register(this);
    }
    
	/**
	 * playername to uuid as string
	 */
	public static HashMap<String,String> players = new HashMap();

	public static int count = 0;
	/**
	 * stop vanilla/forge bug of playerdata being randomly wiped
	 */
	@SubscribeEvent
	public void inventoryFixer(TickEvent.PlayerTickEvent e)
	{
		if(e.phase != Phase.END || e.player.worldObj.isRemote || !Config.inventoryFixer)
			return;

		if(count != (20*6) )
		{
			count++;
			return;
		}
		else
			count = 0;
		
		EntityPlayerMP p = (EntityPlayerMP) e.player;
		try
		{
			String pname = p.getCommandSenderName();
			if(!players.containsKey(pname))
			{
				File file = EntityUtil.getPlayerFile(p, false);
				if(!file.exists())
				{
					file.createNewFile();
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("uuid", p.getUniqueID().toString());
					EntityUtil.updatePlayerFile(file,nbt);
					System.out.println("File Created:" + file);
				}
				NBTTagCompound nbt = EntityUtil.getPlayerFileNBT(pname,false);
				players.put(pname, "" + nbt.getString("uuid") );
			}
			
			String compare = p.getUniqueID().toString(); //+ "-null";
			String cached = players.get(pname);
//			cached = "6c583257-c0f3-36ee-8f7b-120dd0b255f7";//debug simulation
			
			if(!compare.equals(cached))
			{
				System.out.println("UUID Change Detected Patching Player:\"" + pname + "\"");
				File toUpdate = EntityUtil.getPlayerFile(p, true);
				
				NBTTagCompound proper = EntityUtil.getPlayerFileNBT(cached.toString(),true);
				proper.setLong("UUIDLeast", p.getUniqueID().getLeastSignificantBits() );//reformat uuids to match current one
				proper.setLong("UUIDMost", p.getUniqueID().getMostSignificantBits() );
				EntityUtil.updatePlayerFile(toUpdate, proper);//find actual uuid then update it

				int traveldim = proper.getInteger("Dimension");
				NBTTagList list = proper.getTagList("Pos", 6);
				
				//supports cross dimensional teleporting
				p.mountEntity((Entity)null);
				EntityUtil.telePortEntity(p, p.mcServer, NBTUtil.getDouble(list, 0), NBTUtil.getDouble(list, 1), NBTUtil.getDouble(list, 2), NBTUtil.getRotationYaw(proper), NBTUtil.getRotationPitch(proper), traveldim);
				p.readFromNBT(proper);//force update everything
				p.playerNetServerHandler.playerEntity = EntityUtil.updateForgeCapabilities(p, p.dimension, true);//reset player object
				
				//update playername file so it doesn't constantly update it the first time it changes
				File name = EntityUtil.getPlayerFile(p, false);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("uuid", p.getUniqueID().toString());
				EntityUtil.updatePlayerFile(name,nbt);
				players.put(pname, p.getUniqueID().toString());//update hashmap
				EntityUtil.printChat(p, EnumChatFormatting.GOLD, EnumChatFormatting.DARK_RED, "UUID Change Detected relog for forge capabilities to sync properly");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
    
}
