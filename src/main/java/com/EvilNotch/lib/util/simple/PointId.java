package com.evilnotch.lib.util.simple;

import java.awt.Point;

public class PointId {
	
	public Point point = null;
	public String id = null;
	
	public PointId(int x, int y){
		this(x,y,"null");
	}
	
	public PointId(int x, int y, String id)
	{
		this.point = new Point(x,y);
		this.id = id;
	}
	
	public int getX(){
		return this.point.x;
	}
	public int getY(){
		return this.point.y;
	}
	public void setLocation(int x, int y){
		this.point.setLocation(x, y);
	}

}
