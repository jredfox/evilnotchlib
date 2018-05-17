package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.LineEnhanced;
import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public class BasicBlock extends Block implements IBasicBlock{
	
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	public ItemBlock itemblock = null;
	
	public static ArrayList<LangEntry> blocklangs = new ArrayList();//is static so each object doesn't have a new arraylist optimized for finding and going through
	
	public BasicBlock(ResourceLocation id,LangEntry... lang) {
		this(Material.ROCK,id,lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public BasicBlock(Material blockMaterialIn,ResourceLocation id,LangEntry... lang) {
		this(blockMaterialIn,id,null,lang);
	}
	public BasicBlock(Material mat,ResourceLocation id,CreativeTabs tab,LangEntry... lang) {
		this(mat,id,tab,null,lang);
	}
	public BasicBlock(Material mat,ResourceLocation id,CreativeTabs tab,Properties props,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,true,true,true,true,null,true,props,lang);
	}
	
	/**
	 * MUST BE CALLED DURING PREINIT OR LATER
	 * in order for lang to work it needs to be called before post init or it's manual lang registries for you
	 */
	public BasicBlock(Material blockMaterialIn, MapColor blockMapColorIn,ResourceLocation id,CreativeTabs tab,boolean model,boolean register,boolean lang,boolean config,ItemBlock itemblock,boolean useItemBlock,BasicBlock.Properties props,LangEntry... langlist) 
	{
		super(blockMaterialIn, blockMapColorIn);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
		
		//autofill
		populateLang(langlist,unlocalname);
		
		//set properties of the block
		if(props != null)
		{
			if(config)
				props = BasicBlock.getConfiguredBlockProps(props);
			
			this.blockHardness = props.blockHardness;
			this.blockResistance = props.blastResistance;
			this.setHarvestLevel(props.harvestTool, props.harvestLvl);
			
			if(props.mat != null)
				BlockApi.setMaterial(this, props.mat,true,props.harvestTool);
			
			if(props.sound != null)
				this.setSoundType(props.sound);
			
			if(props.flamability != -1 && props.flameEncoragement != -1)
				Blocks.FIRE.setFireInfo(this, props.flameEncoragement, props.flamability);
			
			this.setLightLevel(props.lightValue);
			this.slipperiness = props.slipperiness;
			
			//props that are not defautly constructed
			if(props.advanced)
			{
				this.translucent = props.translucent;
				this.setLightOpacity(props.lightOpacity);
				this.useNeighborBrightness = props.useNeighborBrightness;
				this.enableStats = props.enableStats;
				this.blockParticleGravity = props.blockParticleGravity;
			}
		}
		
		MainJava.blocks.add(this);
		
		if(useItemBlock)
		{
			if(itemblock == null)
				itemblock = new ItemBlock(this);
			itemblock.setRegistryName(id);
			this.itemblock = itemblock;
		}
	}
	public static Properties getConfiguredBlockProps(Properties props) 
	{
		LineEnhanced line = new LineEnhanced(props.toString());
		ConfigBase cfg = MainJava.cfgBlockProps;
		cfg.addLine(line);
		line = (LineEnhanced) cfg.getUpdatedLine(line);
		return new Properties(line);
	}

	public static void populateLang(LangEntry[] langlist,String unlocalname) {
		for(LangEntry entry : langlist)
		{
			entry.langId = "tile." + unlocalname + ".name";
			blocklangs.add(entry);
		}
	}

	public static class Properties{
		public ResourceLocation loc = null;
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
		public Properties(){
			this.loc = new ResourceLocation("null");
		}
		/**
		 * Basic constructor
		 * loc is the resource location of the block
		 * and mat is the material of that block
		 */
		public Properties(ResourceLocation loc,Material mat,String tool,float hard,float resist,int lvl){
			this(loc,mat,tool,hard,resist,lvl,null);
		}
		public Properties(ResourceLocation loc,Material mat,String tool,float hard,float resist,int lvl, SoundType sound){
			this(loc,mat,tool,hard,resist,lvl,sound,-1,-1);
		}
		public Properties(ResourceLocation loc,Material mat,String tool,float hard,float resist,int lvl, SoundType sound,int flamE,int flame){
			this(loc,mat,tool,hard,resist,lvl,sound,flamE,flame,0.6F,0);
		}

		public Properties(ResourceLocation loc,Material mat,String tool,float hard,float resist,int lvl,SoundType sound,int flameE,int flame,float slip,int light){
			this.loc = loc;
			this.mat = mat;
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
		public Properties(LineEnhanced line)
		{
			this.loc = line.getResourceLocation();
			if(line.hasStrMeta)
				this.mat = BlockApi.getMatFromReg(new ResourceLocation(line.strmeta));
			int size = line.heads.size();
			if(size >= 5)
			{
				this.harvestTool = line.getString(0);
				this.blockHardness = line.getFloat(1);
				this.blastResistance = line.getFloat(2);
				this.harvestLvl = line.getInt(3);
				this.sound = BlockApi.getSoundType(new ResourceLocation(line.getString(4)));
			}
			if(size == 7)
			{
				this.flameEncoragement = line.getInt(5);
				this.flamability = line.getInt(6);
				return;
			}
			if(size >= 9)
			{
				this.flameEncoragement = line.getInt(5);
				this.flamability = line.getInt(6);
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
			return "\"" + this.loc.toString() + "\" <\"" + BlockApi.getMaterialLoc(this.mat) + "\">" + " = [" + init + bonus +"]";
		}
	}
	
	@Override
	public boolean register() {
		return this.hasregister;
	}
	@Override
	public boolean registerModel() {
		return this.hasmodel;
	}
	@Override
	public boolean useLangRegistry() {
		return this.haslang;
	}
	@Override
	public boolean useConfigPropterties() {
		return this.hasconfig;
	}
	@Override
	public ItemBlock getItemBlock() {
		return this.itemblock;
	}
	@Override
	public boolean hasItemBlock() {
		return this.getItemBlock() != null;
	}

	@Override
	public List<String> getModelStates() {
		return Arrays.asList(new Object[]{"inventory","normal"});
	}

}
