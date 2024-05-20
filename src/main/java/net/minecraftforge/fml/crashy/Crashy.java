package net.minecraftforge.fml.crashy;

import java.awt.GraphicsEnvironment;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.crashy.Crashy.ModEntry.MODSIDE;

/**
 * Copy this class and GuiCrashReport.jar into your project to use
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
		
    	File guicrashjar = new File("GuiCrashReport.jar").getAbsoluteFile();
    	if(!guicrashjar.exists())
    	{
    		InputStream in = Crashy.class.getClassLoader().getResourceAsStream("GuiCrashReport.jar");
    		if(in == null)
    			throw new NullPointerException();
    		OutputStream out = new FileOutputStream(guicrashjar);
    		copy(in, out);
    		closeQuietly(in);
    		closeQuietly(out);
    	}
		String java = System.getProperty("java.home") + "/bin/java".replace("/", File.separator);
		ProcessBuilder pb = new ProcessBuilder(java, "-jar", guicrashjar.getPath(), msg);
		try 
		{
			pb.start();
		} 
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		if(exit)
		{
			exit(-1);
		}
	}
	
    public static void displayMissingMods(ModEntry... mods)
    {
    	String s = "Minecraft is:" + ModEntry.CURRENT_SIDE + "\n\n";
    	s += "Missing Mods";
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
	
	//_______________________________________________START IOUTILS METHODS REQUIRED______________________________\\
	public static final int BUFFER_SIZE = 1048576/2;
	public static final boolean IS_HEADLESS = GraphicsEnvironment.isHeadless();
	/**
	 * Gets set by ConfigCore#load
	 */
	public static boolean GUI = true;
	/**
	 * enforce thread safety with per thread local variables
	 */
	public static final ThreadLocal<byte[]> bufferes = new ThreadLocal<byte[]>()
	{
        @Override
        protected byte[] initialValue() 
        {
			return new byte[BUFFER_SIZE];
        }
	};
	
	public static void copy(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = bufferes.get();
		int length;
   	 	while ((length = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, length);
		}
	}

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
	
	public static void closeQuietly(Closeable clos)
	{
		try 
		{
			if(clos != null)
				clos.close();
		}
		catch (IOException e)
		{
			
		}
	}

}
