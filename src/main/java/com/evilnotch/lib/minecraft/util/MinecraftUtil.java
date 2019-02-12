package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.loader.LoaderFields;
import com.evilnotch.lib.minecraft.event.EventCanceler;
import com.evilnotch.lib.util.simple.PairObj;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class MinecraftUtil {
	
   public static void addGameRule(GameRules g,String ruleName, Object init, ValueType type) 
   {
	   if(init instanceof Boolean)
		   addGameRuleBoolean(g,ruleName,init,type);
	   else if(init instanceof Integer)
		   addGameInteger(g,ruleName,init,type);
   }
	 
   public static void addGameInteger(GameRules g, String ruleName, Object init, ValueType type) 
   {
	   if(!g.hasRule(ruleName))
		   g.addGameRule(ruleName, "" + init, type);
	   else
		   g.addGameRule(ruleName, "" + g.getInt(ruleName), type);
   }

   public static void addGameRuleBoolean(GameRules g, String ruleName, Object init, ValueType type) 
   {
	   if(!g.hasRule(ruleName))
		   g.addGameRule(ruleName, "" + init, type);
	   else
		   g.addGameRule(ruleName, "" + g.getBoolean(ruleName), type);
   }

   public static SoundEvent getSoundEvent(ResourceLocation soundEventIN) 
   {
	 return SoundEvent.REGISTRY.getObject(soundEventIN);
   }
   
   public static ResourceLocation getSoundLoc(SoundEvent sound)
   {
	 return SoundEvent.REGISTRY.getNameForObject(sound);
   }
   
   /**
    * uses reflection since the world method is protected
    */
   public static boolean isChunkLoaded(World w,int x,int z,boolean allowEmpty)
   {
	   try
	   {
		   Method m = LoaderFields.chunkLoaded;
		   m.setAccessible(true);
		   return (Boolean) m.invoke(w, x,z,allowEmpty);
	   }
	   catch(Throwable t)
	   {
		   t.printStackTrace();
	   }
	   return false;
   }
	public static final HashMap<String,Boolean> compiledTracker = new HashMap();
   /**
    * tells whether or not a mod is compiled goes through the entire process so use a cache or something don't check this alot
    */
   public static boolean isModCompiled(String modid)
   {
	   if(!compiledTracker.containsKey(modid))
	   {
		   File source = Loader.instance().getIndexedModList().get(modid).getSource();
		   boolean isJar = source == null ? false : source.isFile() && isModExtension(source.getName());
		   compiledTracker.put(modid, isJar);
		   return isJar;
	   }
	   return compiledTracker.get(modid);
   }
   /**
    * supports 1.7.10 .zip and .jar mods
    */
   public static boolean isModExtension(String name) 
   {
	   return name.endsWith(".jar") || name.endsWith(".zip");
   }
   
   public static String getActiveModDomain()
   {
	   ModContainer mc = Loader.instance().activeModContainer();
	   String prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
	   return prefix;
   }
   
   public static void cancelCurrentEvent(Class clazz, boolean setCanceled) 
   {
	   cancelEvent(null, clazz, setCanceled);
   }
   
   /**
    * current event is the event to ignore. Pass in null not to ignore the current event. This also works for uncanceling events.
    */
   public static void cancelEvent(Event currentEvent, Class clazz, boolean setCanceled) 
   {
	   Side side = FMLCommonHandler.instance().getEffectiveSide();
	   if(side == Side.CLIENT)
		   LibEvents.cancelerClient = new EventCanceler(currentEvent, clazz, setCanceled, side);
	   else
		   LibEvents.cancelerServer = new EventCanceler(currentEvent, clazz, setCanceled, side);
   }
   
   /**
    * works with all ICommands
    */
   public static boolean checkPermission(EntityPlayerMP p, String name) 
   {
	   ICommand cmd = getCommand(p.mcServer, name);
	   System.out.println(cmd.checkPermission(p.mcServer, p));
	   return cmd.checkPermission(p.mcServer, p);
   }
   
   /**
    * requires the ICommand to be instanceof CommandBase
    */
   public static int getCommandLevel(EntityPlayerMP p, String name) 
   {
	   CommandBase cmd = (CommandBase) getCommand(p.mcServer,name);
	   return cmd.getRequiredPermissionLevel();
   }

   public static ICommand getCommand(MinecraftServer mcServer, String name) 
   {
	   Map<String,ICommand> cmds =  mcServer.commandManager.getCommands();
	   for(String s : cmds.keySet())
	   {
		   if(s.equals(name))
			   return cmds.get(s);
	   }
	   return null;
   }

   /**
    * internal do not use unless your an ICommand overriding checkPermisions(). Instead use canUseCommand() which checks the overriden ICommand can be used by the sender. used by seed command so far
    */
   public static boolean canUseCommand(EntityPlayerMP player, int permLevel, String commandName)
   {
       if (player.mcServer.getPlayerList().canSendCommands(player.getGameProfile()))
       {
          UserListOpsEntry userlistopsentry = (UserListOpsEntry)player.mcServer.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
          if (userlistopsentry != null)
          {
            return userlistopsentry.getPermissionLevel() >= permLevel;
          }
          else
          {
              return player.mcServer.getOpPermissionLevel() >= permLevel;
          }
      }
      return false;
   }

}
