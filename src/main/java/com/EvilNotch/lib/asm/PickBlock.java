package com.EvilNotch.lib.asm;

import com.EvilNotch.lib.minecraft.events.PickBlockEvent;
import com.EvilNotch.lib.minecraft.events.PickEntityEvent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PickBlock {
	
	/**
	 * a replaced version of forge's hooks for pickblock that allows for event overrides
	 * @return
	 */
	public static boolean pickBlock(RayTraceResult target, EntityPlayer player, World world)
	{
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
            PickBlockEvent event = new PickBlockEvent(result,state,target,world,player);
            MinecraftForge.EVENT_BUS.post(event);
            isCreative = event.canPick;
            result = event.result;
            boolean copyTe = event.copyTE;
            
            if (isCreative && copyTe)
                te = event.tile;
        }
        else
        {
            if (target.typeOfHit != RayTraceResult.Type.ENTITY || target.entityHit == null)
            {
                return false;
            }
            result = target.entityHit.getPickedResult(target);
            PickEntityEvent event = new PickEntityEvent(result,target,player,world);
            MinecraftForge.EVENT_BUS.post(event);
            result = event.current;
            isCreative = event.canPick;
            if(!isCreative)
            	return false;
        }

        if (result.isEmpty())
        {
            return false;
        }

        if (te != null)
        {
           storeTEInStack(result, te);
        }
        
        if (isCreative)
        {
            player.inventory.setPickedItemStack(result);
            Minecraft.getMinecraft().playerController.sendSlotPacket(player.getHeldItem(EnumHand.MAIN_HAND), 36 + player.inventory.currentItem);
            return true;
        }
        int slot = player.inventory.getSlotFor(result);
        if (slot != -1)
        {
            if (InventoryPlayer.isHotbar(slot))
                player.inventory.currentItem = slot;
            else
                Minecraft.getMinecraft().playerController.pickItem(slot);
            return true;
        }
        return false;
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
