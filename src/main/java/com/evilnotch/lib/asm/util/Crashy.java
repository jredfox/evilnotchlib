package com.evilnotch.lib.asm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.compress.utils.IOUtils;

import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
    	File guicrashjar = new File("GuiCrashReport.jar").getAbsoluteFile();
    	if(!guicrashjar.exists())
    	{
    		InputStream in = Crashy.class.getClassLoader().getResourceAsStream("GuiCrashReport.jar");
    		OutputStream out = new FileOutputStream(guicrashjar);
    		IOUtils.copy(in, out);
    		IOUtils.closeQuietly(in);
    		IOUtils.closeQuietly(out);
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

	public static void exit(int i) 
	{
		FMLCommonHandler.instance().handleExit(-1);
	}

}
