package com.evilnotch.lib.minecraft.content.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ShapelessRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe{
	
	public ResourceLocation id = null;
	public ItemStack output = null;
	public ItemStack[] ingrediants = null;
	
	public ShapelessRecipe(ResourceLocation loc,ItemStack output,ItemStack... params)
	{
		this.id = loc;
		this.output = output;
		this.ingrediants = params;
	}
	public ShapelessRecipe(ResourceLocation loc,ItemStack output,Item... params)
	{
		this.id = loc;
		this.output = output;
		ItemStack[] stacks = new ItemStack[params.length];
		for(int i=0;i<params.length;i++)
			stacks[i] = new ItemStack(params[i],1,OreDictionary.WILDCARD_VALUE);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) 
	{
		List<ItemStack> matching = new ArrayList();
		for(int j=0;j<inv.getSizeInventory();j++)
		{
			ItemStack slot = (ItemStack)inv.getStackInSlot(j);
			if(containsStack(slot) )
				matching.add(slot);
			else if(!slot.isEmpty())
			{
				return false;
			}
		}
		return matching.size() == this.ingrediants.length;
	}

	public boolean containsStack(ItemStack compare) 
	{
		if(compare.isEmpty())
			return false;
		
		for(ItemStack stack : this.ingrediants)
		{
			if(stack.getItem() == compare.getItem())
			{
				if(stack.getItemDamage() == compare.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
					return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) 
	{
		return this.getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width*height >= this.ingrediants.length;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.output.copy();
	}

}
