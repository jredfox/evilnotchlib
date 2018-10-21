package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPickBlock implements IMessage{
	
	public BlockPos pos;
	public Vec3d vec;
	public EnumFacing facing;
	
	public PacketPickBlock(){}
	
	public PacketPickBlock(RayTraceResult trace)
	{
		this.pos = trace.getBlockPos();
		this.vec = trace.hitVec;
		this.facing = trace.sideHit;
	}
	
	@Override
	public void toBytes(ByteBuf buf) 
	{
		//block pos
		buf.writeInt(this.pos.getX());
		buf.writeInt(this.pos.getY());
		buf.writeInt(this.pos.getZ());
		
		//the vec 3d
		buf.writeDouble(this.vec.x);
		buf.writeDouble(this.vec.y);
		buf.writeDouble(this.vec.z);
		
		//write the enum facing
		buf.writeInt(this.facing.getIndex());
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		//block pos
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		this.pos = new BlockPos(x,y,z);
		
		//vec3d
		this.vec = new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
		
		this.facing = EnumFacing.getFront(buf.readInt());
	}

}
