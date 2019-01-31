package com.evilnotch.lib.asm;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer
{
    public static final List<String> classesBeingTransformed = (List<String>)JavaUtil.<String>asArray(new Object[]
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
    	"net.minecraft.world.chunk.storage.AnvilChunkLoader",//caps for chunks need readFromNBT() and writeToNBT()
    	"net.minecraft.client.Minecraft"
    });
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform)
    {
        int index = classesBeingTransformed.indexOf(transformedName);
        return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
    }
    
    public static byte[] transform(int index, byte[] classToTransform,boolean obfuscated)
    {
    	String name = classesBeingTransformed.get(index);
    	System.out.println("Transforming: " + name);
    	
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
                		System.out.println("returning default class:" + name + " ob:" + obfuscated + " cfg:" + ConfigCore.asm_playerlist);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "PlayerList", "getPlayerNBT", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "getPlayerNBT");
                	ASMHelper.replaceMethod(classNode, inputBase + "PlayerList", "readPlayerDataFromFile", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "func_72380_a");
                	GeneralTransformer.injectUUIDPatcher(classNode, obfuscated);
                break;
                
                case 1:
                	if(!ConfigCore.asm_furnace)
                	{
                		System.out.println("returning default class:" + name);
                		return classToTransform;
                	}
                	ASMHelper.replaceMethod(classNode, inputBase + "TileEntityFurnace", "readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", "func_145839_a");
                	ASMHelper.replaceMethod(classNode, inputBase + "TileEntityFurnace", "writeToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", "func_189515_b");
                break;
                
                case 2:
                	if(!ConfigCore.asm_furnace)
                		return classToTransform;
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
                		return classToTransform;
                	ASMHelper.replaceMethod(classNode, inputBase + "ItemBlock", "setTileEntityNBT", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Z", "func_179224_a");
                break;
                
                case 5:
                	return ASMHelper.replaceClass(inputBase + "SPacketUpdateTileEntity");
                case 6:
                	if(FMLCorePlugin.isObf)
                		ASMHelper.replaceMethod(classNode, inputBase + "NetHandlerPlayServer", "processTryUseItemOnBlock", "(Lnet/minecraft/network/play/client/CPacketPlayerTryUseItemOnBlock;)V", "func_184337_a");
                	else
                		ASMHelper.replaceClass(inputBase + "NetHandlerPlayServer");//to fix all them no such field errors in mdk
                break;
                //custom capability system to the Entity.class
                case 7:
                	CapTransformer.transFormEntityCaps(name,classNode,obfuscated);
                break;
                case 8:
                	CapTransformer.transFormTileEntityCaps(name, classNode, obfuscated);
                break;
                case 9:
                	CapTransformer.transformWorld(name, classNode,inputBase, obfuscated);
                	CapTransformer.injectWorldTickers(name, classNode,inputBase, obfuscated);
                break;
                case 10:
                	CapTransformer.transformWorldInfo(classNode, name, obfuscated);
                break;
                case 11:
                	CapTransformer.transformChunk(classNode, name, obfuscated);
                break;
                case 12:
                	CapTransformer.transformAnvilChunkLoader(classNode,name,obfuscated);
                break;
                case 13:
                	if(!ConfigCore.asm_middleClickEvent)
                		return classToTransform;
                		GeneralTransformer.transformMC(classNode);
                break;
            }
            
            ASMHelper.clearCacheNodes();

            ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
          if(index == ConfigCore.cfgIndex || ConfigCore.cfgIndex == -2)
          {
        	  String[] a = name.split("\\.");
        	  File f = new File(System.getProperty("user.home") + "/Desktop/" + a[a.length-1] + ".class");
        	  FileUtils.writeByteArrayToFile(f, classWriter.toByteArray());
          }
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return classToTransform;
    }

	public static String getFieldNodeString(FieldNode node) {
		return node.name + " desc:" + node.desc + " signature:" + node.signature + " access:" + node.access;
 	}

}
