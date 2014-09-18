package planets;

import images.ImageFinder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import main.Expansion;

public class AttackPlanet extends Planet{

	private Image turret = ImageFinder.getImage("Turret.png");
	private int orbitRadius = radius*4;
	private int defenseDamage, defenseHealth, unitDamageModifier;
	private boolean isWarFactoryBought, isRadarTechBought, isPlasmaTechBought;
	
	public AttackPlanet(Planet p){
		super(p, "AttackPlanet");
		this.populationCurrent = 40;
		this.toBeMade = 0;
		this.buildTime = 0;
		this.defenseDamage = 16;
		this.defenseHealth = 300;
		this.unitDamageModifier = 0;
		this.isWarFactoryBought = false;
	}
	
	public void draw(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos - radius, yPos - radius, radius*2, radius*2);
		g2d.drawOval(xPos - orbitRadius, yPos - orbitRadius, orbitRadius*2, orbitRadius*2);
		g2d.drawImage(turret, xPos - radius/3 - 8, yPos - radius/4 - 8, 24, 24, Expansion.panel);
		g2d.drawImage(turret, xPos + radius/3 + 8, yPos + radius/4 - 8, -24, 24, Expansion.panel);
	}	

	//Draws the little guy
	public void drawOnMap(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos / (MAP_OBJ_SIZE*5) + 148 - radius/10, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/10, radius/5, radius/5);
		g2d.drawOval(xPos / (MAP_OBJ_SIZE*5) + 148 - orbitRadius/20, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - orbitRadius/20, orbitRadius/10, orbitRadius/10);
	}
	
	//accessor
	public int getOrbitRadius(){
		return orbitRadius;
	}
	
	public boolean getIsWarFactoryBought(){
		return isWarFactoryBought;
	}
	
	public boolean getIsRadarTechBought(){
		return isRadarTechBought;
	}
	
	public boolean getIsPlasmaTechBought(){
		return isPlasmaTechBought;
	}
	
	public int getDamageModifier(){
		return unitDamageModifier;
	}
	
	//mutator
	public void incOrbitRadius(){
		orbitRadius = radius*5;
		isRadarTechBought = true;
	}
	
	public void incDamageModifier(){
		unitDamageModifier += 5;
		isWarFactoryBought = true;
	}
	
	public void incDefenseDamage(){
		defenseDamage++;
	}
	
	public void incDefenseHealth(){
		defenseHealth += 50;
		isPlasmaTechBought = true;
	}
}
