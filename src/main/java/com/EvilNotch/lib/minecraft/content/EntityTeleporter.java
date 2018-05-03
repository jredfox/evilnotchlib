package com.EvilNotch.lib.minecraft.content;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

//Code copied from perfect spawn no credit from me here
public class EntityTeleporter extends Teleporter
{

	public EntityTeleporter(WorldServer worldServer)
	{
		super(worldServer);
	}
	@Override
	public void placeInPortal(Entity entityIn, float yaw)
    {
		placeInExistingPortal(entityIn, yaw);
    }
	@Override
	public boolean placeInExistingPortal(Entity par1Entity, float rotationYaw)
    {
		return false;
    }
	@Override
	public boolean makePortal(Entity par1Entity)
    {
		return false;
    }
	@Override
	public void removeStalePortalLocations(long par1)
    {
		
    }
}