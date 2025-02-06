package com.evilnotch.lib.minecraft.command;

import com.evilnotch.lib.minecraft.util.TeleportUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

public class CMDTeleport extends CommandTeleport{
	

    /**
     * Callback for when the command is executed
     */
    @Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException("commands.teleport.usage", new Object[0]);
        }
        else
        {
            Entity entity = getEntity(server, sender, args[0]);

            if (entity.world != null)
            {
                int i = 4096;
                Vec3d vec3d = sender.getPositionVector();
                int j = 1;
                CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(vec3d.x, args[j++], true);
                CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(vec3d.y, args[j++], -4096, 4096, false);
                CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(vec3d.z, args[j++], true);
                Entity entity1 = sender.getCommandSenderEntity() == null ? entity : sender.getCommandSenderEntity();
                CommandBase.CoordinateArg commandbase$coordinatearg3 = parseCoordinate(args.length > j ? (double)entity1.rotationYaw : (double)entity.rotationYaw, args.length > j ? args[j] : "~", false);
                ++j;
                CommandBase.CoordinateArg commandbase$coordinatearg4 = parseCoordinate(args.length > j ? (double)entity1.rotationPitch : (double)entity.rotationPitch, args.length > j ? args[j] : "~", false);
                doTeleport(entity,server, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg3, commandbase$coordinatearg4);
                notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult()});
            }
        }
    }

    /**
     * Perform the actual teleport
     *  
     * @param teleportingEntity the entity being teleported
     * @throws WrongUsageException 
     */
    public void doTeleport(Entity e,MinecraftServer server, CommandBase.CoordinateArg argX, CommandBase.CoordinateArg argY, CommandBase.CoordinateArg argZ, CommandBase.CoordinateArg argYaw, CommandBase.CoordinateArg argPitch) throws WrongUsageException
    {
    	TeleportUtil.teleportStackAtIndex(e, server, argX.getResult(), argY.getResult(), argZ.getResult(), (float)argYaw.getResult(), (float)argPitch.getResult(), e.dimension);
    }

}
