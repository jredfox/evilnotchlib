package com.evilnotch.lib.util.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.evilnotch.lib.util.JavaUtil;

/**
 * Doesn't support zips inside of zips, adding files/ZipEntires from another file
 * Only default zip manipulation within a zip file currently
 * updateArchive() to have delete/add/set actions done 
 * @author Jredfox
 *
 */
public class Archive {
	public File file;
	public File temp;
	public ZipFile zip;
	public ArrayList<ArchiveEntry> entries = new ArrayList<ArchiveEntry>();
	
	public Archive(File file)
	{
		this.file = file;
		this.temp = new File(file.getParentFile(),file.getName() + ".zip");
		cacheZip();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cacheZip() 
	{
		try{
			this.zip = new ZipFile(this.file);
			this.entries = new ArrayList();
			
			 Enumeration<? extends ZipEntry> efake = this.zip.entries();
			  while(efake.hasMoreElements())
		            entries.add(new ArchiveEntry(efake.nextElement()));
			  this.zip.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Deletes First entry instance
	 * @param entry
	 */
	public void deleteEntry(ZipEntry entry,boolean delete_all)
	{
		Iterator<ArchiveEntry> it = this.entries.iterator();
		while(it.hasNext())
		{
			ArchiveEntry e = it.next();
			if(e.output.getName().equals(entry.toString()))
			{
				it.remove();
				if(!delete_all)
					break;
			}
		}
	}
	/**
	 * Note when adding entry make sure the output data doesn't exist already
	 * @param entry
	 */
	public void addEntry(ArchiveEntry entry)
	{
		this.entries.add(entry);
	}
	public void addAndReplaceEntry(ArchiveEntry entry)
	{
		deleteEntry(entry.output,true);
		this.entries.add(entry);
	}
	 /** Note when adding entry make sure the output data doesn't exist already
	  * @param entry
	  * @param index
	  */
	public void addEntry(ArchiveEntry entry,int index)
	{
		this.entries.add(index,entry);
	}
	public void setEntry(ArchiveEntry entry,int index)
	{
		this.entries.set(index,entry);
	}
	
	public void addEntryList(ArrayList<ArchiveEntry> list)
	{
		this.entries.addAll(list);
	}
	public void addEntryList(ArrayList<ArchiveEntry> list, int index)
	{
		this.entries.addAll(index,list);
	}
	public void setEntryList(ArrayList<ArchiveEntry> list, int index)
	{
		JavaUtil.setList(this.entries,list,index);
	}
	public void deleteEntryList(ArrayList<ArchiveEntry> list)
	{
		this.entries.removeAll(list);
	}
	
	public boolean containsEntry(ZipEntry entry,boolean output)
	{
		for(ArchiveEntry e : this.entries)
		{
			if(e.input.getName().equals(entry.getName()) && !output)
				return true;
			if(e.output.getName().equals(entry.getName()) && output)
				return true;
		}
		return false;
	}
	public ArchiveEntry getEntry(int index)
	{
		return this.entries.get(index);
	}
	public int getIndex(ArchiveEntry e)
	{
		for(int i=0;i<this.entries.size();i++)
			if(e.equals(entries.get(i)))
				return i;
		return -1;
	}
	public void deleteEntry(int index)
	{
		this.entries.remove(index);//Remove at a specific index
	}
	public void deleteEntry(int index1,int index2)
	{
		JavaUtil.deleteListRange(this.entries,index1,index2);
	}
	
	/**
	 * Writes the file again with the current zip being manipulated
	 */
	public void updateArchive()
	{
		try{
			this.zip = new ZipFile(this.file);
			if(!temp.exists())
				temp.createNewFile();
			ZipOutputStream outputstream = new ZipOutputStream(new FileOutputStream(this.temp));
			long start = System.currentTimeMillis()/1000;
			//System.out.println("Zipping:" + start);
			
	         //Transfer files to temp
	         for(ArchiveEntry entry : this.entries)
	         {
	        	 ZipEntry tst = new ZipEntry(entry.output.getName());
	        	 outputstream.putNextEntry(tst);
	        	    
	        	 byte[] buffer = new byte[1048576/2];//Half a megabyte per iteration
	        	 InputStream reader = zip.getInputStream(entry.input);
	        	 int len;
	        	 while ((len = reader.read(buffer)) > 0) {
	        	     outputstream.write(buffer, 0, len);
	        	 }
	        	   reader.close();
	         }
			
			outputstream.closeEntry();
			outputstream.close();
			this.zip.close();
			
			Files.move(Paths.get(this.temp.getPath()), Paths.get(this.file.getPath()), StandardCopyOption.REPLACE_EXISTING);
			long finish = System.currentTimeMillis()/1000;
			System.out.println("Zipping Took:" + (finish-start) + " Seconds");
		}catch(Exception e){e.printStackTrace();}
	}

}