package com.EvilNotch.lib.asm;

import java.util.List;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapRegHandler;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class Caps implements ICapProvider{
	
	public Caps()
	{
		CapRegHandler.registerCapsToObj(this);
	}
	
	public CapContainer capContainer = new CapContainer();
	private List<TileEntity> loadedTileEntityList = null;
	private List<TileEntity> field_147482_g = null;
	
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public CapContainer getCapContainer()
	{
		return this.capContainer;
	}
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public void setCapContainer(CapContainer c)
	{
		this.capContainer = c;
	}
	
    public void tickTileCapsDeOb() 
    {
        for(TileEntity tile : this.loadedTileEntityList)
            ((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)tile).getCapContainer().tick(tile);
	}
    public void tickTileCapsOb() 
    {
        for(TileEntity tile : this.field_147482_g)
            ((com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider)tile).getCapContainer().tick(tile);
	}
    public void tst()
    {
    	this.capContainer.tick(this);
    }

	
}
