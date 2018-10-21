package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.api.FieldAcess;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketHand;
import com.evilnotch.lib.minecraft.network.packet.PacketPickBlock;
import com.evilnotch.lib.minecraft.network.packet.PacketPickEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PickBlock {
	
	/**
	 * a replaced version of forge's hooks for pickblock that allows for event overrides via server side for te data
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
        
        ItemStack result;
        boolean isCreative = player.capabilities.isCreativeMode;
        TileEntity te = null;

        if (target.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            IBlockState state = world.getBlockState(target.getBlockPos());

            if (state.getBlock().isAir(state, world, target.getBlockPos()))
            {
                return false;
            }

            result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
            PickEvent.Block event = new PickEvent.Block(result,target,player,world,state);
            MinecraftForge.EVENT_BUS.post(event);
            isCreative = event.canPick;
            result = event.current;
            boolean copyTe = event.copyTE;
            
            if (isCreative && copyTe)
                te = event.tile;
            
            if (result.isEmpty())
            {
            	return false;
            }
        }
        else
        {
            if (target.typeOfHit != RayTraceResult.Type.ENTITY || target.entityHit == null)
            {
                return false;
            }
            result = target.entityHit.getPickedResult(target);
            PickEvent.Entity event = new PickEvent.Entity(result,target,player,world);
            MinecraftForge.EVENT_BUS.post(event);
            result = event.current;
            isCreative = event.canPick;
            if(!isCreative)
            	return false;
            
            if (result.isEmpty())
            {
                return false;
            }
        }

        if (te != null)
        {
           storeTEInStack(result, te);
        }
        
        if (isCreative)
        {
            setPickedItemStack(player.inventory, result);
            processCreativeInventoryAction((EntityPlayerMP) player, new CPacketCreativeInventoryAction(36 + player.inventory.currentItem, player.getHeldItem(EnumHand.MAIN_HAND)));
        }
        
        int slot = getSlotFor(player.inventory, result);
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
            if (!((ItemStack)inv.mainInventory.get(i)).isEmpty() && stackEqualExact(stack, inv.mainInventory.get(i)))
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

        int itemDropThreshold = (int) ReflectionUtil.getObject(player.connection, NetHandlerPlayServer.class, FieldAcess.itemDropThreshold);
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
        	ReflectionUtil.setObject(player.connection, itemDropThreshold + 20, NetHandlerPlayServer.class, FieldAcess.itemDropThreshold);
            EntityItem entityitem = player.dropItem(itemstack, true);

            if (entityitem != null)
            {
                entityitem.setAgeToCreativeDespawnTime();
            }
        }
	}

	/**
	 * this is a method that will store te to the stack saving everything including mob spawner delay
	 */
    public static void storeTEInStack(ItemStack stack, TileEntity te)
    {
        NBTTagCompound nbttagcompound = te.writeToNBT(new NBTTagCompound());
        nbttagcompound.removeTag("x");
        nbttagcompound.removeTag("y");
        nbttagcompound.removeTag("z");
        nbttagcompound.removeTag("id");

        if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner"))
        {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            stack.setTagCompound(nbttagcompound3);
        }
        else
        {
            stack.setTagInfo("BlockEntityTag", nbttagcompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.appendTag(new NBTTagString("(+NBT)"));
            nbttagcompound1.setTag("Lore", nbttaglist);
            stack.setTagInfo("display", nbttagcompound1);
        }
    }

}
