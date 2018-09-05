package com.EvilNotch.lib.minecraft.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PickEntityEvent extends Event{
	
	public final ItemStack initStack;
	/**
	 * the itemstack from the entity returned 
	 */
	public ItemStack current;
	public RayTraceResult targ;
	public EntityPlayer player;
	public World world;
	public boolean canPick;
	
	public PickEntityEvent(ItemStack resault,RayTraceResult target, EntityPlayer player, World world)
	{
		this.initStack = resault.copy();
		this.current = resault;
		this.targ = target;
		this.player = player;
		this.world = world;
		this.canPick = player.capabilities.isCreativeMode;
	}

}
