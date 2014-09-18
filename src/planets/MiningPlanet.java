package planets;

import images.ImageFinder;
import java.awt.Graphics;
import java.awt.Image;
import main.Expansion;

public class MiningPlanet extends Planet{

	private Image crane = ImageFinder.getImage("Crane.png");
	private int orbitRadius = radius*2;
	private int populationGrowth = 2, materialsRate = 2;
	
	//constructor
	public MiningPlanet(Planet p){
		super(p, "MiningPlanet");
		this.populationCurrent = 40;
		this.toBeMade = 0;
		this.buildTime = 0;
	}
	
	public void draw(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos - radius, yPos - radius, radius*2, radius*2);
		g2d.drawOval(xPos - orbitRadius, yPos - orbitRadius, orbitRadius*2, orbitRadius*2);
		g2d.drawImage(crane, xPos - radius/3 - 8, yPos - radius/4 - 8, 16, 16, Expansion.panel);
		g2d.drawImage(crane, xPos + radius/3 + 8, yPos + radius/4 - 8, -16, 16, Expansion.panel);
	}	

	//Draws the little guy
	public void drawOnMap(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos / (MAP_OBJ_SIZE*5) + 148 - radius/10, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/10, radius/5, radius/5);
		g2d.drawOval(xPos / (MAP_OBJ_SIZE*5) + 148 - orbitRadius/14, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - orbitRadius/14, orbitRadius/7, orbitRadius/7);
	}
	
	public void growPopulation(){
		if (populationCurrent + populationGrowth < populationMax){
			populationCurrent += populationGrowth;
		} else {
			populationCurrent = populationMax;
		}
	}

	public boolean harvestMaterials(){
		if (materialsActual - materialsRate >= 0){
			materialsActual -= materialsRate;
			return true;
		} else {
			materialsActual = 0;
		}
		return false;
	}
	
	public int getMaterialsRate(){
		return materialsRate;
	}
}
