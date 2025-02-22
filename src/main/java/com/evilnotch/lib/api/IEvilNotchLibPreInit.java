package com.evilnotch.lib.api;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IEvilNotchLibPreInit {
	/**
	 * Fires before most of EvilNotchLib's Pre-Init fires which Includes the SkinCache but after the Config loads
	 * Use this instead of normal pre-init for the SkinCache API as the SkinCache API fires in pre-init.
	 * This is so you can use the SkinCache while having normal benefits of EvilNotchLib and not hotload everything in your mod's constructor if you plan to actually use EvilNotchLib's other APIs
	 */
	public void preInitMod(FMLPreInitializationEvent e);

}
