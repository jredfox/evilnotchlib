package com.evilnotch.lib.minecraft.basicmc.recipe;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
/**
 * this allows for nbt recipes note the output has to be fixed though thus one nbt recipe per thing
 * @author jredfox
 */
public class NBTShapelessRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe{

	public ResourceLocation id = null;
	public ItemStack output = null;
	public ItemStackNBT[] ingrediants = null;
	
	public NBTShapelessRecipe(ResourceLocation loc,ItemStack output,ItemStackNBT... params)
	{
		this.id = loc;
		this.output = output;
		this.ingrediants = params;
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) 
	{
		return this.getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height) 
	{
		return width*height >= this.ingrediants.length;
	}

	@Override
	public ItemStack getRecipeOutput() 
	{
		return this.output.copy();
	}

}
