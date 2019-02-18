package com.evilnotch.lib.asm.transformer;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.asm.util.ComputeClassWriter;
import com.evilnotch.lib.asm.util.ObfRemappingClassWriter;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer
{
    public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
    {
    	"net.minecraft.server.management.PlayerList",
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
    	"net.minecraft.enchantment.Enchantment",
    	"net.minecraft.server.integrated.IntegratedServer",
    	"net.minecraft.util.WeightedSpawnerEntity"
    });
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform)
    {	
        int index = clazzes.indexOf(transformedName);
        return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
    }

	public static byte[] transform(int index, byte[] classToTransform,boolean obfuscated)
    {
    	String name = clazzes.get(index);
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
                	if(!ConfigCore.asm_playerlist)
                	{
                		print(name);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "PlayerList", "getPlayerNBT", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "getPlayerNBT");
                	ASMHelper.replaceMethod(classNode, inputBase + "PlayerList", "readPlayerDataFromFile", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "func_72380_a");
                	GeneralTransformer.injectUUIDPatcher(classNode, obfuscated);
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
                			ASMHelper.replaceMethod(classNode, inputBase + "NetHandlerPlayServer", "processTryUseItemOnBlock", "(Lnet/minecraft/network/play/client/CPacketPlayerTryUseItemOnBlock;)V", "func_184337_a");
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
                	if(!ConfigCore.asm_middleClickEvent)
                	{
                		print(name);
                		return classToTransform;
                	}
                	GeneralTransformer.transformMC(classNode);
                break;
                case 14:
                	if(!ConfigCore.asm_enchantments)
                	{
                		print(name);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "Enchantment", "getTranslatedName", "(I)Ljava/lang/String;", "func_77316_c");
                break;
                case 15:
                	GeneralTransformer.patchOpenToLan(classNode);
                break;
                case 16:
                	GeneralTransformer.patchWeightedSpawner(classNode, name);
                break;
            }
            
            ASMHelper.clearCacheNodes();
       
            ClassWriter classWriter = new ComputeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
            byte[] bytes = classWriter.toByteArray();
            if(ConfigCore.dumpASM)
            {
            	ASMHelper.dumpFile(name, bytes);
            }
            return bytes;
        }
        catch (Exception e)
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

}
