package com.evilnotch.lib.minecraft.command;

import net.minecraft.command.ICommandSender;

public class CMDStack extends CMDDim{
	
	@Override
	public String getName(){
		return "tpStack";
	}
	@Override
	public String getUsage(ICommandSender sender){
		return "/" + getName();
	}
	@Override
	public boolean wholeStack(){return true;}

}
