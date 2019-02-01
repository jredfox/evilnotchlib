package com.evilnotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.api.FieldAcessClient;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.eventhandler.ClientEvents;
import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.content.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.content.block.IBasicBlock;
import com.evilnotch.lib.minecraft.content.client.ClientUUID;
import com.evilnotch.lib.minecraft.content.client.block.ModelPart;
import com.evilnotch.lib.minecraft.content.client.block.StateMapperSupreme;
import com.evilnotch.lib.minecraft.content.item.IBasicItem;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairString;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ServerProxy{
	
	public static File root = null;
	public static final HashMap<String,Boolean> compiledTracker = new HashMap();
	

	@Override
	public void proxyStart()
	{
		LoaderMain.isClient = true;
	}
	
	@Override
	public void preinit(FMLPreInitializationEvent e) 
	{
		super.preinit(e);
		
		FieldAcessClient.cacheFields();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		if(Config.debug)
			ClientCommandHandler.instance.registerCommand(new ClientUUID());
	}
	@Override
	public void jsonGen() throws Exception
	{
		if(!LoaderMain.isDeObfuscated)
		{
			return;
		}
		checkRootFile();
		boolean flag = false;
		for(IBasicItem i : LoaderItems.items)
		{
			if(!i.registerModel())
			{
				continue;
			}
			String domain = i.getRegistryName().getResourceDomain();
			if(!compiledTracker.containsKey(domain))
				compiledTracker.put(domain, MinecraftUtil.isModCompiled(domain));
			boolean compiled = compiledTracker.get(domain);
			if(compiled)
				continue;
			
			JSONObject json = null;
			ResourceLocation loc = i.getRegistryName();
			if(i instanceof ItemAxe || i instanceof ItemHoe || i instanceof ItemPickaxe || i instanceof ItemSpade || i instanceof ItemSword)
			{
				json = getJSONItem("item/handheld", i);
				File file = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
				if(!file.exists())
				{
					flag = true;
					JavaUtil.saveJSONSafley(json, file);
				}
			}
			else if(i.isMeta())
			{
				for(int index=0;index<=i.getMaxMeta();index++)
				{
					json = getJSONItem("item/generated", i,index);
					File file = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + "_" + index + ".json");
					if(!file.exists())
					{
						flag = true;
						JavaUtil.saveJSONSafley(json, file);
					}
				}
			}
			else
			{
				//for both item armor and regular items this is the parent
				json = getJSONItem("item/generated", i);
				File file = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
				if(!file.exists())
				{
					flag = true;
					JavaUtil.saveJSONSafley(json, file);
				}
			}
		}
		for(IBasicBlock b : LoaderBlocks.blocks)
		{
			if(!b.registerModel())
			{
				if(Config.debug)
					System.out.println("skipping model no model reeg found:" + b.getRegistryName());
				continue;
			}
			ResourceLocation loc = b.getRegistryName();
			
			String domain = loc.getResourceDomain();
			if(!compiledTracker.containsKey(domain))
				compiledTracker.put(domain, MinecraftUtil.isModCompiled(domain));
			boolean compiled = compiledTracker.get(domain);
			if(compiled)
			{
				if(Config.debug)
					System.out.println("skipping model gen for:" + loc);
				continue;
			}
			
			//blockstate gen
			List<String> names = b.getBlockStatesNames();
			
			//block model gen
			if(b.isMeta())
			{
				//blockstates
				JSONObject bs = getJSONBlockState(names,b);
				File fbs = new File(root,loc.getResourceDomain() + "/blockstates/" + loc.getResourcePath() + ".json");
				if(!fbs.exists())
					flag = true;
				JavaUtil.saveJSONSafley(bs, fbs);
				
				for(String s : names)
				{
					String prop = s.split("=")[1];
					
					JSONObject block = getModelBlock(b,prop);
					File mBlockFile = new File(root,loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + "_" + prop + ".json");
					if(!mBlockFile.exists())
						flag = true;
					JavaUtil.saveJSONSafley(block, mBlockFile);
					
					//itemblock part
					JSONObject item = getJSONBlockItem(b,prop);
					File itemFile = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + "_" + prop + ".json");
					if(!itemFile.exists())
					{
						flag = true;
						JavaUtil.saveJSONSafley(item, itemFile);
					}
				}
			}
			else
			{
				JSONObject block = getModelBlock(b,names.get(0));
				File bmodel = new File(root,loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + ".json");
				
				if(!bmodel.exists())
				{
					flag = true;
					JavaUtil.saveJSONSafley(block, bmodel);
				}
				
				JSONObject item = getJSONBlockItem(b,names.get(0));
				File itemFile = new File(root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
				
				if(!itemFile.exists())
				{
					flag = true;
					JavaUtil.saveJSONSafley(item, itemFile);
				}
			}
		}
		if(flag)
		{
			System.out.println("refreshing resources as a new json has been generated");
			Minecraft.getMinecraft().refreshResources();
		}
	}

	public static JSONObject getJSONBlockItem(IBasicBlock b,String name) {
		JSONObject json = new JSONObject();
		ResourceLocation loc = b.getRegistryName();
		json.put("parent", loc.getResourceDomain() + ":block/" + (name.equals("normal") ? loc.getResourcePath() : loc.getResourcePath() + "_" + name) );
		return json;
	}

	public static JSONObject getJSONBlockState(List<String> names,IBasicBlock b) 
	{
		JSONObject json = new JSONObject();
		JSONObject varients = new JSONObject();
		JSONObject normal = new JSONObject();
		
		if(b.isMeta())
		{
			IProperty p = b.getStateProperty();
			for(String s : names)
			{
				JSONObject model = new JSONObject();
				String name = s.split("=")[1];
				model.put("model", b.getRegistryName() + "_" + name);
				varients.put(p.getName() + "=" + name, model);
			}
		}
		else
		{
			normal.put("model", b.getRegistryName().toString());
			varients.put("normal", normal);
		}
		
		json.put("variants", varients);
		
		return json;
	}

	/**
	 * supports textures with multiple states and multi textured blocks at the same time
	 */
	public static JSONObject getModelBlock(IBasicBlock b,String stateValue) 
	{
		ModelPart base = b.getModelPart();
		Block block = (Block)b;
		JSONObject json = new JSONObject();
		json.put("parent", base.parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = block.getRegistryName();
		String particle = "particle";
		for(PairString pair : base.keySet)
		{
			if(pair.obj1.equals(particle) && base.customParticle)
			{
				textures.put(particle, loc.getResourceDomain() + ":blocks/" + (stateValue.equals("normal") ? b.getTextureName() + "_" + particle : b.getTextureName() + "_" + stateValue + "_" + particle) );
				continue;
			}
			
			String texture = loc.getResourceDomain() + ":blocks/" + (stateValue.equals("normal") ? b.getTextureName() : b.getTextureName() + "_" + stateValue);
			if(base.customSide)
			{
				texture += "_" + pair.getValue();
			}
			textures.put(pair.getValue(),texture);
		}
		json.put("textures",textures);
		return json;
	}
	
	@Override
	public void lang(){
		LangRegistry.registerLang();
	}
	
	public static JSONObject getJSONItem(String parent,IBasicItem i) {
		return getJSONItem(parent,i,0);
	}

	public static JSONObject getJSONItem(String parent,IBasicItem i,int meta) {
		Item item = (Item)i;
		JSONObject json = new JSONObject();
		json.put("parent", parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = item.getRegistryName();
		if(i.isMeta())
		{
			textures.put("layer0", loc.getResourceDomain() + ":items/" + i.getTextureName() + "_" + meta);
		}
		else
			textures.put("layer0", loc.getResourceDomain() + ":items/" + i.getTextureName());
		json.put("textures",textures);
		return json;
	}
	
	public static void checkRootFile() {
		if(root != null)
			return;
		root = new File(Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile(),"src/main/resources/assets");
		if(!root.exists())
			root.mkdirs();
	}


	@Override
	public void initMod()
	{
		super.initMod();
	}
	
	@Override
	public void postinit()
	{
		super.postinit();
	}

	/**
	 * future:Generate models with textures coming from the registry name
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void modeltest(ModelRegistryEvent event)
	{
		modelpreInit();
	}
	
	public static void modelpreInit() 
	{
		System.out.println("Loading MODELS");
		for(IBasicItem i : LoaderItems.items)
	    {
		   if(i.registerModel())
		   {
			  Item item = (Item)i;
			  if(i.isMeta())
			  {
				 List<ResourceLocation> locs = new ArrayList();
				 for(int index=0;index<=i.getMaxMeta();index++)
				 {
					 ModelLoader.setCustomModelResourceLocation(item, index, new ModelResourceLocation(item.getRegistryName() + "_" + index,"inventory"));
				 }
			  }
			  else
			  {
				  ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(BlockApi.getItemString(item), "inventory"));
			  }
		   }
	   }
	   for(IBasicBlock i : LoaderBlocks.blocks)
	   {
		   if(i.registerModel())
		   {
			   Block b = (Block)i;
			   Item item = Item.getItemFromBlock(b);
			   
			   if(i.isMeta() )
			   {
					ModelLoader.setCustomStateMapper((Block) b, new StateMapperSupreme());
					HashMap<Integer,ModelResourceLocation> metas = i.getModelMap();
					Iterator<Map.Entry<Integer,ModelResourceLocation>> it = metas.entrySet().iterator();
					while(it.hasNext())
					{
						Map.Entry<Integer, ModelResourceLocation> pair = it.next();
						int meta = pair.getKey();
						ModelResourceLocation loc = pair.getValue();
						ModelLoader.setCustomModelResourceLocation(item, meta, loc);
					}
			   }
			   else
			   {
				   for(String s : i.getModelStates())
				   {
					   ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(BlockApi.getBlockString(b), s));
			   	   }
			   }
		   }
	   }
	}

	public static EntityPlayer getPlayer() {
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

}
