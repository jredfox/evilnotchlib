package com.EvilNotch.lib.util.registry;

import java.util.ArrayList;

import net.minecraft.command.ICommand;

public class GeneralRegistry {
	
	protected static ArrayList<ICommand> cmds = new ArrayList();
	
	public static void registerCommand(ICommand cmd){
		cmds.add(cmd);
	}
	public static void removeCmd(int index){
		cmds.remove(index);
	}
	public static ArrayList<ICommand> getCmdList(){
		return cmds;
	}

}
