package com.EvilNotch.lib.minecraft.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClientBlockPlaceEvent2 extends Event{
	
	   public final EntityPlayer player;
	   public final IBlockState placedBlock;
	   public final IBlockState placedAgainst;
	   public final EnumHand hand;
	   public final ItemStack stackInHand;
	   public World world;
	   public BlockPos pos1;
	   public BlockPos pos2;

       public ClientBlockPlaceEvent2(IBlockState placed,IBlockState placedAgainst,EntityPlayer player,EnumHand hand,ItemStack stack,BlockPos posplace,BlockPos posagainst) 
       {
           this.player = player;
           this.placedBlock = placed;
           this.placedAgainst = placedAgainst;
           this.hand = hand;
           this.stackInHand = stack;
           this.world = player.world;
           this.pos1 = posplace;
           this.pos2 = posagainst;
       }

       public EntityPlayer getPlayer() { return player; }
       public ItemStack getItemInHand() { return player.getHeldItem(hand); }
       public IBlockState getPlacedBlock() { return placedBlock; }
       public IBlockState getPlacedAgainst() { return placedAgainst; }
       public EnumHand getHand() { return hand; }
       public ItemStack getStack(){return this.stackInHand;}

}
