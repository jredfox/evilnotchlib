package com.evilnotch.lib.minecraft.basicmc.client.model;

import java.util.ArrayList;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairString;

public class ModelPart {
	
	public String parent = null;
	public String particle = null;
	public String up = null;
	public String down = null;
	public String north = null;
	public String south = null;
	public String east = null;
	public String west = null;
	
	/***
	 * if the particle has a different texture not pulled from it's sides
	 */
	public boolean customParticle = false;
	
	/**
	 * if the sides are not all the same texture
	 */
	public boolean customSide = false;
	
	public static final ModelPart cube_all = new ModelPart();
	public static final ModelPart cube_bottom_top = new ModelPart("block/cube_bottom_top","side","top","bottom","side","side","side", "side");
	public static final ModelPart cube_column = new ModelPart("block/cube_column", "side", "end", "end", "side", "side", "side", "side");
	public static final ModelPart cube_directional = new ModelPart("block/cube_directional", "particle", "down", "up", "north", "south", "east", "west");
	public static final ModelPart cube_mirrored = new ModelPart("block/cube_mirrored", "particle", "down", "up", "north", "south", "east", "west");
	public static final ModelPart cube_mirrored_all = new ModelPart("block/cube_mirrored_all","all","all","all","all","all","all","all");
	public static final ModelPart cube_top = new ModelPart("block/cube_top","side","top","side","side","side","side","side");
	
	public ModelPart()
	{
		this("block/cube_all", "all", "all", "all", "all", "all", "all", "all");
	}
	
	public ModelPart(String parent, String particle, String up, String down, String north, String south, String east, String west)
	{
		this.parent = parent;
		this.particle = particle;
		this.down = down;
		this.up = up;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.customParticle = getHasCustomParticle();
		this.customSide = getHasCustomSide();
	}
	
	/**
	 * use this constructor for model parts that are not implementing the particle texture
	 */
	public ModelPart(ModelPart base, String particle)
	{
		this.particle = particle;
		
		this.parent = base.parent;
		this.down = base.down;
		this.up = base.up;
		this.north = base.north;
		this.south = base.south;
		this.east = base.east;
		this.west = base.west;
		this.customParticle = getHasCustomParticle();
		this.customSide = getHasCustomSide();
	}

	protected boolean getHasCustomSide() 
	{	
		ArrayList<String> list = JavaUtil.asArray(new Object[]{this.down,this.up,this.north,this.south,this.east,this.west});
		for(String s : list)
		{
			for(String s2 : list)
			{
				if(!s.equals(s2))
					return true;
			}
		}
		return false;
	}

	protected boolean getHasCustomParticle() 
	{
		return this.particle.equals("particle");
	}

	public PairString[] getParts() 
	{
		return new PairString[]{new PairString("particle",this.particle),new PairString("up",this.up),new PairString("down",this.down),new PairString("north",this.north),new PairString("south",this.south),new PairString("east",this.east),new PairString("west",this.west)};
	}

}
