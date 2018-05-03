package com.EvilNotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.Api.FieldAcessClient;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.main.events.ClientEvents;
import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.minecraft.content.client.models.BasicModel;
import com.EvilNotch.lib.minecraft.content.client.rp.CustomResourcePack;
import com.EvilNotch.lib.minecraft.content.items.IBasicItem;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.Comment;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.IHead;
import com.EvilNotch.lib.util.Line.ILine;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ServerProxy{
	
	public static IBakedModel default_block = null;
	
	@Override
	public void preinit() 
	{
		super.preinit();
		MainJava.isClient = true;
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		FieldAcessClient.cacheFields();
		
		MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
		
		//register user registered menus
		File f = new File(Config.cfg.getParent(),"menulib.cfg");
		ArrayList<Comment> comments = (ArrayList<Comment>)JavaUtil.asArray(new Comment[]{new Comment("Menu Lib Configuration File. Register Other Mod's Main Menus That refuse to do it themselves :("),new Comment("Format is modid:mainmenu = \"class full name\"")});
		ConfigBase cfg = new ConfigBase(f,comments);
		for(ILine line : cfg.lines)
		{
			try
			{
				IHead head = (IHead)line;
				MenuRegistry.registerGuiMenu((Class<? extends GuiScreen>) Class.forName(head.getStringHead()), line.getResourceLocation());
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
	}
	
	@Override
	public void postinit()
	{
		super.postinit();
		menuLibInit();
		
		CustomResourcePack pack = new CustomResourcePack(ServerProxy.dirResourcePack);
		List<IResourcePack> list = (List<IResourcePack>)ReflectionUtil.getObject(Minecraft.getMinecraft(), Minecraft.class, MCPMappings.getField(Minecraft.class, "defaultResourcePacks"));
		list.add(pack);
		long stamp = System.currentTimeMillis();
		try 
		{
			SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
			manager.reloadResourcePack(pack);
			pack.refresh();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(pack);//for future
		JavaUtil.printTime(stamp, "Done Refreshing:");
	}

	/**
	 * Reorder menus or if client overrides using whitelist do only the whitelist
	 */
	public static void menuLibInit() 
	{
		MenuRegistry.reOrder();
		MenuRegistry.setCurrentMenu(Config.currentMenuIndex);
	}

	/**
	 * future:Generate models with textures coming from the registry name
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void modeltest(ModelBakeEvent event)
	{
		//TODO: needs to get updated
		modelpreInit();
		ModelResourceLocation loc = new ModelResourceLocation(new ResourceLocation("minecraft:diamond_block"), "inventory");
		ModelResourceLocation loc2 = new ModelResourceLocation(new ResourceLocation("minecraft:stick"), "inventory");
		IBakedModel diamond = event.getModelRegistry().getObject(loc);
		IBakedModel stick = event.getModelRegistry().getObject(loc2);
		for(IBasicBlock b : MainJava.blocks)
		{
			Block block = (Block)b;
			for(String s : b.getModelStates() )
				event.getModelRegistry().putObject(new ModelResourceLocation(BlockApi.getBlockString(block),s),new BasicModel(diamond,"minecraft:blocks/grass_top") );
		}
		for(IBasicItem i : MainJava.items)
		{
			Item item = (Item)i;
			ModelResourceLocation itemloc = new ModelResourceLocation(BlockApi.getItemString(item),"inventory");
			event.getModelRegistry().putObject(itemloc,new BasicModel(stick,"minecraft:blocks/coal_ore") );
		}
	}
	
	public static void modelpreInit() 
	{
		for(IBasicItem i : MainJava.items)
	    {
		   if(i.registerModel())
		   {
			  Item item = (Item)i;
			  ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(BlockApi.getItemString(item), "inventory"));
		   }
	   }
	   for(IBasicBlock i : MainJava.blocks)
	   {
		   if(i.registerModel())
		   {
			   Block b = (Block)i;
			   ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0,  new ModelResourceLocation(BlockApi.getBlockString(b), "inventory"));
			   ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0,  new ModelResourceLocation(BlockApi.getBlockString(b), "normal"));
		   }
	   }
	}

	public static String currentLang() {
		return CustomResourcePack.getCurrentLang();
	}



}
