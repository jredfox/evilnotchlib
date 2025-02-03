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
		this.nbt = nbt != null ? nbt : new NBTTagCompound();
	}
	
	public NBTTagCompound get(ResourceLocation id)
	{
		String strId = id.toString().replace(":", "_");
		//Prevent Dis-Attatched NBTTagCompound Editing
		if(!this.nbt.hasKey(strId, 10))
			this.nbt.setTag(strId, new NBTTagCompound());
		
		return this.nbt.getCompoundTag(strId);
	}
	
	public NBTTagCompound getClientCaps()
	{
		return this.get(ClientCapHooks.ID_CLIENTCAPS);
	}
	
	public void set(ResourceLocation id, NBTTagCompound nbt) 
	{
		this.nbt.setTag(id.toString().replace(":", "_"), nbt);
	}
	
	public void setClientCaps(NBTTagCompound nbt)
	{
		this.set(ClientCapHooks.ID_CLIENTCAPS, nbt);
	}

	@Override
	public void writeToNBT(EntityPlayerMP object, NBTTagCompound nbt, CapContainer c) {}

	@Override
	public void readFromNBT(EntityPlayerMP object, NBTTagCompound nbt, CapContainer c) {}

}
