package com.evilnotch.lib.minecraft.basicmc.client.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairString;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
	 * if the particle has a different value entirely
	 */
	public boolean customParticle = false;
	
	/**
	 * if the sides are not all the same texture
	 */
	public boolean customSide = false;
	
	/**
	 * if the particle isn't null
	 */
	public boolean hasParticle = false;
	
	public static final ModelPart cube_all = new ModelPart();
	public static final ModelPart cube_bottom_top = new ModelPart("block/cube_bottom_top","side","top","bottom","side","side","side", "side");
	public static final ModelPart cube_column = new ModelPart("block/cube_column", "side", "end", "end", "side", "side", "side", "side");
	public static final ModelPart cube_directional = new ModelPart("block/cube_directional", "null", "down", "up", "north", "south", "east", "west");
	public static final ModelPart cube_mirrored = new ModelPart("block/cube_mirrored", "null", "down", "up", "north", "south", "east", "west");
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
		this.hasParticle = !particle.equals("null");
		this.customParticle = getHasCustomParticle();
		this.customSide = getHasCustomSide();
	}
	
	/**
	 * use this constructor for model parts that are not implementing the particle texture
	 */
	public ModelPart(ModelPart base, String particle)
	{
		this.parent = base.parent;
		this.particle = base.particle;
		this.down = base.down;
		this.up = base.up;
		this.north = base.north;
		this.south = base.south;
		this.east = base.east;
		this.west = base.west;
		this.hasParticle = !particle.equals("null");
		this.customParticle = getHasCustomParticle();
		this.customSide = getHasCustomSide();
	}

	protected boolean getHasCustomSide() 
	{	
		ArrayList<String> list = JavaUtil.asArray(new Object[]{down,up,north,south,east,west});
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
		return !this.particle.equals(this.up) && !this.particle.equals(this.down) && !this.particle.equals(this.north) && !this.particle.equals(this.south) && !this.particle.equals(this.east) && !this.particle.equals(this.west);
	}

}
