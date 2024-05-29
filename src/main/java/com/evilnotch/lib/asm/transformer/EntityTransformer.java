package com.evilnotch.lib.asm.transformer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.classwriter.MCWriter;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.minecraft.util.UUIDPatcher;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class EntityTransformer implements IClassTransformer{
	
    public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
    {
    	"net.minecraft.entity.EntityHanging",
    	"net.minecraft.entity.item.EntityFallingBlock",
    	"net.minecraft.entity.item.EntityPainting",
    	"net.minecraft.entity.player.EntityPlayerMP",
    	"net.minecraft.entity.monster.EntityZombie",
    	"net.minecraft.entity.monster.EntityShulker",
    	"net.minecraft.client.entity.EntityPlayerSP",
    	"com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService",
    	"com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService$1",
    	"net.minecraft.client.network.NetworkPlayerInfo",//stopSteve
    	"net.minecraft.client.network.NetworkPlayerInfo$1",
    	"net.minecraft.network.login.client.CPacketLoginStart",//sends skin to server
    	"net.minecraft.client.resources.SkinManager",//redirect $steve and $alex to be localized resource location instead of always steve
    	"net.minecraft.client.resources.SkinManager$3$1",//stopSteve add callback of skin failure
    	"net.minecraft.client.renderer.ThreadDownloadImageData",
    	"net.minecraft.client.renderer.ThreadDownloadImageData$1",//stopSteve add callback of skin failure
    	"net.minecraft.client.resources.SkinManager$3" //transform all fields into public minus final
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] classToTransform) 
	{
		 int index = clazzes.indexOf(transformedName);
	     return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
	}

	private byte[] transform(int index, byte[] classToTransform, boolean isObf) 
	{
		String name = clazzes.get(index);
		
		if(!ConfigCore.asm_entityPatch && !name.equals("net.minecraft.entity.player.EntityPlayerMP"))
		{
			return classToTransform;
		}

    	System.out.println("Transforming: " + name + " index:" + index);
    	
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classToTransform);
            classReader.accept(classNode, 0);
            
            switch(index)
            {
                case 0:
                	transformEntityHanging(classNode);
                break;
                
                case 1:
                	transformEntityFalling(classNode);
                break;
                
                case 2:
                	transformEntityPainting(classNode);
                break;
                
                case 3:
                	patchPlayer(classNode);
                break;
                
                case 4:
                	patchZombie(classNode);
                break;
                
                case 5:
                	patchShulker(classNode);
                break;
                
                case 6:
                	transformPlayerClient(classNode);
                break;
                
                case 7:
                	if(ConfigCore.asm_patchLanSkins)
                		patchLanSkins(classNode);
                break;
                
                case 8:
                	if(ConfigCore.asm_patchLanSkins)
                		patchLanSkinsCache(classNode);
                break;
                
                case 9:
                	patchStopSteve(classNode);
                break;
                
                case 10:
                	patchStopSteve1(classNode);
                break;
                
                case 11:
                	patchCPacketLoginStart(classNode);
                break;
                
                case 12:
                	transformSkinManager(classNode);
                break;
                
                case 13:
                	patchSkinManager3M1(classNode);
                break;
                
                case 14:
                	transformSkinDL(classNode);
                break;
                
                case 15:
                	patchSkinDL(classNode);
                break;
                
                case 16:
                	pubMinusFinal(classNode);
                break;
            }
            
            ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
            byte[] bytes = classWriter.toByteArray();
            if(ConfigCore.dumpASM)
            {
            	ASMHelper.dumpFile(name, bytes);
            }
            return bytes;
        }
        catch(Throwable t)
        {
        	t.printStackTrace();
        }
		return classToTransform;
	}

	
	public void pubMinusFinal(ClassNode classNode)
	{
		for(FieldNode f : classNode.fields)
		{
		    // Get the current access flags
		    int access = f.access;
		    
		    // Remove conflicting access modifiers
		    access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
		    
		    // Remove the final modifier
		    access &= ~Opcodes.ACC_FINAL;
		    
		    // Set the public modifier
		    access |= Opcodes.ACC_PUBLIC;
		    
		    // Update the field's access flags
		    f.access = access;
		}
	}

	public void transformSkinDL(ClassNode classNode) 
	{
		ASMHelper.addFeild(classNode, "skinCallBack", "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;");
		ASMHelper.addFeild(classNode, "skinType", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;");
		ASMHelper.addFeild(classNode, "skinLoc", "Lnet/minecraft/util/ResourceLocation;");
		ASMHelper.addFeild(classNode, "skinTexture", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;");
	}

	public void patchSkinDL(ClassNode classNode) 
	{
		MethodNode run = ASMHelper.getMethodNode(classNode, "run", "()V");
		String ref = new MCPSidedString("this$0", "field_110932_a").toString();
		
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/MainJava", "proxy", "Lcom/evilnotch/lib/minecraft/proxy/ServerProxy;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "getResponseCode", "()I", false));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinCallBack", "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinType", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinLoc", "Lnet/minecraft/util/ResourceLocation;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinTexture", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/proxy/ServerProxy", "noSkin", "(ILjava/lang/Object;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Ljava/lang/Object;)V", false));

		run.instructions.insert(ASMHelper.getFirstMethodInsn(run, Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "connect", "()V", false), list);
		
		//inject noSkin call into the exception
		InsnList li = new InsnList();
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/MainJava", "proxy", "Lcom/evilnotch/lib/minecraft/proxy/ServerProxy;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinCallBack", "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinType", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinLoc", "Lnet/minecraft/util/ResourceLocation;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData$1", ref, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;"));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinTexture", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;"));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/proxy/ServerProxy", "noSkin", "(Ljava/lang/Object;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Ljava/lang/Object;)V", false));
	
		run.instructions.insert(ASMHelper.getMethodInsnNode(run, Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true), li);
		
	}

	public void patchSkinManager3M1(ClassNode classNode)
	{
		MethodNode m = ASMHelper.getMethodNode(classNode, "run", "()V");
		//MainJava.proxy.noSkin(map, callback);
		InsnList l = new InsnList();
		l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/MainJava", "proxy", "Lcom/evilnotch/lib/minecraft/proxy/ServerProxy;"));
		l.add(new VarInsnNode(Opcodes.ALOAD, 0));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", new MCPSidedString("val$map", "field_152803_a").toString(), "Ljava/util/Map;"));
		l.add(new VarInsnNode(Opcodes.ALOAD, 0));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", new MCPSidedString("this$1", "field_152804_b").toString(), "Lnet/minecraft/client/resources/SkinManager$3;"));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3", new MCPSidedString("val$skinAvailableCallback", "field_152801_c").toString(), "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));

		l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/proxy/ServerProxy", "noSkin", "(Ljava/util/Map;Ljava/lang/Object;)V", false));
		m.instructions.insert(ASMHelper.getFirstInstruction(m), l);
	}

	public void patchStopSteve1(ClassNode classNode) throws IOException
	{
		//add IMPL of stopSteve
		String inputBase = "assets/evilnotchlib/asm/" + (FMLCorePlugin.isObf ? "srg/" : "deob/");
		ASMHelper.addIfMethod(classNode, inputBase + "NetworkPlayerInfo$1", "skinUnAvailable", "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)V");
		//patch method for compiled as this$0 doesn't exist
		if(FMLCorePlugin.isObf)
		{
			MethodNode topatch = ASMHelper.getMethodNode(classNode, "skinUnAvailable", "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)V");
			Iterator<AbstractInsnNode> it = topatch.instructions.iterator();
			while(it.hasNext())
			{
				AbstractInsnNode ab = it.next();
				if(ab instanceof FieldInsnNode)
				{
					FieldInsnNode f = (FieldInsnNode) ab;
					if(f.name.equals("this$0"))
					{
						f.name = "field_177224_a";
					}
				}
			}
		}
		//hack JVM supports multiple inner class interfaces but never made it for the compiler side
		ASMHelper.addInterface(classNode, "com/evilnotch/lib/main/skin/IStopSteve");
		
		//NetWorkPlayerInfo.this.stopedSteve = true;
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("skinAvailable", "func_180521_a").toString(), "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)V");
		InsnList l = new InsnList();
		l.add(new VarInsnNode(Opcodes.ALOAD, 0));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/network/NetworkPlayerInfo$1", new MCPSidedString("this$0", "field_177224_a").toString(), "Lnet/minecraft/client/network/NetworkPlayerInfo;"));
		l.add(new InsnNode(Opcodes.ICONST_1));
		l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/network/NetworkPlayerInfo", "stopedSteve", "Z"));
		m.instructions.insert(ASMHelper.getLastLabelNode(m, false), l);
	}

	/**
	 * redirect steve and alex to local resource locations
	 */
	public void transformSkinManager(ClassNode classNode)
	{
		//UUIDPatcher#patchSkinResource
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("loadSkin", "func_152789_a").toString(), "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;");
		m.instructions.insertBefore(ASMHelper.getVarInsnNode(m, new VarInsnNode(Opcodes.ASTORE, 4)), new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patchSkinResource", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;", false));
		
		InsnList li = new InsnList();
//	  	threaddownloadimagedata.skinCallBack = skinAvailableCallback;
		li.add(new VarInsnNode(Opcodes.ALOAD, 9));
		li.add(new VarInsnNode(Opcodes.ALOAD, 3));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinCallBack", "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));

		//	  	threaddownloadimagedata.skinType = textureType;
		li.add(new VarInsnNode(Opcodes.ALOAD, 9));
		li.add(new VarInsnNode(Opcodes.ALOAD, 2));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinType", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;"));
	
//	  	threaddownloadimagedata.skinLoc = resourcelocation;
		li.add(new VarInsnNode(Opcodes.ALOAD, 9));
		li.add(new VarInsnNode(Opcodes.ALOAD, 4));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinLoc", "Lnet/minecraft/util/ResourceLocation;"));
	
//	  	threaddownloadimagedata.skinTexture = profileTexture;
		li.add(new VarInsnNode(Opcodes.ALOAD, 9));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinTexture", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;"));
		
		m.instructions.insert(ASMHelper.getVarInsnNode(m, new VarInsnNode(Opcodes.ASTORE, 9)), li);
	}

	public void patchCPacketLoginStart(ClassNode classNode)
	{
		//public skindata
		classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "skindata", "Ljava/lang/String;", null, null));
		
		//this.skindata = Config.skinCache ? SkinCache.getEncode(SkinCache.INSTANCE.selected) : "";
		MethodNode ctr = ASMHelper.getConstructionNode(classNode, "(Lcom/mojang/authlib/GameProfile;)V");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/Config", "skinCache", "Z"));
		LabelNode l3 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFEQ, l3));
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/skin/SkinCache", "INSTANCE", "Lcom/evilnotch/lib/main/skin/SkinCache;"));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "com/evilnotch/lib/main/skin/SkinCache", "selected", "Lcom/evilnotch/lib/main/skin/SkinEntry;"));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinCache", "getEncode", "(Lcom/evilnotch/lib/main/skin/SkinEntry;)Ljava/lang/String;", false));
		LabelNode l4 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.GOTO, l4));
		li.add(l3);
		li.add(new LdcInsnNode(""));
		li.add(l4);
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		ctr.instructions.insert(ASMHelper.getFirstInstruction(ctr, Opcodes.PUTFIELD), li);
		
		//this.skindata = buf.readString(Short.MAX_VALUE);
		MethodNode read = ASMHelper.getMethodNode(classNode, new MCPSidedString("readPacketData", "func_148837_a").toString(), "(Lnet/minecraft/network/PacketBuffer;)V");
		InsnList rlist = new InsnList();
		rlist.add(new VarInsnNode(Opcodes.ALOAD, 0));
		rlist.add(new VarInsnNode(Opcodes.ALOAD, 1));
		rlist.add(new IntInsnNode(Opcodes.SIPUSH, 32767));
		rlist.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", new MCPSidedString("readString", "func_150789_c").toString(), "(I)Ljava/lang/String;", false));
		rlist.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		read.instructions.insert(ASMHelper.getFieldNode(read, Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", new MCPSidedString("profile", "field_149305_a").toString(), "Lcom/mojang/authlib/GameProfile;"), rlist);
	
		//buf.writeString(this.skindata);
		MethodNode write = ASMHelper.getMethodNode(classNode, new MCPSidedString("writePacketData", "func_148840_b").toString(), "(Lnet/minecraft/network/PacketBuffer;)V");
		InsnList wlist = new InsnList();
		String writestring = new MCPSidedString("writeString", "func_180714_a").toString();
		wlist.add(new VarInsnNode(Opcodes.ALOAD, 1));
		wlist.add(new VarInsnNode(Opcodes.ALOAD, 0));
		wlist.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		wlist.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", writestring, "(Ljava/lang/String;)Lnet/minecraft/network/PacketBuffer;", false));
		wlist.add(new InsnNode(Opcodes.POP));
		AbstractInsnNode spot = ASMHelper.getMethodInsnNode(write, Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", writestring, "(Ljava/lang/String;)Lnet/minecraft/network/PacketBuffer;", false).getNext();
		write.instructions.insert(spot, wlist);
	}

	public void patchStopSteve(ClassNode classNode) 
	{
		if(!ASMHelper.containsFieldNode(classNode, "stopedSteve"))
		{
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "stopedSteve", "Z", null, null));
		}
	}

	private void patchLanSkinsCache(ClassNode classNode) 
	{
		MethodNode m = ASMHelper.getMethodNode(classNode, "load", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;");
		AbstractInsnNode ab = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "fillGameProfile", "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;", false);
		AbstractInsnNode is = ab.getPrevious();
		
		//changes fillGameProfile(gameProfile, false) to fillGameProfile(gameProfile, true) 
		if(is.getOpcode() == Opcodes.ICONST_0)
		{
			InsnNode spot = (InsnNode) is;
			m.instructions.insert(spot, new InsnNode(Opcodes.ICONST_1));
			m.instructions.remove(spot);
		}
	}

	public static void patchLanSkins(ClassNode classNode)
	{
		MethodNode m = ASMHelper.getMethodNode(classNode, "getTextures", "(Lcom/mojang/authlib/GameProfile;Z)Ljava/util/Map;");
		JumpInsnNode jump = (JumpInsnNode) ASMHelper.getFirstInstruction(m, Opcodes.ILOAD).getNext();
		LabelNode label = jump.label;
		
		//append && JavaUtil.returnFalse() so that lan skins works again
		InsnList li = new InsnList();
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/util/JavaUtil", "returnFalse", "()Z", false));
		li.add(new JumpInsnNode(Opcodes.IFEQ, label));
		m.instructions.insert(jump, li);
		
		//if(url == null || url.trim().isEmpty) return false;
		MethodNode wlist = ASMHelper.getMethodNode(classNode, "isWhitelistedDomain", "(Ljava/lang/String;)Z");
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		LabelNode l1 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFNULL, l1));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;", false));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "isEmpty", "()Z", false));
		LabelNode l2 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, l2));
		list.add(l1);
		list.add(new InsnNode(Opcodes.ICONST_0));
		list.add(new InsnNode(Opcodes.IRETURN));
		list.add(l2);
		wlist.instructions.insert(ASMHelper.getFirstInstruction(wlist), list);
		
	}

	public static void transformPlayerClient(ClassNode classNode)
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("sendChatMessage", "func_71165_d").toString(), "(Ljava/lang/String;)V");
		InsnList li = new InsnList();
		
		/*
		 *  message = MessageEvent.Send(message);
    		if(message == null)
    			return
		 */
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/event/client/MessageEvent", "Send", "(Ljava/lang/String;)Ljava/lang/String;", false));
		li.add(new VarInsnNode(Opcodes.ASTORE, 1));
		li.add(new LabelNode());
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		LabelNode l2 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFNONNULL, l2));
		LabelNode l3 = new LabelNode();
		li.add(l3);
		li.add(new InsnNode(Opcodes.RETURN));
		li.add(l2);
		
		node.instructions.insert(ASMHelper.getFirstInstruction(node), li);
	}

	public static void patchShulker(ClassNode classNode) 
	{
		//insert EntityUtil#patchShulker into the constructor
		MethodNode node = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "patchShulker", "(Lnet/minecraft/entity/monster/EntityShulker;)V", false));
		node.instructions.insert(ASMHelper.getLastPutField(node), list);
		
		//append && EntityUtil.addedToWorld(this) to see if it can teleport or not
		MethodNode pos = ASMHelper.getMethodNode(classNode, new MCPSidedString("setPosition", "func_70107_b").toString(), "(DDD)V");
		JumpInsnNode spot = null;
		FieldInsnNode check = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/monster/EntityShulker", new MCPSidedString("ticksExisted","field_70173_aa").toString(), "I");
		for(AbstractInsnNode ab : pos.instructions.toArray())
		{
			if(ab.getOpcode() == Opcodes.GETFIELD && ab.getNext() instanceof JumpInsnNode)
			{
				if(ASMHelper.equals(check, (FieldInsnNode)ab))
				{
					spot = (JumpInsnNode) ab.getNext();
				}
			}
		}
		
		InsnList list2 = new InsnList();
		list2.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "addedToWorld", "(Lnet/minecraft/entity/Entity;)Z", false));
		list2.add(new JumpInsnNode(Opcodes.IFEQ, spot.label));
		pos.instructions.insert(spot, list2);
	}

	public static void patchZombie(ClassNode classNode)
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("readEntityFromNBT", "func_70037_a").toString(), "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		
		//stop broken method from firing to begin with
		AbstractInsnNode spot = null;
		LabelNode l2 = null;
		for(AbstractInsnNode ab : node.instructions.toArray())
		{
			if(ab instanceof LdcInsnNode)
			{	
				if("IsBaby".equals(((LdcInsnNode)ab).cst))
				{
					spot = ab.getPrevious();//ALOAD INSTRUCTION FOUND
					AbstractInsnNode compare = ab;
					while(compare != null)
					{
						compare = compare.getNext();
						if(compare instanceof JumpInsnNode)
						{
							l2 = ((JumpInsnNode)compare).label;
							break;
						}
					}
					break;
				}
			}
		}
		InsnList list = new InsnList();
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/util/JavaUtil",  "returnFalse", "()Z", false));
		list.add(new JumpInsnNode(Opcodes.IFEQ, l2));
		node.instructions.insertBefore(spot, list);
		
		//inject this.setChild(nbt.getBoolean("isBaby")); to the end of the method
		AbstractInsnNode spot2 = ASMHelper.getLastInstruction(node, Opcodes.RETURN);
		InsnList list2 = new InsnList();
		list2.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list2.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list2.add(new LdcInsnNode("IsBaby"));
		list2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", new MCPSidedString("getBoolean", "func_74767_n").toString(), "(Ljava/lang/String;)Z", false));
		list2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/monster/EntityZombie", new MCPSidedString("setChild", "func_82227_f").toString(), "(Z)V", false));
		node.instructions.insertBefore(spot2, list2);
	}

	public static void transformEntityHanging(ClassNode classNode) 
	{
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		AbstractInsnNode point = ASMHelper.getLastMethodInsn(construct, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityHanging", new MCPSidedString("setSize", "func_70105_a").toString(), "(FF)V", false);
		
		InsnList list = new InsnList();
		//this.facingDirection = EnumFacing.NORTH;
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/EnumFacing", "NORTH", "Lnet/minecraft/util/EnumFacing;"));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/EntityHanging", new MCPSidedString("facingDirection", "field_174860_b").toString(), "Lnet/minecraft/util/EnumFacing;"));
		
		//this.hangingPosition = this.getPosition().offset(this.facingDirection);
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityHanging", new MCPSidedString("getPosition", "func_180425_c").toString(), "()Lnet/minecraft/util/math/BlockPos;", false));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/EntityHanging", new MCPSidedString("facingDirection", "field_174860_b").toString(), "Lnet/minecraft/util/EnumFacing;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", new MCPSidedString("offset", "func_177972_a").toString(), "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;", false));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/EntityHanging", new MCPSidedString("hangingPosition", "field_174861_a").toString(), "Lnet/minecraft/util/math/BlockPos;"));
		
		construct.instructions.insert(point, list);
	}

	public static void transformEntityPainting(ClassNode classNode) 
	{
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/entity/item/EntityPainting$EnumArt", "KEBAB", "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityPainting", new MCPSidedString("art", "field_70522_e").toString(), "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
		
		construct.instructions.insertBefore(ASMHelper.getLastInstruction(construct, Opcodes.RETURN), list);
	}
	
	public static void transformEntityFalling(ClassNode classNode) 
	{
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETSTATIC,"net/minecraft/init/Blocks", new MCPSidedString("STONE", "field_150348_b").toString(), "Lnet/minecraft/block/Block;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block", new MCPSidedString("getDefaultState", "func_176223_P").toString(), "()Lnet/minecraft/block/state/IBlockState;", false));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityFallingBlock", new MCPSidedString("fallTile", "field_175132_d").toString(), "Lnet/minecraft/block/state/IBlockState;"));
		
		construct.instructions.insertBefore(ASMHelper.getLastInstruction(construct, Opcodes.RETURN), list);
	}
	
	/**
	 * patch seed check
	 */
	public static void patchPlayer(ClassNode classNode)
    {    
      	MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("canUseCommand","func_70003_b").toString(), "(ILjava/lang/String;)Z");
      	
      	//makes the seed check never happen
      	LdcInsnNode strseed = ASMHelper.getLdcInsnNode(node, new LdcInsnNode("seed"));
      	if(strseed != null)
      		strseed.cst = "_mc.1.12.2";
      	
      	//add if(permLevel == 0 || commandName.equals("seed") && PlayerUtil.isPlayerOwner(this)) return true;
      	InsnList li = new InsnList();
      	LabelNode l0 = new LabelNode();
      	li.add(l0);
      	li.add(new VarInsnNode(Opcodes.ILOAD, 1));
      	LabelNode l1 = new LabelNode();
      	li.add(new JumpInsnNode(Opcodes.IFEQ, l1));
      	li.add(new VarInsnNode(Opcodes.ALOAD, 2));
      	li.add(new LdcInsnNode("seed"));
      	li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
      	LabelNode l2 = new LabelNode();
      	li.add(new JumpInsnNode(Opcodes.IFEQ, l2));
      	li.add(new VarInsnNode(Opcodes.ALOAD, 0));
      	li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/PlayerUtil", "isPlayerOwner", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Z", false));
      	li.add(new JumpInsnNode(Opcodes.IFEQ, l2));
      	li.add(l1);
      	li.add(new InsnNode(Opcodes.ICONST_1));
      	li.add(new InsnNode(Opcodes.IRETURN));
      	li.add(l2);
      	
      	node.instructions.insert(ASMHelper.getFirstInstruction(node), li);
      	
      	MethodNode m = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/WorldServer;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/server/management/PlayerInteractionManager;)V");
      	//UUIDPatcher.patch(profile)
      	InsnList l = new InsnList();
      	l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patchCheck", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;", false));
      	m.instructions.insertBefore(ASMHelper.getFirstMethodInsn(m, Opcodes.INVOKESPECIAL, "net/minecraft/entity/player/EntityPlayer", "<init>", "(Lnet/minecraft/world/World;Lcom/mojang/authlib/GameProfile;)V", false), l);
      	
      	
      	//profile = this.getGameProfile();
      	InsnList list = new InsnList();
      	list.add(new VarInsnNode(Opcodes.ALOAD, 0));
      	list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayerMP", new MCPSidedString("getGameProfile", "func_146103_bH").toString(), "()Lcom/mojang/authlib/GameProfile;", false));
      	list.add(new VarInsnNode(Opcodes.ASTORE, 3));
      	AbstractInsnNode spot = ASMHelper.PreviousLabel(ASMHelper.getFieldNode(m, Opcodes.PUTFIELD, "net/minecraft/server/management/PlayerInteractionManager", new MCPSidedString("player", "field_73090_b").toString(), "Lnet/minecraft/entity/player/EntityPlayerMP;"));
      	m.instructions.insert(spot, list);
      	
    }

}
