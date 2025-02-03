package com.evilnotch.lib.main.capability;

import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.ICapability;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class LoginCap implements ICapability<EntityPlayerMP> {
	
	public NBTTagCompound nbt;
	public LoginCap(NBTTagCompound nbt)
	{
		this.nbt = nbt;
	}
	
	public NBTTagCompound getClientCaps()
	{
		return this.get(ClientCapHooks.ID_CLIENTCAPS);
	}
	
	public NBTTagCompound get(ResourceLocation id)
	{
		return this.nbt.getCompoundTag(id.toString().replace(":", "_"));
	}

	@Override
	public void writeToNBT(EntityPlayerMP object, NBTTagCompound nbt, CapContainer c) {}

	@Override
	public void readFromNBT(EntityPlayerMP object, NBTTagCompound nbt, CapContainer c) {}

}
