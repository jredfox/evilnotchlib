package com.EvilNotch.lib.asm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;


public class TestTransformer 
{	
	public static void transformClass(ClassNode ClassToTransform,String className,String clazzNewMethod,String method_name,String method_desc,String c,String v,String obMethod) 
	{
		for (MethodNode method : ClassToTransform.methods)
		{
			if (FMLCorePlugin.isObf && method.name.equals(c) && method.desc.equals(v)|| !FMLCorePlugin.isObf && method.name.equals(method_name) && method.desc.equals(method_desc))
			{
				try
				{
					String s = "assets/evilnotchlib/asm/" + clazzNewMethod;
					System.out.println(s + " " + obMethod);
					InputStream stream = Transformer.class.getClassLoader().getResourceAsStream(s);
					System.out.println(stream == null);
					MethodNode mn = GetMethodNode(stream, obMethod, method_desc);
					method.localVariables.clear();
//					patchInstructions(mn,className,clazzNewMethod.replaceAll("\\.", "/"));
					method.instructions = mn.instructions;
					method.localVariables = GetLocalVar(mn,className);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}	
		}
	}

	public static void patchInstructions(MethodNode mn, String className, String NewClassName) 
	{
		InsnList il = mn.instructions;
		AbstractInsnNode[] al = il.toArray();
		for(AbstractInsnNode ain : al)
		{
			if(ain instanceof MethodInsnNode)
			{
				MethodInsnNode min = (MethodInsnNode)ain;				
				if(min.owner.equals(NewClassName))
				{
					min.owner=className.replaceAll("\\.", "/");
					System.out.println("Patched: " + min.owner);
				}
				
			}
			else if(ain instanceof FieldInsnNode)
			{
				FieldInsnNode fin = (FieldInsnNode)ain;
				if(fin.owner.equals(NewClassName))
				{
					fin.owner=className.replaceAll("\\.", "/");
					System.out.println("Patched: " + fin.owner);
				}
			}
		}
	}
	public static MethodNode GetMethodNode(Class ourClass, String method_name,String method_desc) throws IOException 
	{
		String className = ourClass.getName();
		String classAsPath = className.replaceAll("\\.", "/") + ".class";
		InputStream stream = ourClass.getClassLoader().getResourceAsStream(classAsPath);
		return GetMethodNode(stream,method_name,method_desc);
	}
	public static MethodNode GetMethodNode(InputStream stream, String method_name,String method_desc) throws IOException 
	{
		byte[] newbyte = IOUtils.toByteArray(stream);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(newbyte);
		classReader.accept(classNode,0);
		
		for (MethodNode method : classNode.methods)
		{
			//System.out.println("Name: " + method.name + " Desc: " + method.desc);
			if (method.name.equals(method_name) && method.desc.equals(method_desc))
			{
				return method;
			}
		}
		return null;
	}

	public static List<LocalVariableNode> GetLocalVar(MethodNode method, String Name) throws IOException 
	{
		List<LocalVariableNode> l = method.localVariables;
		for(LocalVariableNode lvn : l)
		{
			if(lvn.name.equals("this"))
			{
				lvn.desc = "L" + Name.replaceAll("\\.", "/") + ";";
			}
		}
		return l;
	}
}
