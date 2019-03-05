package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketHand;
import com.evilnotch.lib.minecraft.network.packet.PacketPickBlock;
import com.evilnotch.lib.minecraft.network.packet.PacketPickEntity;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PickBlock {
	
	/**
	 * a replaced version of forge's hooks for pickblock that allows for event overrides via server side for TE and Entity data
	 */
	public static boolean pickBlock(RayTraceResult target, EntityPlayer player, World world)
	{
        if(world.isRemote)
        {
            if (target.typeOfHit == RayTraceResult.Type.BLOCK)
            	NetWorkHandler.INSTANCE.sendToServer(new PacketPickBlock(target));
            else
            	NetWorkHandler.INSTANCE.sendToServer(new PacketPickEntity(target));
            return false;
        }

        if (target.typeOfHit == RayTraceResult.Type.BLOCK)
        {
        	BlockPos pos = target.getBlockPos();
            IBlockState state = world.getBlockState(pos);
            ItemStack stack = ItemStack.EMPTY;
            TileEntity tile = null;
            boolean canPick;
            boolean copyTe;
            
            stack = state.getBlock().getPickBlock(state, target, world, pos, player);

            PickEvent.Block event = new PickEvent.Block(stack,target,player,world,state);
            MinecraftForge.EVENT_BUS.post(event);
           
            stack = event.current;
            tile = event.tile;
            canPick = event.canPick;
            copyTe = event.copyTE;
            
            if (!canPick || stack.isEmpty())
            {
            	return false;
            }
            
            if (copyTe && tile != null)
            {
            	TileEntityUtil.storeTEInStack(stack, tile);
            }
            return setPickedStack(player,stack);
        }
        else if(target.typeOfHit == RayTraceResult.Type.ENTITY)
        {
            if (target.entityHit == null)
            {
                return false;
            }
            ItemStack stack = target.entityHit.getPickedResult(target);
            boolean canPick;
            
            PickEvent.Entity event = new PickEvent.Entity(stack,target,player,world);
            MinecraftForge.EVENT_BUS.post(event);
            stack = event.current;
            canPick = event.canPick;
            
            if(!canPick || stack.isEmpty())
            {
            	return false;
            }
            
            return setPickedStack(player,stack);
        }
        
        return false;
	}
	
    private static boolean setPickedStack(EntityPlayer player, ItemStack stack) 
    {
        setPickedItemStack(player.inventory, stack);
        processCreativeInventoryAction((EntityPlayerMP) player, new CPacketCreativeInventoryAction(36 + player.inventory.currentItem, player.getHeldItem(EnumHand.MAIN_HAND)));
        
        int slot = getSlotFor(player.inventory, stack);
        if (slot != -1)
        {
            if (InventoryPlayer.isHotbar(slot))
            {
                 player.inventory.currentItem = slot;
                 NetWorkHandler.INSTANCE.sendTo(new PacketHand(slot), (EntityPlayerMP) player);
            }
            
         	((EntityPlayerMP)player).inventoryContainer.detectAndSendChanges();
            
            return true;
        }
        return false;
	}

	public static int getSlotFor(InventoryPlayer inv, ItemStack stack)
    {
        for (int i = 0; i < inv.mainInventory.size(); ++i)
        {
        	ItemStack compare = (ItemStack)inv.mainInventory.get(i);
            if (!compare.isEmpty() && stackEqualExact(stack, compare))
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Checks item, NBT, and meta if the item is not damageable
     */
    public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
	
    public static void setPickedItemStack(InventoryPlayer inv, ItemStack stack)
    {
        int i = inv.getSlotFor(stack);

        if (inv.isHotbar(i))
        {
        	inv.currentItem = i;
        }
        else
        {
            if (i == -1)
            {
            	inv.currentItem = inv.getBestHotbarSlot();

                if (!((ItemStack)inv.mainInventory.get(inv.currentItem)).isEmpty())
                {
                    int j = inv.getFirstEmptyStack();

                    if (j != -1)
                    {
                    	inv.mainInventory.set(j, inv.mainInventory.get(inv.currentItem));
                    }
                }

                inv.mainInventory.set(inv.currentItem, stack);
            }
            else
            {
            	inv.pickItem(i);
            }
        }
    }
	/**
	 * functions without creative acess in player's inventory
	 */
	public static void processCreativeInventoryAction(EntityPlayerMP player, CPacketCreativeInventoryAction packetIn) 
	{
        boolean flag = packetIn.getSlotId() < 0;
        ItemStack itemstack = packetIn.getStack();
        
        boolean flag1 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() <= 45;
        boolean flag2 = itemstack.isEmpty() || itemstack.getMetadata() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();

        int itemDropThreshold = player.connection.itemDropThreshold;
        if (flag1 && flag2)
        {
            if (itemstack.isEmpty())
            {
                player.inventoryContainer.putStackInSlot(packetIn.getSlotId(), ItemStack.EMPTY);
            }
            else
            {
                player.inventoryContainer.putStackInSlot(packetIn.getSlotId(), itemstack);
            }

            player.inventoryContainer.setCanCraft(player, true);
        }
        else if (flag && flag2 && itemDropThreshold < 200)
        {
        	player.connection.itemDropThreshold += 20;
            EntityItem entityitem = player.dropItem(itemstack, true);

            if (entityitem != null)
            {
                entityitem.setAgeToCreativeDespawnTime();
            }
        }
	}

}
