package planets;

import images.ImageFinder;
import java.awt.Graphics;
import java.awt.Image;
import main.Expansion;

public class TechPlanet extends Planet{

	private Image gears = ImageFinder.getImage("Gears.png");
	private int orbitRadius = radius*2;
	private boolean isAcademyBought, isEMPTechBought, isMolecularFusionBought;
	
	public TechPlanet(Planet p){
		super(p, "MiningPlanet");
		this.populationCurrent = 40;
		this.toBeMade = 0;
		this.buildTime = 0;
		this.isAcademyBought = false;
		this.isEMPTechBought = false;
		this.isMolecularFusionBought = false;
	}
	
	public void draw(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos - radius, yPos - radius, radius*2, radius*2);
		g2d.drawOval(xPos - orbitRadius, yPos - orbitRadius, orbitRadius*2, orbitRadius*2);
		g2d.drawImage(gears, xPos - radius/3 - 8, yPos - radius/4 - 8, 16, 16, Expansion.panel);
		g2d.drawImage(gears, xPos + radius/3 + 8, yPos + radius/4 - 8, -16, 16, Expansion.panel);
	}	

	//Draws the little guy
	public void drawOnMap(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos / (MAP_OBJ_SIZE*5) + 148 - radius/10, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/10, radius/5, radius/5);
		g2d.drawOval(xPos / (MAP_OBJ_SIZE*5) + 148 - orbitRadius/14, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - orbitRadius/14, orbitRadius/7, orbitRadius/7);
	}
	
	//accessor
	public boolean getIsAcademyBought(){
		return isAcademyBought;
	}
	
	public boolean getIsEMPTechBought(){
		return isEMPTechBought;
	}
	
	public boolean getIsMolecularFusionBought(){
		return isMolecularFusionBought;
	}
	
	//mutator
	public void setIsAcademyBought(){
		isAcademyBought = true;
	}
	
	public void setIsEMPTechBought(){
		isEMPTechBought = true;
	}
	
	public void setIsMolecularFusionBought(){
		isMolecularFusionBought = true;
	}
}
