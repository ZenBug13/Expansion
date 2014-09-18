package planets;

import images.ImageFinder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import main.Expansion;

public class HomePlanet extends Planet{

	private Image satellite = ImageFinder.getImage("satellite.png");
	private int orbitRadius = radius*3;
	private Image house = null;
	
	public HomePlanet(Planet p){
		super(p, "HomePlanet");
		house = ImageFinder.getImage("House.png");
		this.populationCurrent = 100;
		this.toBeMade = 0;
		this.buildTime = 0;
	}
	
	public void draw(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos - radius, yPos - radius, radius*2, radius*2);
		g2d.setColor(Color.white);
		g2d.drawOval(xPos - orbitRadius, yPos - orbitRadius, orbitRadius*2, orbitRadius*2);
		g2d.drawImage(house, xPos - 16, yPos - 16, 32, 32, Expansion.panel);
	}
	
	//Draws the little guy
	public void drawOnMap(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos / (MAP_OBJ_SIZE*5) + 148 - radius/10, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/10, radius/5, radius/5);
		g2d.setColor(Color.white);
		g2d.drawRect(xPos / (MAP_OBJ_SIZE*5) + 148 - radius/8, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/8, radius/4, radius/4);
		int[] xPts = {xPos / (MAP_OBJ_SIZE*5) + 148 - radius/8, xPos / (MAP_OBJ_SIZE*5) + 148, xPos / (MAP_OBJ_SIZE*5) + 148 + radius/8},
				yPts = {yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/8, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/4, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/8};
		g2d.drawPolygon(xPts, yPts, 3);
	}
	
	//accessors
	public int getOrbitRadius(){
		return orbitRadius;
	}
}
