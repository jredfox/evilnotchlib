package com.evilnotch.lib.minecraft.content.capability;

import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;

/**
 * this allows you to have your capabilities attatched to the update() as long as they call super()
 * you must define the type of object to have here based on the parameter for example Entity "YourClass extends ICapTick<Entity>"
 * @author jredfox
 *
 * @param <T>
 */
public interface ICapTick<T> {
	
	public void tick(T object,CapContainer c);

}
