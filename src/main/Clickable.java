package main;

import images.ImageFinder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

//for a clickable object
public class Clickable {

	//x and y are top left of clickable
	protected int xPos, yPos, width, height;
	protected Image image = null;
	
	//at a position, with a generic color
	public Clickable(String msg, int x, int y, int w, int h){
		this.xPos = x;
		this.yPos = y;
		this.width = w;
		this.height = h;
		this.image = ImageFinder.getImage(msg +".png");
	}
	
	//at a position with a specific color
	public Clickable(String msg, int x, int y, int w, int h, Color c){
		this.xPos = x;
		this.yPos = y;
		this.width = w;
		this.height = h;
		this.image = ImageFinder.getImage(msg);
	}
	
	//accessors********************************************************************************
	public int getX(){
		return xPos;
	}
	
	public int getY(){
		return yPos;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	//Mutator for setting
	public void setMessage(String s){
		image = ImageFinder.getImage(s);
	}

	//the drawing method*******************************************************************************
	public void draw(Graphics g2d){
		g2d.drawImage(image, xPos - width/2, yPos - height/2, width, height, Expansion.panel);
	}
	
}
