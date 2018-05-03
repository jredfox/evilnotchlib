package com.EvilNotch.lib.util.registry;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.EvilNotch.lib.util.minecraft.EntityUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class SpawnListEntryAdvanced extends SpawnListEntry{
		public static HashMap<Entity,SpawnListEntryAdvanced> tags = new HashMap();
	
        public NBTTagCompound NBT = null;
        public ResourceLocation loc;
        public java.lang.reflect.Constructor<? extends EntityLiving> ctr;

         /**
          * Super is EnityDummy.class since forges removal system doesn't support dupe classes when mine does with nbt
          * So I simply don't give them the right info
          */
         public SpawnListEntryAdvanced(ResourceLocation loc, int weight, int groupCountMin, int groupCountMax,NBTTagCompound nbt)
         {
             super((Class<? extends EntityLiving>) EntityList.getClass(loc),weight,groupCountMin,groupCountMax);
             try
             {
                 ctr = (Constructor<? extends EntityLiving>) EntityList.getClass(loc).getConstructor(World.class);
             }
             catch (NoSuchMethodException e)
             {
            	System.out.print("FAILED To get Entity World Constructor At SpawnListEntry\n");
             }
             this.loc = loc;
             this.NBT = nbt;
         }
         
         @Override
         public boolean equals(Object obj)
         {
        	 if(!(obj instanceof SpawnListEntryAdvanced))
        	 {
        		 if(obj instanceof SpawnListEntry)
        		 {
        			 SpawnListEntry e = (SpawnListEntry)obj;
        			 return this.NBT == null && this.entityClass.equals(e.entityClass);
        		 }
        		 return false;
        	 }
        	 SpawnListEntryAdvanced entry = (SpawnListEntryAdvanced)obj;
        	 
        	 boolean nbt = true;
        	 if(this.NBT == null)
        		 nbt = entry.NBT == null;
        	 else
        		 nbt = this.NBT.equals(entry.NBT);
        	 
        	 return this.loc.equals(entry.loc) && nbt;
         }
         
         @Override
         public String toString()
         {
             return this.loc + " (" + this.minGroupCount + "-" + this.maxGroupCount + ") " + this.itemWeight + " " + this.NBT;
         }
         /**
          * Since WorldEntitySpawner is overriding this instead of actually using this do the work around and only instantiate the entity as deafult
          */
         @Override
         public EntityLiving newInstance(World world) throws Exception
         {
        	 EntityLiving e = ctr.newInstance(world);
        	 if(this.NBT != null)
        		 tags.put(e, this);//override everything if nbt isn't null
             return e;
         }
         public boolean newActualInstance(World w,double x, double y, double z){
        	 return EntityUtil.spawnEntityEntry(w,this,x,y,z);
         }

}
