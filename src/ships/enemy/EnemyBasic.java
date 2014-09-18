package ships.enemy;

import images.ImageFinder;

import java.awt.Graphics;

import main.Expansion;
import ships.Ship;


public class EnemyBasic extends Ship {
	
	private int wHitBox, hHitBox;

	public EnemyBasic(){
		super(8, 100, 1000, 20);
		
		this.type = "EnemyBasic";
		this.image = ImageFinder.getImage(type +".png");
		this.wHitBox = 60;
		this.hHitBox = 60;
	}
	
	public EnemyBasic(int x, int y){
		super(8, 100, 1000, 20);

		this.type = "EnemyBasic";
		this.image = ImageFinder.getImage(type +".png");
		this.xPos = x; 
		this.yPos = y;
		this.wHitBox = 60;
		this.hHitBox = 60;
	}
	
	public void draw(Graphics g2d)
	{
		g2d.drawImage(image, xPos - width/2, yPos - height/2, width, height, Expansion.panel);
		drawMenu(g2d);
	}
	
	public void setDest(int x, int y){
		xDest = xPos;
		yDest = yPos;
	}
	
	public void setDestMove(int x, int y){
		xDest += 0;
		yDest += 0;
	}
	
	public void setIsMoving(boolean b){
		isMoving = false;
	}
	
	public int getwHitBox(){
		return wHitBox;
	}
	
	public int gethHitBox(){
		return hHitBox;
	}	
}