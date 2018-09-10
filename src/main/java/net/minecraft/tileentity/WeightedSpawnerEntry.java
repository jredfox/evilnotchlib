package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedSpawnerEntity;

public class WeightedSpawnerEntry extends WeightedSpawnerEntity{
	
	public boolean nbtEntry = false;
	
    public WeightedSpawnerEntry()
    {
    	super();
    	this.nbtEntry = false;
    }
    public WeightedSpawnerEntry(NBTTagCompound nbtIn)
    {
        this(nbtIn.hasKey("Weight", 99) ? nbtIn.getInteger("Weight") : 1,nbtIn.getCompoundTag("Entity"));
    }

    public WeightedSpawnerEntry(int itemWeightIn, NBTTagCompound nbtIn)
    {
    	super(itemWeightIn,nbtIn);
    	this.nbtEntry = nbtIn.getBoolean("NBTEntry");
    }
    
	@Override
    public NBTTagCompound toCompoundTag()
    {
    	NBTTagCompound nbt = super.toCompoundTag();
    	nbt.setBoolean("NBTEntry", this.nbtEntry);
    	return nbt;
    }
    public NBTTagCompound getAdditionalData(MobSpawnerBaseLogic logic)
    {
    	NBTTagCompound nbt = new NBTTagCompound();
    	nbt.setInteger("currentIndex", getCurrentIndex(logic));
    	nbt.setBoolean("NBTEntry", this.nbtEntry);
    	return nbt;
    }
	public int getCurrentIndex(MobSpawnerBaseLogic logic) {
		int index = 0;
		for(WeightedSpawnerEntity e : logic.potentialSpawns)
		{
			if(e == this)
				return index;
			index++;
		}
		return -1;
	}

}
