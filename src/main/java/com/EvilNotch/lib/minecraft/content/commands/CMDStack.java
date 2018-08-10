package com.EvilNotch.lib.minecraft.content.commands;

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
