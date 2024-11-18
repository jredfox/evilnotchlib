package net.minecraftforge.fml.crashy;

import java.awt.GraphicsEnvironment;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.crashy.Crashy.ModEntry.MODSIDE;

/**
 * Copy this package into your project so you can have a Crash Report with a GUI while not keeping the original Process Alive
 */
public class Crashy {
	
    public static void crash(String msg, Throwable t, boolean gui)
    {
		CrashReport crashreport = CrashReport.makeCrashReport(t, msg);
		crashreport.makeCategory(msg);
		
        File file1 = new File("crash-reports").getAbsoluteFile();
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        System.out.println(crashreport.getCompleteReport());

        int retVal;
        if (crashreport.getFile() != null)
        {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashreport.getFile());
            retVal = -1;
        }
        else if (crashreport.saveToFile(file2))
        {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            retVal = -1;
        }
        else
        {
            System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            retVal = -2;
        }
        
        if(gui)
        {
        	try
        	{
        		displayCrash(msg, t);
        	}
        	catch(Throwable t2)
        	{
        		System.err.println("Unable to Crash Report GUI");
        		t2.printStackTrace();
        	}
        }
        
        exit(retVal);
    }
    
    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stacktrace = sw.toString();
        return stacktrace;
    }
    
    public static void displayCrash(String msg, Throwable t) throws IOException
    {
        displayCrash(msg + (t == null ? "" : ("\n" + getStackTrace(t))), false);
    }
    
	public static void displayCrash(String msg, boolean exit) throws IOException
	{
		//don't display GUI on web servers
		if(IS_HEADLESS || !GUI)
		{
			if(exit)
				exit(-1);
			return;
		}
		
		try
		{
			String java = System.getProperty("java.home") + "/bin/java".replace("/", File.separator);
			File jarFile = getFileFromClass(Crashy.class);
			//If Deobf or Jar is a Derp Jar get the Jar as a Folder
			if(jarFile.getPath().endsWith(".class"))
				jarFile = getDerpJar(jarFile, Crashy.class);
			ProcessBuilder pb = new ProcessBuilder(new String[]{java, "-cp", jarFile.getPath(), "net.minecraftforge.fml.crashy.Crash", msg});
			pb.start();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		if(exit)
			exit(-1);
	}
	
    public static void displayMissingMods(ModEntry... mods)
    {
    	String s = "Missing Mods For " + ModEntry.CURRENT_SIDE;
    	boolean missing = false;
    	for(ModEntry mod : mods)
    	{
    		if(mod.side == MODSIDE.ANY || mod.side == ModEntry.CURRENT_SIDE)
    		{
    			try
    			{
					Class.forName(mod.clazz);
				} 
    			catch (Throwable e)
    			{
    				s += "\n" + mod.name + " for Side:" + mod.side;
    				missing = true;
				}
    		}
    	}
    	if(missing)
    	{
        	try 
        	{
    			displayCrash(s, true);
    		} 
        	catch (Throwable e)
        	{
    			e.printStackTrace();
    		}
    	}
    }
	
	public static class ModEntry
	{
		public static MODSIDE CURRENT_SIDE = MODSIDE.SERVER;
		static
		{
			ClassLoader cl = Crashy.class.getClassLoader();
			CURRENT_SIDE = (cl.getResource("net/minecraft/client/renderer/RenderGlobal.class") != null || cl.getResource("buy.class") != null) ? MODSIDE.CLIENT : MODSIDE.SERVER;
		}
		
		public static enum MODSIDE
		{
			CLIENT(),
			SERVER(),
			ANY()
		}
		
		public String clazz;
		public String name;
		public MODSIDE side;
		
		public ModEntry(String clazz, String name, MODSIDE side)
		{
			this.clazz = clazz;
			this.name = name;
			this.side = side;
		}
	}
	
	//_______________________________________________START UTIL METHODS REQUIRED______________________________\\
	public static final boolean IS_HEADLESS = GraphicsEnvironment.isHeadless();
	/**
	 * Gets set by ConfigCore#load
	 */
	public static boolean GUI = true;
	
	public static void exit(int i) 
	{
		FMLCommonHandler fml = FMLCommonHandler.instance();
		if(fml != null)
		{
			fml.handleExit(i);
		}
		else
		{
			System.exit(i);
		}
	}
	
	/**
	 * get a file from a class Does not support Eclipse's Jar In Jar Loader but does support javaw java and URLClassLoaders
	 * This assumes the file is contained in an archive file such as a zip or a jar. If it's a folder derp jar then it will return the physical path of the .class file
	 */
	public static File getFileFromClass(Class clazz)
	{
		URL jarURL = clazz.getProtectionDomain().getCodeSource().getLocation();//get the path of the currently running jar
		return getFileFromURL(jarURL);
	}
	
	private static File getFileFromURL(URL jarURL) 
	{
		String j = jarURL.toExternalForm().replace("jar:/", "").replace("jar:", "");
		if(j.contains("!"))
			j = j.substring(0, j.indexOf('!'));
		return getFileFromURL(j);
	}

	public static File getFileFromURL(String url)
	{
		try 
		{
			return new File(new URL(url).toURI()).getAbsoluteFile();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return The Root Directory of the Jar when the Jar is a Folder
	 */
	public static File getDerpJar(File clazzFile, Class clazz)
	{
		String pjar = clazzFile.getPath().replace("\\", "/");
		return new File(pjar.substring(0, pjar.lastIndexOf(clazz.getName().replace(".", "/") + ".class")));
	}

}
