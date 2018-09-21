package com.evilnotch.lib.minecraft.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.evilnotch.lib.Api.FieldAcess;
import com.evilnotch.lib.Api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.MinecraftUtil;
import com.evilnotch.lib.minecraft.content.recipe.ShapelessRecipe;
import com.evilnotch.lib.util.simple.PairObj;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.registries.IForgeRegistry;

public class GeneralRegistry {
	
	//adding
	public static List<ICommand> cmds = new ArrayList();
	public static HashMap<ResourceLocation,Material> blockmats = new HashMap();
	public static HashMap<ResourceLocation,SoundType> soundTypes = new HashMap();
	public static List<PairObj<String,Object>> gameRules = new ArrayList();
	
	//removing
	public static Set<String> cmdRemove = new HashSet();
	public static Set<String> gameRulesRemove = new HashSet();
	
	public static void load()
	{
		System.out.println("loading mats");
		//register block materials
		blockmats.put(new ResourceLocation("AIR"), Material.AIR);
		blockmats.put(new ResourceLocation("ANVIL"), Material.ANVIL);
		blockmats.put(new ResourceLocation("BARRIER"), Material.BARRIER);
		blockmats.put(new ResourceLocation("CACTUS"), Material.CACTUS);
		blockmats.put(new ResourceLocation("CAKE"), Material.CAKE);
		blockmats.put(new ResourceLocation("CIRCUITS"), Material.CIRCUITS);
		blockmats.put(new ResourceLocation("CARPET"), Material.CARPET);
		blockmats.put(new ResourceLocation("CLAY"), Material.CLAY);
		blockmats.put(new ResourceLocation("CLOTH"), Material.CLOTH);
		blockmats.put(new ResourceLocation("CORAL"), Material.CORAL);
		blockmats.put(new ResourceLocation("CRAFTED_SNOW"), Material.CRAFTED_SNOW);
		blockmats.put(new ResourceLocation("DRAGON_EGG"), Material.DRAGON_EGG);
		blockmats.put(new ResourceLocation("FIRE"), Material.FIRE);
		blockmats.put(new ResourceLocation("GLASS"), Material.GLASS);
		blockmats.put(new ResourceLocation("GOURD"), Material.GOURD);
		blockmats.put(new ResourceLocation("GRASS"), Material.GRASS);
		blockmats.put(new ResourceLocation("GROUND"), Material.GROUND);
		blockmats.put(new ResourceLocation("ICE"), Material.ICE);
		blockmats.put(new ResourceLocation("IRON"), Material.IRON);
		blockmats.put(new ResourceLocation("LAVA"), Material.LAVA);
		blockmats.put(new ResourceLocation("LEAVES"), Material.LEAVES);
		blockmats.put(new ResourceLocation("PACKED_ICE"), Material.PACKED_ICE);
		blockmats.put(new ResourceLocation("PISTON"), Material.PISTON);
		blockmats.put(new ResourceLocation("PLANTS"), Material.PLANTS);
		blockmats.put(new ResourceLocation("PORTAL"), Material.PORTAL);
		blockmats.put(new ResourceLocation("REDSTONE_LIGHT"), Material.REDSTONE_LIGHT);
		blockmats.put(new ResourceLocation("ROCK"), Material.ROCK);
		blockmats.put(new ResourceLocation("SAND"), Material.SAND);
		blockmats.put(new ResourceLocation("SNOW"), Material.SNOW);
		blockmats.put(new ResourceLocation("SPONGE"), Material.SPONGE);
		blockmats.put(new ResourceLocation("STRUCTURE_VOID"), Material.STRUCTURE_VOID);
		blockmats.put(new ResourceLocation("TNT"), Material.TNT);
		blockmats.put(new ResourceLocation("VINE"), Material.VINE);
		blockmats.put(new ResourceLocation("WATER"), Material.WATER);
		blockmats.put(new ResourceLocation("WEB"), Material.WEB);
		blockmats.put(new ResourceLocation("WOOD"), Material.WOOD);
		 
		System.out.println("loading sound types");
		//register soundtypes
		soundTypes.put(new ResourceLocation("ANVIL"), SoundType.ANVIL);
		soundTypes.put(new ResourceLocation("CLOTH"), SoundType.CLOTH);
		soundTypes.put(new ResourceLocation("GLASS"), SoundType.GLASS);
		soundTypes.put(new ResourceLocation("GROUND"), SoundType.GROUND);
		soundTypes.put(new ResourceLocation("LADDER"), SoundType.LADDER);
		soundTypes.put(new ResourceLocation("METAL"), SoundType.METAL);
		soundTypes.put(new ResourceLocation("PLANT"), SoundType.PLANT);
		soundTypes.put(new ResourceLocation("SAND"), SoundType.SAND);
		soundTypes.put(new ResourceLocation("SLIME"), SoundType.SLIME);
		soundTypes.put(new ResourceLocation("SNOW"), SoundType.SNOW);
		soundTypes.put(new ResourceLocation("STONE"), SoundType.STONE);
		soundTypes.put(new ResourceLocation("WOOD"), SoundType.WOOD);
	}
	
	public static void registerCommand(ICommand cmd){
		cmds.add(cmd);
	}
	public static List<ICommand> getCmdList(){
		return cmds;
	}
	
	public static void removeVanillaCommand(String name){
		cmdRemove.add(name);
	}
	/**
	 * remove all commands with this name
	 */
	public static void removeAllCMD(String name)
	{
		Iterator<ICommand> it = cmds.iterator();
		while(it.hasNext())
		{
			ICommand cmd = it.next();
			if(cmd.getName().equals(name))
			{
				it.remove();
			}
		}
	}
	/**
	 * remove first command with this name
	 */
	public static void removeCMD(String name)
	{
		Iterator<ICommand> it = cmds.iterator();
		while(it.hasNext())
		{
			ICommand cmd = it.next();
			if(cmd.getName().equals(name))
			{
				it.remove();
				break;
			}
		}
	}

	public static void replaceVanillaCommand(String name, ICommand cmd){
		removeVanillaCommand(name);
		registerCommand(cmd);
	}
	
	public static void registerGameRule(String name,boolean value)
	{
		gameRules.add(new PairObj<String,Object>(name,value));
	}
	public static void registerGameRule(String name,int value)
	{
		gameRules.add(new PairObj<String,Object>(name,value));
	}
	
	public static void removeActiveCommands(MinecraftServer server)
	{
    	//removes half of it
    	ICommandManager manager = server.getCommandManager();
    	Map<String,ICommand> map = manager.getCommands();
    	Iterator<String> keys = map.keySet().iterator();
    	while(keys.hasNext())
    	{
    		String s = keys.next();
    		if(cmdRemove.contains(s))
    		{
    			if(Config.debug)
    				System.out.println("found and removing:" + s);
    			keys.remove();
    		}
    	}
    	Set<ICommand> cmds = (Set<ICommand>) ReflectionUtil.getObject(manager, CommandHandler.class, FieldAcess.commandSet);
    	Iterator<ICommand> it = cmds.iterator();
    	while(it.hasNext())
    	{
    		ICommand cmd = it.next();
    		String s = cmd.getName();
    		if(cmdRemove.contains(s))
    		{
    			if(Config.debug)
    				System.out.println("found and removing:" + s);
    			it.remove();
    		}
    	}
	}
	
	public static void removeVanillaGameRule(String name)
	{
		gameRulesRemove.add(name);
	}
	public static void removeActiveGameRules(GameRules g)
	{
		TreeMap<String, Object> map = (TreeMap<String, Object>) ReflectionUtil.getObject(g, GameRules.class, FieldAcess.gameRuleMap);
		for(String s : gameRulesRemove)
		{
			map.remove(s);
		}
	}
	public static void removeModGameRule(String s)
	{
		Iterator<PairObj<String,Object>> it = gameRules.iterator();
		while(it.hasNext())
		{
			PairObj<String,Object> pair = it.next();
			if(s.equals(pair.getKey()))
			{
				System.out.println("found and removing:" + pair);
				it.remove();
				break;
			}
		}
	}

	public static void injectGameRules(GameRules g)
	{
		for(PairObj<String,Object> pair : gameRules)
		{
			Object value = pair.getValue();
			GameRules.ValueType type = value instanceof Boolean ? GameRules.ValueType.BOOLEAN_VALUE : GameRules.ValueType.NUMERICAL_VALUE;
			MinecraftUtil.addGameRule(g, pair.getKey(), value, type);
		}
	}
	public static void addShapelessRecipe(IForgeRegistry<IRecipe> reg,ResourceLocation id,ItemStack output, ItemStack... params) 
	{
		reg.register(new ShapelessRecipe(id,output,params).setRegistryName(id));
	}

}
