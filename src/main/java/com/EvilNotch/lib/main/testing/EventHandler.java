package com.EvilNotch.lib.main.testing;

import com.EvilNotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.EvilNotch.lib.minecraft.content.capabilites.primitive.CapString;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;
import com.EvilNotch.lib.minecraft.events.PickEvent;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class EventHandler {
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void join(BlockEvent.PlaceEvent e)
	{
		TileEntity tile = e.getWorld().getTileEntity(e.getPos());
		if(tile == null)
			return;
		ICapProvider provider = (ICapProvider)tile;
		CapContainer c = provider.getCapContainer();
		CapBoolean bool = (CapBoolean) c.getCapability(CapRegTile.tile);
		System.out.println("tileHasCap:" + (bool != null) );
		if(bool != null)
			bool.value = true;
	}
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void join(PlayerRespawnEvent e)
	{
		if(!e.player.world.isRemote)
		{
			ICapProvider provider = (ICapProvider)e.player;
			CapContainer c = provider.getCapContainer();
			CapString str = (CapString) c.getCapability(new ResourceLocation("a:b"));
			str.str = "jredfox";
		}
	}
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void pick(PickEvent.Block e)
	{
		if(e.state.getBlock() == Blocks.MOB_SPAWNER)
		{
			System.out.println("here pickblock:" + e.canPick);
			e.current = new ItemStack(Blocks.MOB_SPAWNER);
		}
	}

}
