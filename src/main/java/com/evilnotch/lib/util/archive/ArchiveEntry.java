package com.evilnotch.lib.util.archive;

import java.util.zip.ZipEntry;

public class ArchiveEntry {
	
	public ZipEntry input;
	public ZipEntry output;
	
	public ArchiveEntry(ZipEntry input)
	{
		this.input = input;
		this.output  = input;
	}
	public ArchiveEntry(ZipEntry input, ZipEntry output)
	{
		this.input = input;
		this.output = output;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof ArchiveEntry))
				return false;
		return this.input.getName().equals(((ArchiveEntry)obj).input.getName()) && this.output.getName().equals(((ArchiveEntry)obj).output.getName());
	}

}
