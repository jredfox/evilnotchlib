package com.EvilNotch.lib.asm;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;


public class TestTransformer 
{
	public static InsnList getInstructions(Class ourClass,String method_name,String method_desc) throws IOException
	{
		String className = ourClass.getName();
		String classAsPath = className.replace('.', '/') + ".class";
		InputStream stream = ourClass.getClassLoader().getResourceAsStream(classAsPath);
		byte[] newbyte = IOUtils.toByteArray(stream);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(newbyte);
		classReader.accept(classNode,0);
		
		for (MethodNode method : classNode.methods)
		 {
			//System.out.println("Name: " + method.name + " Desc: " + method.desc);
			 if (method.name.equals(method_name) && method.desc.equals(method_desc))
			 {
				return method.instructions;
			 }
		 }
		return null;
	}
	
	public static void transformClass(ClassNode ClassToTransform,Class ClassContainingNewMethod,String method_name,String method_desc,String c,String v) 
	{
		for (MethodNode method : ClassToTransform.methods)
		{
			if (FMLCorePlugin.isObf && method.name.equals(c) && method.desc.equals(v)|| !FMLCorePlugin.isObf && method.name.equals(method_name) && method.desc.equals(method_desc))
			{
				try
				{
					method.localVariables.clear();
					method.instructions = getInstructions(ClassContainingNewMethod,method_name,method_desc);
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}	
		}
	}
}
