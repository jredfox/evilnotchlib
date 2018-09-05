package com.EvilNotch.lib.main.testing;

import com.EvilNotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.EvilNotch.lib.minecraft.content.capabilites.primitive.CapString;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;
import com.EvilNotch.lib.minecraft.events.PickEvent;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class EventHandler {
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void pick(PickEvent.Block e)
	{
		if(e.tile instanceof TileEntityMobSpawner)
		{
			Block b = e.state.getBlock();
			e.current = new ItemStack(b,1,b.getMetaFromState(e.state));
		}
	}

}
