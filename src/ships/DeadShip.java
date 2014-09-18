package ships;

import java.awt.Color;
import java.awt.Graphics;

import main.Expansion;
import images.ImageFinder;
import ships.Ship;

public class DeadShip extends Ship{
	public DeadShip(int x, int y){
		//wid height range health move dmg
		super(x, y, 16, 16, 0, 0, 0, 0);
		this.peopleOnboard = 0;
		this.isSelected = false;
		this.isMoving = false;
		this.type = "DeadShip";
		this.image = ImageFinder.getImage(type +".png");
	}
	
	public void setIsSelected(boolean b){
		//do nothing
	}
	
	protected void drawMenu(Graphics g2d){
		//do nothing
	}
}