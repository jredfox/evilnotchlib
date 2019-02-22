package com.evilnotch.lib.asm.gen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class Methods {

	public void fixIdLib(NBTTagCompound nbt) 
    {
        if (!nbt.hasKey("id", 8))
        {
            nbt.setString("id", "minecraft:pig");
        }
        else if (!nbt.getString("id").contains(":"))
        {
            nbt.setString("id", (new ResourceLocation(nbt.getString("id"))).toString());
        }
	}

}
