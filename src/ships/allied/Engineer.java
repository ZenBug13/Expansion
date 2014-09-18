package ships.allied;

import java.awt.Color;
import java.awt.Graphics;

import main.Expansion;
import images.ImageFinder;
import ships.Ship;

public class Engineer extends Ship{
	public Engineer(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 28, 28, 10, 80, 600, 1);
		this.peopleOnboard = people;
		this.type = "Engineer";
		this.image = ImageFinder.getImage(type +".png");
	}
	
	//overriding draw method
	public void draw(Graphics g2d){
		g2d.drawImage(image, xPos - width/2, yPos - height/2, width, height, Expansion.panel);
		g2d.setColor(Color.white);
		g2d.drawOval(xPos - 20*attackRange/2, yPos - 20*attackRange/2, 20*attackRange, 20*attackRange);
		drawMenu(g2d);
		if (xDest != xPos || yDest != yPos){
			drawDestination(g2d);
		}
	}
}