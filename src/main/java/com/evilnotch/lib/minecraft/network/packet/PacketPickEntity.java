package com.evilnotch.lib.minecraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketPickEntity implements IMessage{
	
	public Vec3d vec;
	public int entityId;
	public boolean ctr;
	
	public PacketPickEntity(RayTraceResult trace, boolean ctr)
	{
		this.vec = trace.hitVec;
		this.entityId = trace.entityHit.getEntityId();
		this.ctr = ctr;
	}
	
	public PacketPickEntity()
	{
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) 
	{
		//vec3d
		buf.writeDouble(this.vec.x);
		buf.writeDouble(this.vec.y);
		buf.writeDouble(this.vec.z);
		
		//entity
		buf.writeInt(this.entityId);
		
		buf.writeBoolean(this.ctr);
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.vec = new Vec3d(buf.readDouble(),buf.readDouble(),buf.readDouble());
		this.entityId = buf.readInt();
		this.ctr = buf.readBoolean();
	}

}
