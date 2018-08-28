package net.minecraft.item;

import java.util.List;

import javax.annotation.Nullable;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.events.TileStackSyncEvent;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlock extends Item
{
    protected final Block block;

    public ItemBlock(Block block)
    {
        this.block = block;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity)null))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    public static boolean setTileEntityNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn)
    {
    	NBTTagCompound stack = stackIn.getSubCompound("BlockEntityTag");
    	return setTileNBT(worldIn,player,pos,stackIn,stack,true);
    }
    public static TileEntity lastTile = null;
    public static boolean setTileNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn,NBTTagCompound stack,boolean blockData)
    {
        if (stack != null)
        {
        	stack = stack.copy();//prevents this from being modified on the itemstack's nbt when firing the pre event
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity != null)
            {
            	 TileStackSyncEvent.Permissions permissions = new TileStackSyncEvent.Permissions(stackIn,pos,tileentity,player,worldIn,blockData);
            	 MinecraftForge.EVENT_BUS.post(permissions);
                 //allow mods to override the tile entity ops only as well as player can use command
            	 if (permissions.opsOnly && !permissions.canUseCommand)
                 {
                     return false;
                 }

                 NBTTagCompound tileData = tileentity.writeToNBT(new NBTTagCompound());
                 NBTTagCompound copyTile = tileData.copy();
                 
                 TileStackSyncEvent.Merge mergeEvent = new TileStackSyncEvent.Merge(stackIn,pos,tileentity,player,worldIn,blockData,tileData,stack);
                 MinecraftForge.EVENT_BUS.post(mergeEvent);
                 tileData = mergeEvent.tileData;
                 stack = mergeEvent.nbt;
                 
                 tileData.merge(stack);
                 tileData.setInteger("x", pos.getX());
                 tileData.setInteger("y", pos.getY());
                 tileData.setInteger("z", pos.getZ());

                 if (!tileData.equals(copyTile))
                 {
                    if(tileentity instanceof TileEntityMobSpawner && !stack.hasKey("SpawnPotentials"))
                    {
                       NBTTagList list = new NBTTagList();
                       NBTTagCompound nbt = new WeightedSpawnerEntity(1, stack.getCompoundTag("SpawnData")).toCompoundTag();
                       list.appendTag(nbt);
                       tileData.setTag("SpawnPotentials", list);
                    }
                	 
                    tileentity.readFromNBT(tileData);
                    tileentity.markDirty();
                    lastTile = tileentity;

                    IBlockState state = worldIn.getBlockState(pos);
                    worldIn.notifyBlockUpdate(pos, state.getBlock().getDefaultState(), state, 3);
                    
                    TileStackSyncEvent.Post event = new TileStackSyncEvent.Post(stackIn,pos,tileentity,player,worldIn,blockData);
                    MinecraftForge.EVENT_BUS.post(event);
                    if(worldIn.isRemote)
                    	SPacketUpdateTileEntity.toIgnore.add(pos);//tells your client to ignore the next tile entity packet sent to you
                    
                    return true;
                 }
            }
         }
         
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos))
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        return worldIn.mayPlace(this.block, pos, false, side, (Entity)null);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.block.getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return this.block.getCreativeTabToDisplayOn();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            this.block.getSubBlocks(tab, items);
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.block.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public Block getBlock()
    {
        return this.getBlockRaw() == null ? null : this.getBlockRaw().delegate.get();
    }

    private Block getBlockRaw()
    {
        return this.block;
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block)
        {
            setTileEntityNBT(world, player, pos, stack);
            this.block.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }
}