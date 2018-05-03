package com.EvilNotch.lib.main.events;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.content.EntityTeleporter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TickHandler {
	
	/**
	 * playername to uuid as string
	 */
	public static HashMap<String,String> players = new HashMap();

	
	int tickcount = 0;
	@SubscribeEvent
	public void tick(PlayerTickEvent event)
	{
		if(event.phase != Phase.END || event.player.world.isRemote)
			return;
		if(tickcount != 20)
		{
			tickcount++;
			return;
		}
		tickcount = 0;
		
		//actual code
		if(!event.player.world.loadedEntityList.contains(event.player))
			System.out.println("ERR:" + event.player.getName() );
	}
	public static int count = 0;
	/**
	 * stop vanilla/forge bug of playerdata being randomly wiped
	 */
	@SubscribeEvent
	public void inventoryFixer(TickEvent.PlayerTickEvent e)
	{
		if(e.phase != Phase.END || e.player.world.isRemote || !Config.inventoryFixer)
			return;

		if(count != (20*6) )
		{
			count++;
			return;
		}
		else
			count = 0;
		
		EntityPlayer p = e.player;
		
		try
		{
			String pname = p.getName();
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
//			cached = "74c27658-bdbf-3735-92e3-d4cbb89f0350";
			
			if(!compare.equals(cached))
			{
				System.out.println("UUID Change Detected Patching Player:\"" + pname + "\"");
				File toUpdate = EntityUtil.getPlayerFile(p, true);
				
				NBTTagCompound proper = EntityUtil.getPlayerFileNBT(cached.toString(),true);
				proper.setLong("UUIDLeast", p.getUniqueID().getLeastSignificantBits() );//reformat uuids to match current one
				proper.setLong("UUIDMost", p.getUniqueID().getMostSignificantBits() );
				EntityUtil.updatePlayerFile(toUpdate, proper);//find actual uuid then update it

				int currentdim = p.dimension;
				int traveldim = proper.getInteger("Dimension");
				p.dismountRidingEntity();
				
				NBTTagList list = proper.getTagList("Pos", 6);
				
				//Spawn Entity into world
				if(currentdim != traveldim)
				{
					p.setLocationAndAngles(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2), p.rotationYaw, p.rotationPitch);
					p.getServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) p,traveldim, new EntityTeleporter(p.getServer().getWorld(traveldim)));
				}
				Set<SPacketPlayerPosLook.EnumFlags> set = new HashSet<>();
				p.readFromNBT(proper);//force update what I can like inventories
				((EntityPlayerMP)p).connection.setPlayerLocation(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2), p.rotationYaw, p.rotationPitch, set);
				
				//update capabilities
				FMLCommonHandler.instance().firePlayerRespawnEvent(p, false);
				
				//update playername file so it doesn't constantly update it the first time it changes
				File name = EntityUtil.getPlayerFile(p, false);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("uuid", p.getUniqueID().toString());
				EntityUtil.updatePlayerFile(name,nbt);
				players.put(pname, p.getUniqueID().toString());//update hashmap
				System.out.println("updating player cache:" + name);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
