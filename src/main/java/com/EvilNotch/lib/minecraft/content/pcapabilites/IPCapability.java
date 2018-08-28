package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.util.simple.ICopy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
/**
 * the attachment designed and optimized for players they are not direct attachments and it is server side only right now
 * and are not apart of the capabilities from both forge and the lib. The data gets written to a separate playerdata files
 * @author jredfox
 */
public interface IPCapability extends ICapability<EntityPlayer>{
	
}
