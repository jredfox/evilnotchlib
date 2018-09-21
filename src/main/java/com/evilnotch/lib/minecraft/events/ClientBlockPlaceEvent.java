package com.evilnotch.lib.minecraft.events;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;

public class ClientBlockPlaceEvent extends BlockEvent{
	
	   private final EntityPlayer player;
       private final BlockSnapshot blockSnapshot;
       private final IBlockState placedBlock;
       private final IBlockState placedAgainst;
       private final EnumHand hand;

       public ClientBlockPlaceEvent(@Nonnull BlockSnapshot blockSnapshot, @Nonnull IBlockState placedAgainst, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
           super(blockSnapshot.getWorld(), blockSnapshot.getPos(), blockSnapshot.getCurrentBlock());
           this.player = player;
           this.blockSnapshot = blockSnapshot;
           this.placedBlock = blockSnapshot.getCurrentBlock();
           this.placedAgainst = placedAgainst;
           this.hand = hand;
       }

       public EntityPlayer getPlayer() { return player; }
       @Nonnull
       @Deprecated
       public ItemStack getItemInHand() { return player.getHeldItem(hand); }
       public BlockSnapshot getBlockSnapshot() { return blockSnapshot; }
       public IBlockState getPlacedBlock() { return placedBlock; }
       public IBlockState getPlacedAgainst() { return placedAgainst; }
       public EnumHand getHand() { return hand; }

}