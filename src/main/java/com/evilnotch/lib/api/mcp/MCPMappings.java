package com.evilnotch.lib.api.mcp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.main.loader.LoadingStage;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.Line;
import com.evilnotch.lib.util.line.config.ConfigLine;

import net.minecraftforge.common.MinecraftForge;

/**
 * get srg name in deob from class and field without having to constantly look everything up yourselves.
 * This is for deob only however ObfHelper to get deob and ob class names based on what forge has. This is for fields and methods
 * @author jredfox
 */
public class MCPMappings {
	
    /**
     * is null before or after pre init
     */
	public static Map<String,MCPEntry> fields = new HashMap();
	/**
	 * is null before or after pre init
	 */
	public static Map<String,MCPEntry> methods = new HashMap();
	
	/**
	 * the cached data you grabbed
	 */
	public static Set<MCPEntry> cached = new LinkedHashSet<MCPEntry>();
	public static boolean isCached = false;
	
	/**
	 * get the srg name without constant lookup of the file in deob only after you get srgname use MCPSidedString(deob,ob) instead
	 */
	public static String getSRGField(Class clazz, String name)
	{
		throwExceptions();
		
		MCPEntry entry = getEntry(clazz, name, null, fields);
		if(entry != null)
		{
			cached.add(new MCPEntry(entry.mcp.deob, entry.mcp.ob, clazz.getName()));
			return entry.mcp.ob;
		}
		return null;
	}
	
	/**
	 * get the srg name without constant lookup of the file in deob only after you get srgname use MCPSidedString(deob,ob) instead
	 */
	public static String getSRGMethod(Class clazz, String name, Class... params)
	{
		throwExceptions();
		
		String desc = ASMHelper.getMethodDescriptor(clazz, name, params);
		if(desc == null)
			return null;
		MCPEntry entry = getEntry(clazz, name, desc, methods);
		if(entry != null)
		{
			String deob = JavaUtil.asStringList(params).toString();
			deob = "(" + deob.substring(1, deob.length()-1) + ")";
			cached.add(new MCPEntry(entry.mcp.deob, entry.mcp.ob, deob, desc, clazz.getName()));
			return entry.mcp.ob;
		}
		return null;
	}
	
	private static void throwExceptions() 
	{
		if(!LoaderMain.isDeObfuscated)
			throw new RuntimeException("This Method Is for deobfuscated use only");
		else if(!LoaderMain.isLoadingStage(LoadingStage.PREINIT))
			throw new RuntimeException("This is for pre init use only to get srg names! After you got the srg use MCPSidedString(deob,ob) instead of doing this");
	}

	public static MCPEntry getEntryFromOb(String ob, ArrayList<MCPEntry> list)
	{
		throwExceptions();
		
		for(MCPEntry e : list)
		{
			if(e.mcp.ob.equals(ob))
				return e;
		}
		return null;
	}
	
	private static MCPEntry getEntry(Class clazz, String field, String desc, Map<String,MCPEntry> list)
	{
		MCPEntry entry = null;
		for(MCPEntry e : list.values())
		{
			String name = e.mcp.deob;
			if(name.equals(field))
			{
				ArrayList<Class> clazzes = e.getClasses();
				for(Class c : clazzes)
				{
					if(JavaUtil.isClassExtending(c, clazz))
					{
						entry = e;
						break;
					}
				}
			}
		}
		
		if(list == methods && entry != null)
		{
			if(!desc.equals(entry.desc.deob))
				return null;
		}
		
		return entry;
	}
	
	public static void cacheMCPApplicable()
	{
		if(!isCached)
			cacheMCP();
	}
	
	public static void cacheMCP()
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		isCached = true;
		
		File build = new File(new File(System.getProperty("user.dir")).getParentFile(),"build.gradle");
		List<String> lines = JavaUtil.getFileLines(build,false);
		String mcp_build = null;
		for(String s : lines)
		{
			String whitespaced = JavaUtil.toWhiteSpaced(s);
			if(whitespaced.startsWith("mappings=\""))
			{
				mcp_build = JavaUtil.parseQuotes(whitespaced, 0, "\"");
				break;
			}
		}
		boolean snapshot = mcp_build.contains("snapshot_");
		File srgFile = null;
		if(snapshot)
		{
			srgFile = new File(System.getProperty("user.home") + "/.gradle/caches/minecraft/de/oceanlabs/mcp/mcp_snapshot/" + mcp_build.substring("snapshot_".length(), mcp_build.length()) + "/" + MinecraftForge.MC_VERSION + "/srgs/mcp-srg.srg");
		}
		else
		{
			srgFile = new File(System.getProperty("user.home") + "/.gradle/caches/minecraft/de/oceanlabs/mcp/mcp_stable/" + mcp_build.substring("stable_".length(), mcp_build.length()) + "/" + MinecraftForge.MC_VERSION + "/srgs/mcp-srg.srg");
		}
		
		long time = System.currentTimeMillis();
		parseSRGFile(srgFile);
		JavaUtil.printTime(time, "Done Parsing SRG:");
	}
	
	private static void parseSRGFile(File srgFile) 
	{
		if(!srgFile.exists())
		{
			System.out.println("Mac User Detected?");
			srgFile = new File(srgFile.getAbsolutePath().substring(1, srgFile.getAbsolutePath().length()-1 ));
		}
		List<String> list = JavaUtil.getFileLines(srgFile, false);
		for(String s : list)
		{
			if(s.startsWith("CL:"))
				continue;
			boolean method = s.startsWith("MD:");
			
			String[] parts = s.split("\\s+");
			String[] clazz_srg = parts[method ? 3 : 2].split("/");
			String[] clazz_deob = parts[1].split("/");
			
			String srg = clazz_srg[clazz_srg.length-1];
			String deob = clazz_deob[clazz_deob.length-1];
			StringBuilder b = new StringBuilder();
			for(int i=0;i<clazz_deob.length-1;i++)
			{
				b.append(clazz_deob[i] + "/");
			}
			
			String clazz = b.toString();
			clazz = clazz.substring(0, clazz.length()-1);//remove the / before and after
			
			if(method ? methods.containsKey(srg) : fields.containsKey(srg))
			{
				MCPEntry entry = method ? methods.get(srg) : fields.get(srg);
				entry.classes.add(clazz);
				continue;
			}
			
			if(method)
			{
				String descObf = parts[4];
				String descDeob = parts[2];
				MCPEntry entry = new MCPEntry(deob, srg, descDeob, descObf, clazz);
				methods.put(srg, entry);
			}
			else
			{
				fields.put(srg, new MCPEntry(deob, srg, clazz));
			}
		}
	}

	/**
	 * after pre init these maps don't need to be stored in memory
	 */
	public static void init() 
	{
		//make the api usable output a user freindly file
		if(LoaderMain.isDeObfuscated && !cached.isEmpty())
		{
			File f = new File(System.getProperty("user.home") + "/Desktop/srgs.txt");
			ConfigLine cfg = new ConfigLine(f);
			for(MCPEntry entry : cached)
			{
				MCPSidedString str = entry.mcp;
				cfg.addLine(new Line(entry.classes.get(0) + ", " + "new MCPSidedString(\"" + str.deob + "\", \"" + str.ob + "\"" + ")" + (entry.isMethod() ? ", " + entry.desc.deob + ", " + entry.desc.ob : "" ) ));
			}
			cfg.saveConfig();
		}
		clearMaps();
	}

	public static void clearMaps() 
	{
		MCPMappings.methods.clear();
		MCPMappings.fields.clear();
		MCPMappings.cached.clear();
	}

}
