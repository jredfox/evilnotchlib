package com.EvilNotch.lib.minecraft.content.client.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.simple.PairString;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPart {
	
	public String parent = null;
	public JSONObject json = null;
	public Set<PairString> keySet = new HashSet();
	
	public boolean customParticle = false;
	public boolean customSide = false;
	
	public static final ModelPart cube_all = new ModelPart();
	public static final ModelPart cube_bottom_top = new ModelPart("{\"parent\": \"block/cube_bottom_top\",\"textures\": {\"particle\": \"#side\",\"down\": \"#bottom\",\"up\": \"#top\",\"north\": \"#side\",\"east\": \"#side\",\"south\": \"#side\",\"west\": \"#side\"}}");
	public static final ModelPart cube_column = new ModelPart("{\"parent\": \"block/cube_column\",\"textures\": {\"particle\": \"#side\",\"down\": \"#end\",\"up\": \"#end\",\"north\": \"#side\",\"east\": \"#side\",\"south\": \"#side\",\"west\": \"#side\"}}");
	public static final ModelPart cube_directional = new ModelPart("{\"parent\": \"block/cube_directional\",\"textures\": {\"particle\": \"#null\",\"down\": \"#down\",\"up\": \"#up\",\"north\": \"#north\",\"east\": \"#east\",\"south\": \"#south\",\"west\": \"#west\"}}");
	public static final ModelPart cube_mirrored = new ModelPart("{\"parent\": \"block/cube_mirrored\",\"textures\": {\"particle\": \"#null\",\"down\": \"#down\",\"up\": \"#up\",\"north\": \"#north\",\"east\": \"#east\",\"south\": \"#south\",\"west\": \"#west\"}}");
	public static final ModelPart cube_mirrored_all = new ModelPart("{\"parent\": \"block/cube_mirrored_all\",\"textures\": {\"particle\": \"#all\",\"down\": \"#all\",\"up\": \"#all\",\"north\": \"#all\",\"east\": \"#all\",\"south\": \"#all\",\"west\": \"#all\"}}");
	public static final ModelPart cube_top = new ModelPart("{\"parent\": \"block/cube_top\",\"textures\": {\"particle\": \"#side\",\"down\": \"#side\",\"up\": \"#top\",\"north\": \"#side\",\"east\": \"#side\",\"south\": \"#side\",\"west\": \"#side\"}}");
	
	public ModelPart()
	{
		this("{\"parent\": \"block/cube_all\",\"textures\": {\"particle\": \"#all\",\"down\": \"#all\",\"up\": \"#all\",\"north\": \"#all\",\"east\": \"#all\",\"south\": \"#all\",\"west\": \"#all\"}}");
	}
	public ModelPart(String s)
	{
		this(JavaUtil.getJsonFromString(s));
	}
	public ModelPart(JSONObject jsonfile)
	{
		this.parent = (String) jsonfile.get("parent");
		this.json = jsonfile;
		JSONObject textures = (JSONObject) this.json.get("textures");
		for(Object obj : textures.keySet())
		{
			String key = (String)obj;
			String value = (String) textures.get(key);
			if(value.contains("#"))
				value = value.substring(value.indexOf('#')+1, value.length());
			if(key.equals("particle") && value.equals("null"))
			{
//				System.out.println("particle not implemented!" + textures.toJSONString());
				this.customParticle = true;
			}
			keySet.add(new PairString(key,value));
		}
		this.customSide = getHasCustomSide();
	}
	
	protected boolean getHasCustomSide() 
	{
		String north = getValue("north");
		String south = getValue("south");
		String east = getValue("east");
		String west = getValue("west");
		String up = getValue("up");
		String down = getValue("down");
		
		ArrayList<String> list = JavaUtil.asArray(down,up,north,south,east,west);
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
	
	public String getValue(String key) 
	{
		for(PairString s : this.keySet)
		{
			if(s.obj1.equals(key))
				return s.obj2;
		}
		return null;
	}

}
