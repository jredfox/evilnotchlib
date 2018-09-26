package com.evilnotch.lib.minecraft.content.pcapability;

import com.evilnotch.lib.minecraft.content.capability.ICapability;

import net.minecraft.entity.player.EntityPlayerMP;
/**
 * the attachment designed and optimized for players they are not direct attachments and it is server side only right now
 * and are not apart of the capabilities from both forge and the lib. The data gets written to a separate playerdata files
 * @author jredfox
 */
public interface IPCapability extends ICapability<EntityPlayerMP>{
	
}
