package com.evilnotch.lib.asm.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.classwriter.MCWriter;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.util.JavaUtil;

import jredfox.clfix.LaunchClassLoaderFix;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.Loader;

public class Transformer implements IClassTransformer
{
	
	public Transformer()
	{
		LaunchClassLoaderFix.stopMemoryOverflow(this.getClass().getClassLoader());
		allowMojangASM();
	}
	
    public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
    {
    	"net.minecraft.server.management.PlayerList", //UUIDPatcher
    	"net.minecraft.tileentity.TileEntityFurnace",
    	"net.minecraft.client.gui.inventory.GuiFurnace",
    	"net.minecraft.item.ItemStack",
    	"net.minecraft.item.ItemBlock",
    	"net.minecraft.network.play.server.SPacketUpdateTileEntity",
    	"net.minecraft.network.NetHandlerPlayServer",
    	"net.minecraft.entity.Entity",//capabilities start here
    	"net.minecraft.tileentity.TileEntity",
    	"net.minecraft.world.World",
    	"net.minecraft.world.storage.WorldInfo",
    	"net.minecraft.world.chunk.Chunk",
    	"net.minecraft.world.chunk.storage.AnvilChunkLoader",
    	"net.minecraft.client.Minecraft",
    	"net.minecraft.server.integrated.IntegratedServer",
    	"net.minecraft.util.WeightedSpawnerEntity",
    	"net.minecraft.enchantment.Enchantment",
    	"net.minecraft.client.multiplayer.WorldClient",
    	"net.minecraft.client.renderer.entity.RenderManager",
    	"net.minecraft.client.gui.GuiNewChat",
    	"net.minecraft.client.gui.GuiIngame",
    	"net.minecraft.client.renderer.ImageBufferDownload",//Allow Skin Transparency
    	"net.minecraft.server.network.NetHandlerLoginServer", //UUIDPatcher
    	"net.minecraftforge.fml.common.network.handshake.NetworkDispatcher", //UUIDPatcher
    	"net.minecraftforge.common.DimensionManager",//Unload Dimension Patch
    	"com.mojang.authlib.GameProfile"//AT the class in case of reflection failing on final via modern java 8
    });
    
    public static Set<String> done = new HashSet();
    private static boolean i = false;
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform)
    {	
    	if(!i && classToTransform != null && (transformedName.equals("net.minecraft.client.main.Main") || transformedName.equals("net.minecraft.server.MinecraftServer")) )
    	{
        	System.out.println("Main Class:\t" + transformedName);
			init();
    	}
        int index = clazzes.indexOf(transformedName);
        return (index == -1 || classToTransform == null) ? classToTransform : transform(index, classToTransform, FMLCorePlugin.isObf);
    }
	
	public static byte[] transform(int index, byte[] classToTransform, boolean obfuscated)
    {
    	String name = clazzes.get(index);
    	done.add(name);
		if(!i)
		{
			System.err.println("Error Unable to Find Main Class! Please report to: https://github.com/jredfox/evilnotchlib/issues");
			init();
		}
    	System.out.println("Transforming: " + name + " index:" + index);
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classToTransform);
            classReader.accept(classNode, 0);
            String inputBase = "assets/evilnotchlib/asm/" + (obfuscated ? "srg/" : "deob/");
            switch(index)
            {
                case 0:
                	GeneralTransformer.injectUUIDPatcher(classNode);
                break;
                
                case 1:
                	if(!ConfigCore.asm_furnace)
                	{
                		print(name);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "TileEntityFurnace", "readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", "func_145839_a");
                	ASMHelper.replaceMethod(classNode, inputBase + "TileEntityFurnace", "writeToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", "func_189515_b");
                break;
                
                case 2:
                	if(!ConfigCore.asm_furnace)
                	{
                		print(name);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "GuiFurnace", "getBurnLeftScaled", "(I)I", "func_175382_i");
                break;
                
                case 3:
                	if(ConfigCore.asm_clientPlaceEvent)
                	{
                		ASMHelper.replaceMethod(classNode, inputBase + "ItemStack", "onItemUse", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/EnumFacing;FFF)Lnet/minecraft/util/EnumActionResult;", "func_179546_a");
                	}
                	if(ConfigCore.asm_TranslationEvent)
                	{
                		ASMHelper.replaceMethod(classNode, inputBase + "ItemStack", "getDisplayName", "()Ljava/lang/String;", "func_82833_r");
                	}
                	CapTransformer.transformItemStack(name, classNode, obfuscated);
                break;
                
                case 4:
                	if(!ConfigCore.asm_setTileNBTFix)
                	{
                		print(name);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "ItemBlock", "setTileEntityNBT", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Z", "func_179224_a");
                break;
                case 5:
                	ASMHelper.replaceMethod(classNode, inputBase + "SPacketUpdateTileEntity", "processPacket", "(Lnet/minecraft/network/play/INetHandlerPlayClient;)V", "func_148833_a");
                break;
                case 6:
                		if(obfuscated)
                		{
                			ASMHelper.replaceMethod(classNode, inputBase + "NetHandlerPlayServer", "processTryUseItemOnBlock", "(Lnet/minecraft/network/play/client/CPacketPlayerTryUseItemOnBlock;)V", "func_184337_a");
                		}
                		else
                			return ASMHelper.replaceClass(inputBase + "NetHandlerPlayServer");
                break;
                //custom capability system to the Entity.class
                case 7:
                	CapTransformer.transFormEntityCaps(name, classNode, obfuscated);
                break;
                case 8:
                	CapTransformer.transFormTileEntityCaps(name, classNode, obfuscated);
                break;
                case 9:
                	CapTransformer.transformWorld(name, classNode, inputBase, obfuscated);
                	CapTransformer.injectWorldTickers(name, classNode, inputBase, obfuscated);
                	GeneralTransformer.patchWorld(classNode);
                break;
                case 10:
                	CapTransformer.transformWorldInfo(classNode, name, obfuscated);
                break;
                case 11:
                	CapTransformer.transformChunk(classNode, name, obfuscated);
                break;
                case 12:
                	CapTransformer.transformAnvilChunkLoader(classNode, name, obfuscated);
                break;
                case 13:
                	GeneralTransformer.transformMC(classNode);
                	GeneralTransformer.patchFullScreen(classNode);
                break;
                case 14:
                	GeneralTransformer.patchOpenToLan(classNode);
                break;
                case 15:
                	GeneralTransformer.patchWeightedSpawner(classNode, inputBase, name);
                break;
                case 16:
                	if(!ConfigCore.asm_enchantmentNameFix)
                	{
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "Enchantment", "getTranslatedName", "(I)Ljava/lang/String;", "func_77316_c");
                break;
                
                case 17:
                	GeneralTransformer.patchWorldClient(classNode);
                break;
                
                case 18:
                	GeneralTransformer.patchRenderManager(classNode);
                break;
                
                case 19:
                	GeneralTransformer.transformChat(classNode);
                break;
                
                case 20:
                	GeneralTransformer.transformChatOverlay(classNode);
                break;
                
                case 21:
                	GeneralTransformer.transformSkinTrans(classNode);
                break;
                
                case 22:
                	GeneralTransformer.patchUUID(classNode);
                break;
                
                case 23:
                	GeneralTransformer.patchLoginNBT(classNode);
                break;
                
                case 24:
                	GeneralTransformer.patchUnloadDim(classNode);
                break;
                
                case 25:
                	ASMHelper.pubMinusFinal(classNode, true);
                break;
            }
            
            ASMHelper.clearCacheNodes();
       
            ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
            byte[] bytes = classWriter.toByteArray();
            if(ConfigCore.dumpASM)
            {
            	ASMHelper.dumpFile(name, bytes);
            }
            return bytes;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return classToTransform;
    }
	
	/**
	 * after config disables transformation and can't continue based on all asm being disabled for said class use this method.
	 * If you can disable asm via config and still transforms the class for other things not configurable don't call this method
	 */
	private static void print(String name) 
	{
		System.out.println("Config Disabled Transformation of class:" + name);
	}

	public static void batchLoad() 
	{
		allowMojangASM();
		
		if(!ConfigCore.asm_batchLoad)
			return;
		
		List<String> cls = new ArrayList(Transformer.clazzes);
		cls.addAll(EntityTransformer.clazzes);
		cls.removeAll(done);
		
		//Remove Client-Only Classes on Dedicated Server Side
		if(!MainJava.proxy.isClient())
		{
			Iterator<String> i = cls.iterator();
			while(i.hasNext())
			{
				String c = i.next();
				if(c.startsWith("net.minecraft.client.") || c.endsWith("GuiIngameForge") || c.startsWith("noppes.") || c.startsWith("goblinbob."))
					i.remove();
			}
		}
		else
		{
			if(!Loader.isModLoaded("moreplayermodels"))
				cls.remove("noppes.mpm.client.RenderEvent");
			if(!Loader.isModLoaded("mobends"))
				cls.remove("goblinbob.mobends.standard.client.renderer.entity.layers.LayerCustomCape");
		}
		
		ASMHelper.batchLoad("", cls);
		JavaUtil.toBase64("");//Force Load Base64 Class to prevent 30-90MS Hang
	}
	
    public static void init()
    {
    	i = true;
    	allowMojangASM();
    }
	
	/**
	 * Allow Mojang ASM
	 */
	public static void allowMojangASM()
	{
		Collection<String> transformerExceptions = (Collection<String>) ReflectionUtil.getObject(Launch.classLoader, LaunchClassLoader.class, "transformerExceptions");
		Collection<String> classLoaderExceptions = (Collection<String>) ReflectionUtil.getObject(Launch.classLoader, LaunchClassLoader.class, "classLoaderExceptions");
		
		if(transformerExceptions != null)
		{
			Iterator<String> it1 = transformerExceptions.iterator();
			while(it1.hasNext())
			{
				String c = it1.next();
				if(c.startsWith("com.mojang."))
					it1.remove();
			}
		}
		
		if(classLoaderExceptions != null)
		{
			Iterator<String> it2 = classLoaderExceptions.iterator();
			while(it2.hasNext())
			{
				String c = it2.next();
				if(c.startsWith("com.mojang.") && !c.equals("com.mojang.util.QueueLogAppender"))
					it2.remove();
			}
		}
	}

}
