package com.evilnotch.lib.minecraft.content.block;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.util.line.LineArray;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockProperties {
	public ResourceLocation matId = null;
	public Material mat = null;
	public String harvestTool = null;
	public float blockHardness = 0f;
	public float blastResistance = 0f;
	public int harvestLvl = -1;
	public SoundType sound = null;
	
	public int flamability = -1;
	public int flameEncoragement = -1;
		
	public float slipperiness = 0.6F;
	public int lightValue = 0;
		
	/**
	 * set this manually to true to use advanced properties you must manually configure each of them to your desire
	 */
	public boolean advanced = false;
		
	public boolean translucent = false;
	public int lightOpacity = 0;
	public boolean useNeighborBrightness = false;
	public boolean enableStats = true;
	public float blockParticleGravity = 1.0F;
		
	/**
	 * Construct default with only default values you manipulate what you want
	 */
	public BlockProperties(){
	}
	public BlockProperties(ResourceLocation idMat)
	{
		this.matId = idMat;
	}
	/**
	 * Basic constructor
	 * loc is the resource location of the block
	 * and mat is the material of that block
	 */
	public BlockProperties(ResourceLocation idMat,String tool,float hard,float resist,int lvl){
		this(idMat,tool,hard,resist,lvl,null);
	}
	public BlockProperties(ResourceLocation idMat,String tool,float hard,float resist,int lvl, SoundType sound){
		this(idMat,tool,hard,resist,lvl,sound,-1,-1);
	}
	public BlockProperties(ResourceLocation idMat,String tool,float hard,float resist,int lvl, SoundType sound,int flamE,int flame){
		this(idMat,tool,hard,resist,lvl,sound,flamE,flame,0.6F,0);
	}
	public BlockProperties(ResourceLocation idMat,String tool,float hard,float resist,int lvl,SoundType sound,int flameE,int flame,float slip,int light)
	{
		this.matId = idMat;
		this.mat = BlockApi.getMatFromReg(idMat);
		this.harvestTool = tool;
		this.blockHardness = hard;
		this.blastResistance = resist;
		this.harvestLvl = lvl;
		this.sound = sound;
		this.flameEncoragement = flameE;
		this.flamability = flame;
		this.slipperiness = slip;
		this.lightValue = light;
	}
	public BlockProperties(LineArray line)
	{
		if(line.hasStringMeta())
		{
			this.matId = new ResourceLocation(line.meta);
			this.mat = BlockApi.getMatFromReg(this.matId);
		}
		int size = line.heads.size();
		if(size >= 5)
		{
			this.harvestTool = line.getString(0);
			this.blockHardness = line.getFloat(1);
			this.blastResistance = line.getFloat(2);
			this.harvestLvl = line.getInt(3);
			this.sound = BlockApi.getSoundType(new ResourceLocation(line.getString(4)));
		}
		if(size >= 7)
		{
			this.flameEncoragement = line.getInt(5);
			this.flamability = line.getInt(6);
		}
		if(size >= 9)
		{
			this.slipperiness = line.getFloat(7);
			this.lightValue = line.getInt(8);
		}
		if(size == 14)
		{
			this.advanced = true;
			this.translucent = line.getBoolean(9);
			this.lightOpacity = line.getInt(10);
			this.useNeighborBrightness = line.getBoolean(11);
			this.enableStats = line.getBoolean(12);
			this.blockParticleGravity = line.getFloat(13);
		}
	}
		
	@Override
	public String toString()
	{
		String init = "\"" + this.harvestTool + "\"" + "," + this.blockHardness + "f," + this.blastResistance + "f," + this.harvestLvl + "," + "\"" + BlockApi.getSoundTypeLoc(this.sound) + "\"";
		String bonus = "";
		boolean useAll = this.advanced || this.slipperiness != 0.6F || this.lightValue != 0;
		boolean useFlame = this.flamability != -1 || this.flameEncoragement != -1;
		if(useAll){
			bonus = "," + this.flameEncoragement + "," + this.flamability + "," + this.slipperiness + "f," + this.lightValue;
			if(this.advanced)
				bonus += "," + this.translucent + "," + this.lightOpacity + "," + this.useNeighborBrightness + "," + this.enableStats + "," + this.blockParticleGravity + "f";
		}
		else if(useFlame){
			bonus = "," + this.flameEncoragement + "," + this.flamability;
		}
		return "<\"" + this.matId.toString() + "\">" + " = [" + init + bonus +"]";
	}
}
