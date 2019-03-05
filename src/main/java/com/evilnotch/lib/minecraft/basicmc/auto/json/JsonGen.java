package com.evilnotch.lib.minecraft.basicmc.auto.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.evilnotch.lib.main.loader.LoaderGen;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;
import com.evilnotch.lib.minecraft.basicmc.client.model.StateMapperSupreme;
import com.evilnotch.lib.minecraft.util.BlockUtil;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairString;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import scala.reflect.api.Trees.BlockApi;
@SideOnly(Side.CLIENT)
public class JsonGen {
	
	public static List<BasicBlockJSON> blocks = new ArrayList<BasicBlockJSON>();
	public static List<BasicBlockJSONMeta> blocks_meta = new ArrayList<BasicBlockJSONMeta>();
	
	public static List<BasicItemJSON> items = new ArrayList<BasicItemJSON>();
	public static List<BasicItemJSONMeta> items_meta = new ArrayList<BasicItemJSONMeta>();
	
	protected static void registerBlockJson(Block b, ModelPart part)
	{
		validateDomain(b);
		blocks.add(new BasicBlockJSON(b, part));
	}

	protected static void registerBlockMetaJson(Block b, ModelPart part, IProperty p)
	{
		validateDomain(b);
		blocks_meta.add(new BasicBlockJSONMeta(b, part, p));
	}
	
	protected static void registerItemJson(Item i)
	{
		validateDomain(i);
		items.add(new BasicItemJSON(i));
	}
	
	protected static void registerItemMetaJson(Item i, int maxMeta)
	{
		validateDomain(i);
		items_meta.add(new BasicItemJSONMeta(i, maxMeta));
	}
	
	private static void validateDomain(IForgeRegistryEntry.Impl b) 
	{
		if(b.getRegistryName().getResourceDomain().equals("minecraft"))
			throw new IllegalArgumentException("Json Gen domain cannot be minecraft!" + b.getRegistryName());
	}
	
	private static boolean isDirty = false;
	
	public static void genJSONS() throws IOException
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		
		LoaderGen.checkRootFile();
		
		for(BasicItemJSON i : items)
		{
			if(MinecraftUtil.isModCompiled(i.getResourceLocation().getResourceDomain()))
				continue;
			JSONObject json = getJSONItem(getParentModel(i.getObject()), i, 0, false);
			ResourceLocation loc = i.getResourceLocation();
			File file = new File(LoaderGen.root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
			saveIfJSON(json, file);
		}
		for(BasicItemJSONMeta item : items_meta)
		{
			if(MinecraftUtil.isModCompiled(item.getResourceLocation().getResourceDomain()))
				continue;
			for(int index=0;index<=item.maxMeta;index++)
			{
				JSONObject json = getJSONItem(getParentModel(item.getObject()), item, index, true);
				ResourceLocation loc = item.getResourceLocation();
				File file = new File(LoaderGen.root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + "_" + index + ".json");
				saveIfJSON(json, file);
			}
		}
		
		for(BasicBlockJSON i : blocks)
		{
			if(MinecraftUtil.isModCompiled(i.getResourceLocation().getResourceDomain()))
				continue;
			
			//models/block
			JSONObject json = getJSONBlock(i, null);
			ResourceLocation loc = i.getResourceLocation();
			File file = new File(LoaderGen.root, loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + ".json");
			saveIfJSON(json, file);
				
			//models/item/itemblock
			File itemFile = new File(LoaderGen.root, loc.getResourceDomain() + "/models/item/itemblock/" + loc.getResourcePath() + ".json");
			JSONObject item = getJSONItemBlock(i, null);
			saveIfJSON(item, itemFile);
			
			//blockstates
			File blockstateFile = new File(LoaderGen.root, loc.getResourceDomain() + "/blockstates/" + loc.getResourcePath() + ".json");
			JSONObject blockState = getJSONBlockState(i, null);
			saveIfJSON(blockState, blockstateFile);
		}
		
		for(BasicBlockJSONMeta i : blocks_meta)
		{
			if(MinecraftUtil.isModCompiled(i.getResourceLocation().getResourceDomain()))
				continue;
			
			IProperty p = i.getProperty();
			
			for(IBlockState state : i.getObject().blockState.getValidStates())
			{
				//models/block
				JSONObject json = getJSONBlock(i, state);
				ResourceLocation loc = i.getResourceLocation();
				File file = new File(LoaderGen.root, loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + "_" + BlockUtil.getPropertyValue(state, p) + ".json");
				saveIfJSON(json, file);
				
				//model/item/itemblock
				File itemFile = new File(LoaderGen.root, loc.getResourceDomain() + "/models/item/itemblock/" + loc.getResourcePath() + "_" + BlockUtil.getPropertyValue(state, p) + ".json");
				JSONObject item = getJSONItemBlock(i, state);
				saveIfJSON(item, itemFile);
				
				//blockstates
				File blockstateFile = new File(LoaderGen.root, loc.getResourceDomain() + "/blockstates/" + loc.getResourcePath() + ".json");
				JSONObject blockState = getJSONBlockState(i, i.getObject().getBlockState().getValidStates());
				saveIfJSON(blockState, blockstateFile);
			}
		}
		
		if(isDirty)
		{
			System.out.println("JSON Genration Occured Refreshing resources....");
			Minecraft.getMinecraft().refreshResources();
			isDirty = false;
		}
		clear();
	}
	
	private static void clear()
	{
		blocks.clear();
		blocks_meta.clear();
		items.clear();
		items_meta.clear();
	}

	private static void saveIfJSON(JSONObject json, File file) throws IOException 
	{
		File sync = LoaderGen.getSyncFile(file);
		if(JavaUtil.saveIfJSON(json, file) || JavaUtil.saveIfJSON(json, sync))
			isDirty = true;
	}

	/**
	 * returns the entire blockstate json
	 */
	public static JSONObject getJSONBlockState(IBasicBlockJSON i, List<IBlockState> states) 
	{
		JSONObject json = new JSONObject();
		JSONObject variants = new JSONObject();
		json.put("variants", variants);
		
		if(states == null)
		{
			JSONObject normal = new JSONObject();
			normal.put("model", i.getResourceLocation().toString());
			variants.put("normal", normal);
		}
		else 
		{
			for(IBlockState state : states)
			{	
				IProperty prop = ((BasicBlockJSONMeta) i).getProperty();
				String stateName = BlockUtil.getBlockStateName(state, prop);
				JSONObject model = new JSONObject();
				variants.put(stateName, model);
				model.put("model", i.getResourceLocation().toString() + "_" + BlockUtil.getPropertyValue(state, prop));
			}
		}
		return json;
	}

	public static JSONObject getJSONItemBlock(IBasicBlockJSON i, IBlockState state) 
	{
		JSONObject json = new JSONObject();
		json.put("parent", i.getResourceLocation().getResourceDomain() + ":block/" + i.getResourceLocation().getResourcePath() + (state != null ? "_" + BlockUtil.getPropertyValue(state, ((BasicBlockJSONMeta) i).getProperty()) : "") );
		return json;
	}

	public static String getParentModel(Item item) 
	{
		if(item instanceof ItemTool)
			return "item/handheld";
		else if(item instanceof ItemShield)
			return "item/shield";
		else if(item instanceof ItemSkull)
			return "item/skull";
			
		return "item/generated";
	}

	public static JSONObject getJSONItem(String parent, IBasicItemJSON<? extends Item> item, int meta, boolean isMeta)
	{
		JSONObject json = new JSONObject();
		json.put("parent", parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = item.getResourceLocation();
		textures.put("layer0", loc.getResourceDomain() + ":items/" + item.getTextureName() + (isMeta ? "_" + meta : "") );
		json.put("textures", textures);
		return json;
	}
	
	public static JSONObject getJSONBlock(IBasicBlockJSON block, IBlockState state)
	{
		JSONObject json = new JSONObject();
		json.put("parent", block.getModelPart().parent);
		Block b = (Block) block.getObject();
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = block.getResourceLocation();

		for(PairString s : block.getModelPart().getParts())
		{
			String side = s.getValue();
			textures.put(side, loc.getResourceDomain() + ":blocks/" + block.getTextureName() + (state != null ? "_" + BlockUtil.getPropertyValue(state, ((IBasicBlockMetaJSON)block).getProperty() ) : "") + (side.equals("all") ? "" : "_" + side) );
		}
		json.put("textures", textures);
		return json;
	}

	public static void registerModels() 
	{
		for(BasicItemJSON gen : items)
		{
			Item item = gen.getObject();
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(gen.loc, "inventory"));
		}
		
		for(BasicItemJSONMeta gen : items_meta)
		{
			Item item = gen.getObject();
			for(int index=0;index<=gen.maxMeta;index++)
				ModelLoader.setCustomModelResourceLocation(item, index, new ModelResourceLocation(gen.loc + "_" + index, "inventory"));
		}
		
		for(BasicBlockJSON gen : blocks)
		{
			Block b = gen.getObject();
			Item item = ItemBlock.getItemFromBlock(b);
			ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(gen.loc.getResourceDomain() + ":itemblock/" + gen.loc.getResourcePath(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(gen.loc, "normal"));
		}
		
		for(BasicBlockJSONMeta gen : blocks_meta)
		{
			Block b = gen.getObject();
			Item item = ItemBlock.getItemFromBlock(b);
			ModelLoader.setCustomStateMapper(b, new StateMapperSupreme());
			for(IBlockState state : b.getBlockState().getValidStates())
			{
				int meta = b.getMetaFromState(state);
				ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(gen.loc.getResourceDomain() + ":itemblock/" + gen.loc.getResourcePath() + "_" + BlockUtil.getPropertyValue(state, gen.property), "inventory"));
//				ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(gen.loc + "_" + BlockApi.getPropertyValue(state, gen.property), BlockApi.getBlockStateName(state, gen.property)));
			}
		}
		if(!LoaderMain.isDeObfuscated)
			clear();
	}
	
}
