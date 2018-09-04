package com.EvilNotch.lib.minecraft.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PickBlockEvent extends Event{
	/**
	 * read only itemstack initial value
	 */
	public final ItemStack initResault;
	/**
	 * no reason to screw up other mods by changing this
	 */
	public final TileEntity tile;
	public ItemStack result;
	public IBlockState state;
	public RayTraceResult target;
	public World world;
	public BlockPos pos;
	public EntityPlayer player;
	public boolean canPick;
	public boolean copyTE;
	
	public PickBlockEvent(ItemStack result, IBlockState state, RayTraceResult target, World world,EntityPlayer player) 
	{
		this.initResault = result.copy();
		this.result = result;
		this.state = state;
		this.target = target;
		this.world = world;
		this.pos = target.getBlockPos();
		this.player = player;
		this.canPick = player.capabilities.isCreativeMode;
		this.copyTE = GuiScreen.isCtrlKeyDown();
        if(state.getBlock().hasTileEntity(state))
        	this.tile = world.getTileEntity(target.getBlockPos());
        else
        	this.tile = null;
	}

}
