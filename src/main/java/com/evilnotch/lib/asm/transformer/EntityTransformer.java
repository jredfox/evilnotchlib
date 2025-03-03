package com.evilnotch.lib.asm.transformer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
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
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.classwriter.MCWriter;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.minecraft.client.CapeRenderer;
import com.evilnotch.lib.util.JavaUtil;

import goblinbob.mobends.standard.client.renderer.entity.BendsCapeRenderer;
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
    	"net.minecraft.client.resources.SkinManager$3", //transform all fields into public minus final
    	"net.minecraft.client.resources.SkinManager$3$1",//stopSteve add callback of skin failure
    	"net.minecraft.client.renderer.ThreadDownloadImageData",
    	"net.minecraft.client.renderer.ThreadDownloadImageData$1",//stopSteve add callback of skin failure
    	"net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head", //SkinEvent#Mouse
    	"net.minecraft.client.renderer.entity.RenderLivingBase", //SkinEvent#Dinnerbone
    	"net.minecraftforge.common.util.FakePlayer",//UUIDPatcher V2 FakePlayer Detection
    	"net.minecraft.scoreboard.ScorePlayerTeam",//Fix Team Colors for nicknames on LanEssentials
    	"net.minecraftforge.client.GuiIngameForge",//Override when GuiTabOverlay can be rendered
    	"net.minecraft.client.gui.GuiPlayerTabOverlay",//SkinEvent#fireDinnerbone(player, info);
    	"com.mojang.authlib.minecraft.MinecraftProfileTexture",//SkinEvent#HashURLEvent
    	"net.minecraft.client.renderer.entity.layers.LayerCape",//SkinEvent#CapeEnchant
    	"noppes.mpm.client.RenderEvent",//Fix DERPs of MorePlayerModels Mod making IStopSteve not work and getting the wrong skin info at times
    	"goblinbob.mobends.standard.client.renderer.entity.layers.LayerCustomCape"//Mo' Bends Support! Make Enchanted Capes Work
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] classToTransform) 
	{
        int index = clazzes.indexOf(transformedName);
        return (index == -1 || classToTransform == null) ? classToTransform : transform(index, classToTransform, FMLCorePlugin.isObf);
	}

	private byte[] transform(int index, byte[] classToTransform, boolean isObf) 
	{
		String name = clazzes.get(index);
		Transformer.done.add(name);
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
                	patchLanSkins(classNode);
                break;
                
                case 8:
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
                	patchSkinManager3(classNode);
                break;
                
                case 14:
                	patchSkinManager3M1(classNode);
                break;
                
                case 15:
                	transformSkinDL(classNode);
                break;
                
                case 16:
                	patchSkinDL(classNode);
                break;
                
                case 17:
                	transformMouse(classNode);
                break;
                
                case 18:
                	transformDinnerbone(classNode);
                break;
                
                case 19:
                	patchFakePlayer(classNode);
                break;
                
                case 20:
                	patchScorePlayerTeam(classNode);
                break;
                
                case 21:
                	patchGuiIngameForge(classNode);
                break;
                
                case 22:
                	transformGuiPlayerTabOverlay(classNode);
                break;
                
                case 23:
                	transformMinecraftProfileTexture(classNode);
                break;
                
                case 24:
                	transformLayerCape(classNode);
                break;
                
                case 25:
                	fixMorePlayerModels(classNode);
                break;
                
                case 26:
                	transformMoBends(classNode);
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
            if(ConfigCore.dumpASM)
            {
            	try 
            	{
					ASMHelper.dumpFile(name, classToTransform);
				} 
            	catch (IOException e) 
            	{
					e.printStackTrace();
				}
            }
        }
		return classToTransform;
	}

	public void transformSkinDL(ClassNode classNode) 
	{
		if(!ConfigCore.asm_stopSteve)
			return;
		
		ASMHelper.addFeild(classNode, "skinCallBack", "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;");
		ASMHelper.addFeild(classNode, "skinType", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;");
		ASMHelper.addFeild(classNode, "skinLoc", "Lnet/minecraft/util/ResourceLocation;");
		ASMHelper.addFeild(classNode, "skinTexture", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;");
	}

	public void patchSkinDL(ClassNode classNode) 
	{
		MethodNode run = ASMHelper.getMethodNode(classNode, "run", "()V");
		AbstractInsnNode connectSpot = ASMHelper.getFirstMethodInsn(run, Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection", "connect", "()V", false);
		
		//MainJava.proxy.dlHook(connection);
		if(ConfigCore.asm_skinAgentMozilla)
		{
			InsnList la = new InsnList();
			la.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/MainJava", "proxy", "Lcom/evilnotch/lib/minecraft/proxy/ServerProxy;"));
			la.add(new VarInsnNode(Opcodes.ALOAD, 1));
			la.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/proxy/ServerProxy", "dlHook", "(Ljava/net/HttpURLConnection;)V", false));
			la.add(new LabelNode());
			
			AbstractInsnNode spotAgent = ASMHelper.prevVarInsnNode(connectSpot, new VarInsnNode(Opcodes.ASTORE, 1));
			run.instructions.insert(spotAgent, la);
		}
		
		if(!ConfigCore.asm_stopSteve)
			return;
		
		String ref = ASMHelper.getThisParentField(classNode, "Lnet/minecraft/client/renderer/ThreadDownloadImageData;", "field_110932_a");
		
		InsnList list = new InsnList();
		//inject noskin call with url response after the connection is established
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

		run.instructions.insert(connectSpot, list);
		
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
	
		//find the logger.error line with inconsistencies of the message and even the overloaded call across forge versions
		AbstractInsnNode ab = run.instructions.getLast();
		while(ab != null)
		{
			if(ab instanceof LdcInsnNode)
			{
				LdcInsnNode ldc = (LdcInsnNode)ab;
				if(ldc.cst instanceof String && ldc.cst.toString().toLowerCase().startsWith("couldn't download"))
				{
					ab = ldc;
					break;
				}
			}
			ab = ab.getPrevious();
		}
		AbstractInsnNode spot = ASMHelper.previousLabel(ab);
		
		spot.hashCode();//trigger null pointer exception if not found
		run.instructions.insert(spot, li);
		
	}
	
	private static volatile String skinThisZero;
	private void patchSkinManager3(ClassNode classNode)
	{
    	ASMHelper.pubMinusFinal(classNode, true);
    	skinThisZero = ASMHelper.getThisParentField(classNode, "Lnet/minecraft/client/resources/SkinManager;", "field_152802_d");
	}

	public void patchSkinManager3M1(ClassNode classNode)
	{
		MethodNode m = ASMHelper.getMethodNode(classNode, "run", "()V");
		
		//Dynamically get this$1 and this$1.this$0 val$map and val$skinAvailableCallback as mods can change the internal field names
		MethodInsnNode targ = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/resources/SkinManager", new MCPSidedString("loadSkin", "func_152789_a").toString(), "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;", false);
		String thisone = null;
		String thiszero = null;
		String valmap = null;
		String valSkinCall = null;
		
		boolean thisoneSRG = false;
		boolean thiszeroSRG = false;
		boolean valmapSRG = false;
		boolean valSkinCallSRG = false;
		
		AbstractInsnNode ab = targ;
		while(ab != null)
		{
			ab = ab.getPrevious();
			if(ab instanceof FieldInsnNode && ab.getOpcode() == Opcodes.GETFIELD)
			{
				FieldInsnNode f = (FieldInsnNode) ab;
				if(!thisoneSRG && f.desc.equals("Lnet/minecraft/client/resources/SkinManager$3;"))
				{
					thisoneSRG = f.name.equals("field_152804_b");
					thisone = f.name;
				}
				else if(!thiszeroSRG && f.desc.equals("Lnet/minecraft/client/resources/SkinManager;"))
				{
					thiszeroSRG = f.name.equals("field_152802_d");
					thiszero = f.name;
				}
				else if(!valmapSRG && f.desc.equals("Ljava/util/Map;"))
				{
					valmapSRG = f.name.equals("field_152803_a");
					valmap = f.name;
				}
				else if(!valSkinCallSRG && f.desc.equals("Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"))
				{
					valSkinCallSRG = f.name.equals("field_152801_c");
					valSkinCall = f.name;
				}
			}
			else if(ab instanceof LabelNode)
				break;
		}
		if (thiszero == null) 
		{
			if (skinThisZero == null) 
			{
				try 
				{
					System.err.println("SkinManager$3$1 Unable to find this$1.this$0 Hotloading SkinManager$3 to know what this$0 is!");
					Class.forName("net.minecraft.client.resources.SkinManager$3", false, this.getClass().getClassLoader());
				} 
				catch (Throwable t) 
				{
					t.printStackTrace();
				}
			}
			thiszero = skinThisZero == null ? "this$0" : skinThisZero;
		}
		if(ConfigCore.dumpASM)
		{
			System.out.println("SkinManager$3$1:" + thisone + " " + thiszero + " " + valmap + " " + valSkinCall);
			System.out.println("SRG:" + thisoneSRG + " " + thiszeroSRG + " " + valmapSRG + " " + valSkinCallSRG);
		}
		
		//MainJava.proxy.skinElytra(SkinManager.this, map, skinAvailableCallback);
		InsnList list = new InsnList();
		list.add(new LabelNode());
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/MainJava", "proxy", "Lcom/evilnotch/lib/minecraft/proxy/ServerProxy;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", thisone, "Lnet/minecraft/client/resources/SkinManager$3;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3", thiszero, "Lnet/minecraft/client/resources/SkinManager;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", valmap, "Ljava/util/Map;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", thisone, "Lnet/minecraft/client/resources/SkinManager$3;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3", valSkinCall, "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/proxy/ServerProxy", "skinElytra", "(Ljava/lang/Object;Ljava/util/Map;Ljava/lang/Object;)V", false));
		m.instructions.insert(ASMHelper.getLastLabelNode(m, false), list);
		
		if(ConfigCore.asm_stopSteve)
		{	
			//MainJava.proxy.noSkin(map, callback);
			InsnList l = new InsnList();
			l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/MainJava", "proxy", "Lcom/evilnotch/lib/minecraft/proxy/ServerProxy;"));
			l.add(new VarInsnNode(Opcodes.ALOAD, 0));
			l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", valmap, "Ljava/util/Map;"));
			l.add(new VarInsnNode(Opcodes.ALOAD, 0));
			l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3$1", thisone, "Lnet/minecraft/client/resources/SkinManager$3;"));
			l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager$3", valSkinCall, "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));
			l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/proxy/ServerProxy", "noSkin", "(Ljava/util/Map;Ljava/lang/Object;)V", false));
			m.instructions.insert(ASMHelper.getFirstInstruction(m), l);
		}
	}

	public void patchStopSteve1(ClassNode classNode) throws IOException
	{
		if(!ConfigCore.asm_stopSteve)
			return;
		
		//add IMPL of stopSteve
		String inputBase = "assets/evilnotchlib/asm/" + (FMLCorePlugin.isObf ? "srg/" : "deob/");
		ASMHelper.addIfMethod(classNode, inputBase + "NetworkPlayerInfo$1", "skinUnAvailable", "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)V");
		String thiszero = ASMHelper.getThisParentField(classNode, "Lnet/minecraft/client/network/NetworkPlayerInfo;", "field_177224_a");
		//patch method for compiled as this$0 doesn't exist
		if(!thiszero.equals("this$0"))
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
						f.name = thiszero;
					}
				}
			}
		}
		
		//hack JVM supports multiple inner class interfaces but never made it for the compiler side
		ASMHelper.addInterface(classNode, "com/evilnotch/lib/main/skin/IStopSteve");
		
		//StopSteve.stopSteve(NetWorkPlayerInfo.this, typeIn);
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("skinAvailable", "func_180521_a").toString(), "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)V");
		InsnList l = new InsnList();
		l.add(new VarInsnNode(Opcodes.ALOAD, 0));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/network/NetworkPlayerInfo$1", thiszero, "Lnet/minecraft/client/network/NetworkPlayerInfo;"));
		l.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/eventhandler/StopSteve", "stopSteve", "(Lnet/minecraft/client/network/NetworkPlayerInfo;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;)V", false));
		m.instructions.insert(ASMHelper.getLastLabelNode(m, false), l);
		
	}

	/**
	 * redirect steve and alex to local resource locations
	 */
	public void transformSkinManager(ClassNode classNode)
	{
		//SkinCache#patchSkinResource
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("loadSkin", "func_152789_a").toString(), "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;");
		m.instructions.insertBefore(ASMHelper.getVarInsnNode(m, new VarInsnNode(Opcodes.ASTORE, 4)), new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinCache", "patchSkinResource", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;", false));
		
		//profileTexture.cacheHash();
		if(ConfigCore.asm_skinURLHook)
		{
			InsnList lh = new InsnList();
			lh.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lh.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "cacheHash", "()V", false));
			m.instructions.insert(ASMHelper.getFirstInstruction(m), lh);
		}
		
		if(!ConfigCore.asm_stopSteve)
			return;
		
		InsnList li = new InsnList();
//	  	threaddownloadimagedata.skinCallBack = skinAvailableCallback;
		li.add(new VarInsnNode(Opcodes.ALOAD, 9));
		li.add(new VarInsnNode(Opcodes.ALOAD, 3));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ThreadDownloadImageData", "skinCallBack", "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;"));

//		threaddownloadimagedata.skinType = textureType;
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
		//public String skindata;
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "skindata", "Ljava/lang/String;", null, null));
		//public NBTTagCompound evlNBT;
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;", null, null));
		
		//this.skindata = SkinCache.getEncodeLogin();
		MethodNode ctr = ASMHelper.getConstructionNode(classNode, "(Lcom/mojang/authlib/GameProfile;)V");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinCache", "getEncodeLogin", "()Ljava/lang/String;", false));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		//this.evlNBT = ClientCapHooks.login();
		li.add(new LabelNode());
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/capability/client/ClientCapHooks", "login", "()Lnet/minecraft/nbt/NBTTagCompound;", false));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;"));
		ctr.instructions.insert(ASMHelper.getFirstInstruction(ctr, Opcodes.PUTFIELD), li);
		
		//this.skindata = buf.readString(Short.MAX_VALUE);
		MethodNode read = ASMHelper.getMethodNode(classNode, new MCPSidedString("readPacketData", "func_148837_a").toString(), "(Lnet/minecraft/network/PacketBuffer;)V");
		InsnList rlist = new InsnList();
		rlist.add(new VarInsnNode(Opcodes.ALOAD, 0));
		rlist.add(new VarInsnNode(Opcodes.ALOAD, 1));
		rlist.add(new IntInsnNode(Opcodes.SIPUSH, 32767));
		rlist.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", new MCPSidedString("readString", "func_150789_c").toString(), "(I)Ljava/lang/String;", false));
		rlist.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		//this.evlNBT = ClientCapHooks.readNBT(buf);
		rlist.add(new LabelNode());
		rlist.add(new VarInsnNode(Opcodes.ALOAD, 0));
		rlist.add(new VarInsnNode(Opcodes.ALOAD, 1));
		rlist.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/capability/client/ClientCapHooks", "readNBT", "(Lnet/minecraft/network/PacketBuffer;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		rlist.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;"));
		read.instructions.insert(ASMHelper.getLastLabelNode(read, false), rlist);
	
		//buf.writeString(this.skindata);
		MethodNode write = ASMHelper.getMethodNode(classNode, new MCPSidedString("writePacketData", "func_148840_b").toString(), "(Lnet/minecraft/network/PacketBuffer;)V");
		InsnList wlist = new InsnList();
		String writestring = new MCPSidedString("writeString", "func_180714_a").toString();
		wlist.add(new VarInsnNode(Opcodes.ALOAD, 1));
		wlist.add(new VarInsnNode(Opcodes.ALOAD, 0));
		wlist.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		wlist.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", writestring, "(Ljava/lang/String;)Lnet/minecraft/network/PacketBuffer;", false));
		wlist.add(new InsnNode(Opcodes.POP));
		//buf.writeCompoundTag(this.evlNBT);
		wlist.add(new LabelNode());
		wlist.add(new VarInsnNode(Opcodes.ALOAD, 1));
		wlist.add(new VarInsnNode(Opcodes.ALOAD, 0));
		wlist.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;"));
		wlist.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", new MCPSidedString("writeCompoundTag", "func_150786_a").toString(), "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/network/PacketBuffer;", false));
		wlist.add(new InsnNode(Opcodes.POP));
		
		write.instructions.insert(ASMHelper.getLastLabelNode(write, false), wlist);
	}

	public void patchStopSteve(ClassNode classNode) 
	{
		//to prevent crashes always add the field even when ConfigCore#asm_stopSteve is disabled
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "canRender", "Z", null, null));
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "ssms", "J", null, null));
		ASMHelper.pubMinusFinal(classNode, true);
	}

	private void patchLanSkinsCache(ClassNode classNode) 
	{
		//AT the class even if patchLanSkins is false to prevent crashing
		ASMHelper.pubMinusFinal(classNode, true);
		
    	if(!ConfigCore.asm_patchLanSkins)
    		return;
    	
		//fillGameProfile(profile, true); --> fillGameProfile(profile, ASMHelper.rfalse(true));
		MethodNode m = ASMHelper.getMethodNode(classNode, "load", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;");
		AbstractInsnNode spot = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "fillGameProfile", "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;", false);
		m.instructions.insertBefore(spot, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/asm/util/ASMHelper", "rfalse", "(Z)Z", false));
	}

	public static void patchLanSkins(ClassNode classNode)
	{
		//AT the class even if patchLanSkins is false to prevent crashing
		ASMHelper.pubMinusFinal(classNode, true);
		
    	if(!ConfigCore.asm_patchLanSkins)
    		return;
    	
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
		
		//Prevents Mojang 429 Error as not requireSecure is disabled the error doesn't seem to happen. Imported fix from LanEssentials 2018
		//NOTE: uses hack confirmed Java 8 and lower where final method parameters and possibly local variables being final are not enforced at runtime
		if(ConfigCore.asm_patchLanSkins429)
		{
			MethodNode fill = ASMHelper.getMethodNode(classNode, "fillGameProfile", "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;");
			InsnList listFill = new InsnList();
			listFill.add(new InsnNode(Opcodes.ICONST_0));
			listFill.add(new VarInsnNode(Opcodes.ISTORE, 2));
			fill.instructions.insert(ASMHelper.getFirstInstruction(fill), listFill);
		}
		
		//Wraps return profile; --> return VanillaBugFixes#getCachedProfile(profile, this.insecureProfiles);
		MethodNode fillProps = ASMHelper.getMethodNode(classNode, "fillProfileProperties", "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;");
		InsnList lfill = new InsnList();
		lfill.add(new VarInsnNode(Opcodes.ALOAD, 0));
		lfill.add(new FieldInsnNode(Opcodes.GETFIELD, "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService", "insecureProfiles", "Lcom/google/common/cache/LoadingCache;"));
		lfill.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/eventhandler/VanillaBugFixes", "getCachedProfile", "(Lcom/mojang/authlib/GameProfile;Lcom/google/common/cache/LoadingCache;)Lcom/mojang/authlib/GameProfile;", false));
		
		AbstractInsnNode finsn = ASMHelper.getMethodInsnNode(fillProps, Opcodes.INVOKEINTERFACE, "com/google/common/cache/LoadingCache", "getUnchecked", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		if(finsn == null)
			finsn = ASMHelper.getVarLastInsnNode(fillProps, new VarInsnNode(Opcodes.ILOAD, 2));
		AbstractInsnNode fspot = ASMHelper.nextInsn(finsn, Opcodes.ARETURN).getPrevious();
		fillProps.instructions.insert(fspot, lfill);
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
		if(ConfigCore.asm_playermp)
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
		}
      	
      	//UUIDPatcher.patchCheck(profile)
      	MethodNode m = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/WorldServer;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/server/management/PlayerInteractionManager;)V");
      	InsnList l = new InsnList();
      	l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patchCheck", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;", false));
      	m.instructions.insertBefore(ASMHelper.getFirstMethodInsn(m, Opcodes.INVOKESPECIAL, "net/minecraft/entity/player/EntityPlayer", "<init>", "(Lnet/minecraft/world/World;Lcom/mojang/authlib/GameProfile;)V", false), l);
      	
      	//profile = this.getGameProfile();
      	InsnList list = new InsnList();
      	list.add(new VarInsnNode(Opcodes.ALOAD, 0));
      	list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayerMP", new MCPSidedString("getGameProfile", "func_146103_bH").toString(), "()Lcom/mojang/authlib/GameProfile;", false));
      	list.add(new VarInsnNode(Opcodes.ASTORE, 3));
      	AbstractInsnNode spot = ASMHelper.previousLabel(ASMHelper.getFieldNode(m, Opcodes.PUTFIELD, "net/minecraft/server/management/PlayerInteractionManager", new MCPSidedString("player", "field_73090_b").toString(), "Lnet/minecraft/entity/player/EntityPlayerMP;"));
      	m.instructions.insert(spot, list);
      	
      	//ClientCapHooks.registerServerCap(this);
      	InsnList last = new InsnList();
      	last.add(new VarInsnNode(Opcodes.ALOAD, 0));
      	last.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/capability/client/ClientCapHooks", "registerServerCap", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V", false));
      	m.instructions.insert(ASMHelper.getLastLabelNode(m, false), last);
    }
	
	public void transformMouse(ClassNode classNode) 
	{
		if(!ConfigCore.asm_mouse_ears)
			return;
		
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("doRenderLayer", "func_177141_a").toString(), "(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V");
		MethodInsnNode inif = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/entity/RenderPlayer", new MCPSidedString("bindTexture", "func_110776_a").toString(), "(Lnet/minecraft/util/ResourceLocation;)V", false);
		LabelNode label = ASMHelper.prevLabelR(inif);
		
		//prepend if(SkinEvent.fireMouse ||
		InsnList l = new InsnList();
		l.add(new LabelNode());
		l.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinEvent", "fireMouse", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
		l.add(new JumpInsnNode(Opcodes.IFNE, label));
		m.instructions.insert(l);
	}
	
	public void transformDinnerbone(ClassNode classNode) 
	{
		if(!ConfigCore.asm_dinnerbone)
			return;
		
		//Get Label inside the if statement
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("applyRotations", "func_77043_a").toString(), "(Lnet/minecraft/entity/EntityLivingBase;FFF)V");
		AbstractInsnNode db = ASMHelper.getLdcInsnNode(m, new LdcInsnNode("Dinnerbone"));
		AbstractInsnNode inif = ASMHelper.nextMethodInsnNode(db, Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", new MCPSidedString("translate", "func_179109_b").toString(), "(FFF)V", false);
		LabelNode label = ASMHelper.prevLabelR(inif);
		
		//prepend if(SkinEvent#fireDinnerbone(entityliving) ||
		InsnList l = new InsnList();
		l.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinEvent", "fireDinnerbone", "(Lnet/minecraft/entity/Entity;)Z", false));
		l.add(new JumpInsnNode(Opcodes.IFNE, label));
		l.add(new LabelNode());
		m.instructions.insert(ASMHelper.prevLineNumberNode(db), l);
	}
	
	public void patchFakePlayer(ClassNode classNode) 
	{
		MethodNode ctr = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/WorldServer;Lcom/mojang/authlib/GameProfile;)V");
		AbstractInsnNode spot = ASMHelper.getVarInsnNode(ctr, new VarInsnNode(Opcodes.ALOAD, 2));
		ctr.instructions.insert(spot, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patchFake", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;", false));
	}
	
	public void patchScorePlayerTeam(ClassNode classNode) 
	{
		if(!ConfigCore.asm_teams)
			return;

		String getText = new MCPSidedString("getTextWithoutFormattingCodes", "func_110646_a").toString();
		String formatString = new MCPSidedString("formatString", "func_142053_d").toString();
		
		//transform string --> TextFormatting.getTextWithoutFormattingCodes(string) 
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("formatPlayerName", "func_96667_a").toString(), "(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;");
		AbstractInsnNode spot = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/scoreboard/Team", formatString, "(Ljava/lang/String;)Ljava/lang/String;", false).getPrevious();
		m.instructions.insert(spot, new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/text/TextFormatting", getText, "(Ljava/lang/String;)Ljava/lang/String;", false));
		
		if(!ConfigCore.asm_teams_full)
			return;
		
		//input = TextFormatting.getTextWithoutFormattingCodes(input);
		MethodNode m2 = ASMHelper.getMethodNode(classNode, formatString, "(Ljava/lang/String;)Ljava/lang/String;");
		InsnList l2 = new InsnList();
		l2.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/text/TextFormatting", getText, "(Ljava/lang/String;)Ljava/lang/String;", false));
		l2.add(new VarInsnNode(Opcodes.ASTORE, 1));
		m2.instructions.insert(ASMHelper.getFirstInstruction(m2), l2);
	}
	
	public void patchGuiIngameForge(ClassNode classNode) 
	{
		if(!ConfigCore.asm_guiTabOverlay)
			return;
		
		MethodNode m = ASMHelper.getMethodNode(classNode, "renderPlayerList", "(II)V");
		AbstractInsnNode inif = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/GuiPlayerTabOverlay", new MCPSidedString("updatePlayerList", "func_175246_a").toString(), "(Z)V", false);
		AbstractInsnNode spot = ASMHelper.prevLineNumberNode(ASMHelper.prevJumpInsnNode(inif));
		
		//prepend GuiTabOverlayEvent.fire() || false &&
		InsnList l = new InsnList();
		l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/event/client/GuiTabOverlayEvent", "fire", "()Z", false));
		l.add(new JumpInsnNode(Opcodes.IFNE, ASMHelper.prevLabelR(inif)));
		l.add(new InsnNode(Opcodes.ICONST_0));
		l.add(new JumpInsnNode(Opcodes.IFEQ, ASMHelper.nextJumpInsnNode(spot).label));
		m.instructions.insert(spot, l);
	}
	
	public void transformGuiPlayerTabOverlay(ClassNode classNode) 
	{
		if(!ConfigCore.asm_dinnerbone)
			return;
		
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("renderPlayerlist", "func_175249_a").toString(), "(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V");
		AbstractInsnNode ab = ASMHelper.getLdcInsnNode(m, new LdcInsnNode("Dinnerbone"));
		VarInsnNode targ = (VarInsnNode) ASMHelper.nextInsn(ab, Opcodes.ISTORE);
		int indexPlayer = ASMHelper.prevLocalVarIndex(m, ab, Opcodes.ASTORE, "Lnet/minecraft/entity/player/EntityPlayer;");
		int indexInfo =   ASMHelper.prevLocalVarIndex(m, ab, Opcodes.ASTORE, "Lnet/minecraft/client/network/NetworkPlayerInfo;");
		
		//if(!dinnerbone) dinnerbone = SkinEvent.fireDinnerbone(player, info);
		InsnList li = new InsnList();
		LabelNode l1 = new LabelNode();
		li.add(new VarInsnNode(Opcodes.ILOAD, targ.var));
		li.add(new JumpInsnNode(Opcodes.IFNE, l1));
		li.add(new LabelNode());
		li.add(new VarInsnNode(Opcodes.ALOAD, indexPlayer));
		li.add(new VarInsnNode(Opcodes.ALOAD, indexInfo));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinEvent", "fireDinnerbone", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/network/NetworkPlayerInfo;)Z", false));
		li.add(new VarInsnNode(Opcodes.ISTORE, targ.var));
		li.add(l1);
		m.instructions.insert(ASMHelper.nextLabelR(targ), li);
	}
	
	public void transformMinecraftProfileTexture(ClassNode classNode)
	{
		if(!ConfigCore.asm_skinURLHook)
			return;
		
		//public this.cachedHash;
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "cachedHash", "Ljava/lang/String;", null, null));
		
		MethodNode m = ASMHelper.getMethodNode(classNode, "getHash", "()Ljava/lang/String;");
		
		//if(this.cachedHash != null) return this.cachedHash;
		InsnList li = new InsnList();
		li.add(new LabelNode());
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "cachedHash", "Ljava/lang/String;"));
		LabelNode l1 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFNULL, l1));
		LabelNode l2 = new LabelNode();
		li.add(l2);
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "cachedHash", "Ljava/lang/String;"));
		li.add(new InsnNode(Opcodes.ARETURN));
		li.add(l1);
		m.instructions.insert(li);
		
		//Wrap getBaseName(this.url); --> getBaseName(SkinEvent.HashURLEvent.fire(this.url));
		AbstractInsnNode targ = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKESTATIC, "org/apache/commons/io/FilenameUtils", "getBaseName", "(Ljava/lang/String;)Ljava/lang/String;", false).getPrevious();
		
		if(targ == null)
			targ = ASMHelper.prevFieldInsnNode(ASMHelper.getLastInstruction(m, Opcodes.ARETURN), new FieldInsnNode(Opcodes.GETFIELD, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "url", "Ljava/lang/String;"));
		
		m.instructions.insert(targ, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinEvent$HashURLEvent", "fire", "(Ljava/lang/String;)Ljava/lang/String;", false));
	
		//Create MinecraftProfileTexture#cacheHash()
		/**
		 * 	public void cacheHash()
		    {
				if(this.cachedHash == null)
					this.cachedHash = this.getHash();
		    }
		 */
		if(ASMHelper.getMethodNode(classNode, "cacheHash", "()V") != null)
			return;
		
		InsnList lc = new InsnList();
		LabelNode lc0 = new LabelNode();
		lc.add(lc0);
		lc.add(new VarInsnNode(Opcodes.ALOAD, 0));
		lc.add(new FieldInsnNode(Opcodes.GETFIELD, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "cachedHash", "Ljava/lang/String;"));
		LabelNode lc1 = new LabelNode();
		lc.add(new JumpInsnNode(Opcodes.IFNONNULL, lc1));
		LabelNode lc2 = new LabelNode();
		lc.add(lc2);
		lc.add(new VarInsnNode(Opcodes.ALOAD, 0));
		lc.add(new VarInsnNode(Opcodes.ALOAD, 0));
		lc.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "getHash", "()Ljava/lang/String;", false));
		lc.add(new FieldInsnNode(Opcodes.PUTFIELD, "com/mojang/authlib/minecraft/MinecraftProfileTexture", "cachedHash", "Ljava/lang/String;"));
		lc.add(lc1);
		lc.add(new InsnNode(Opcodes.RETURN));
		LabelNode lc3 = new LabelNode();
		lc.add(lc3);
		
		MethodNode cachedHash = new MethodNode(Opcodes.ACC_PUBLIC, "cacheHash", "()V", null, null);
		cachedHash.instructions = lc;
		cachedHash.maxStack = 2;
		cachedHash.maxLocals = 1;
		cachedHash.localVariables.add(new LocalVariableNode("this", "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;", null, lc0, lc3, 0));
		
		classNode.methods.add(cachedHash);
	}
	
	public void transformLayerCape(ClassNode classNode) 
	{
		if(!ConfigCore.asm_skinEnchantedCapes)
			return;
		
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("doRenderLayer", "func_177141_a").toString(), "(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V");
		AbstractInsnNode spot = ASMHelper.getFirstMethodInsn(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/model/ModelPlayer", new MCPSidedString("renderCape", "func_178728_c").toString(), "(F)V", false);
		Float scale = new Float("0.0625");
		//Attempt to dynamically get the scale in case of another mod's ASM
		if(spot.getPrevious() instanceof LdcInsnNode)
			scale = (Float) ((LdcInsnNode) spot.getPrevious()).cst;
		
		//SkinEvent.CapeEnchant.render(this.playerRenderer, entitylivingbaseIn, partialTicks, 0.0625F);
		InsnList li = new InsnList();
		li.add(new LabelNode());
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/layers/LayerCape",  new MCPSidedString("playerRenderer", "field_177167_a").toString(), "Lnet/minecraft/client/renderer/entity/RenderPlayer;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.FLOAD, 4));
		li.add(new LdcInsnNode(scale));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinEvent$CapeEnchant", "render", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/client/entity/AbstractClientPlayer;FF)V", false));

		m.instructions.insert(spot, li);
	}
	
	public void fixMorePlayerModels(ClassNode classNode)
	{
		if(!ConfigCore.asm_MPMCompat)
			return;
		
		//if( !((AbstractClientPlayer) pl).hasPlayerInfo() ) return;
		MethodNode method = ASMHelper.getMethodNodeByName(classNode, "loadPlayerResource");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/entity/AbstractClientPlayer"));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/AbstractClientPlayer", new MCPSidedString("hasPlayerInfo", "func_152122_n").toString(), "()Z", false));
		LabelNode l1 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFNE, l1));
		LabelNode l2 = new LabelNode();
		li.add(l2);
		li.add(new InsnNode(Opcodes.RETURN));
		li.add(l1);
		method.instructions.insert(ASMHelper.getFirstInstruction(method), li);
	}
	
	public void transformMoBends(ClassNode classNode) 
	{
		//public static Method evlMethod;
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "evlMethod", "Ljava/lang/reflect/Method;", null, null));
	
		//Since <clinit> may not exist add it to every constructor
		for(MethodNode m : classNode.methods)
		{
			if(m.name.equals("<init>"))
			{
				//evlMethod = ReflectionUtil.getMethod(BendsCapeRenderer.class, "render", float.class);
				InsnList list = new InsnList();
				list.add(new LdcInsnNode(Type.getType("Lgoblinbob/mobends/standard/client/renderer/entity/BendsCapeRenderer;")));
				list.add(new LdcInsnNode("render"));
				list.add(new InsnNode(Opcodes.ICONST_1));
				list.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));
				list.add(new InsnNode(Opcodes.DUP));
				list.add(new InsnNode(Opcodes.ICONST_0));
				list.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;"));
				list.add(new InsnNode(Opcodes.AASTORE));
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/api/ReflectionUtil", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "goblinbob/mobends/standard/client/renderer/entity/layers/LayerCustomCape", "evlMethod", "Ljava/lang/reflect/Method;"));
				m.instructions.insert(ASMHelper.getLastPutField(m), list);
			}
		}
		
		MethodNode m = ASMHelper.getMethodNodeByName(classNode, new MCPSidedString("doRenderLayer", "func_177141_a").toString());
		AbstractInsnNode targ = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "goblinbob/mobends/standard/client/renderer/entity/BendsCapeRenderer", "render", "(F)V", false);
		
		//CapeRenderer.render(this.playerRenderer, player, evlMethod, this.capeRenderer, partialTicks, 0.0625F);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "goblinbob/mobends/standard/client/renderer/entity/layers/LayerCustomCape", "playerRenderer", "Lnet/minecraft/client/renderer/entity/RenderPlayer;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "goblinbob/mobends/standard/client/renderer/entity/layers/LayerCustomCape", "evlMethod", "Ljava/lang/reflect/Method;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "goblinbob/mobends/standard/client/renderer/entity/layers/LayerCustomCape", "capeRenderer", "Lgoblinbob/mobends/standard/client/renderer/entity/BendsCapeRenderer;"));
		li.add(new VarInsnNode(Opcodes.FLOAD, 4));
		li.add(new LdcInsnNode(new Float("0.0625")));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/client/CapeRenderer", "render", "(Lnet/minecraft/client/renderer/entity/RenderPlayer;Lnet/minecraft/entity/EntityLivingBase;Ljava/lang/reflect/Method;Ljava/lang/Object;FF)V", false));
		
		m.instructions.insert(targ, li);
	}

}
