package main;
//Panel
/*
>>>>>NEEDS CLEANED UP
>>>>>FUNCTIONS NEED TO BE MADE
>>>>>POSSIBLY MOVE STUFF OUT TO MAIN OR OTHER CLASS
*/

import main.Expansion;
import planets.*;
import ships.Ship;
import images.ImageFinder;
import java.awt.*;
import javax.swing.*;

public class Panel extends JPanel
{

	//***********************************variables***************************************************************
	private Image background = ImageFinder.getImage("Title.png"), mapImage = ImageFinder.getImage("Map.png"), mapLinesImage = ImageFinder.getImage("MapLines.png"),
			resourcesImage = ImageFinder.getImage("Resources.png"), objBack = ImageFinder.getImage("ObjectiveBackground.png");
	private boolean isDrawingSelectionBox = false, isDrawingShipsFighting = false;
	private Image ship1, ship2;
	private String ship1Type, ship2Type;
	private int xSelectionStart, ySelectionStart, cursorType = 0, ship1Size, ship2Size;
	
	//constructor
	public Panel(Color backColor){
		setBackground(backColor);
	}
	
	//*******************************************************************************************************************************
	//setting stuff
	//*******************************************************************************************************************************
	
	//sets the background to given path
	public void setBackground(String s){
		background = ImageFinder.getImage(s +".png"); 
	}
	
	public void setDrawingSelectionBox(boolean b){
		isDrawingSelectionBox = b;
	}

	public void setDrawingSelectionBox(boolean b, int x, int y){
		isDrawingSelectionBox = b;
		xSelectionStart = x;
		ySelectionStart = y;
	}
	
	public void setCursorType(int n){
		cursorType = n;
	}
	
	public void setShipsFighting(boolean b, Ship s1, Ship s2){
		isDrawingShipsFighting = b;
		ship1 = ImageFinder.getImage(s1.getType() +".png"); ship2 = ImageFinder.getImage(s2.getType() +".png");
		ship1Size = s1.getWidth(); ship2Size = s2.getWidth();
		ship1Type = s1.getType(); ship2Type = s2.getType();
	}

	//*******************************************************************************************************************************
	//drawings
	//*******************************************************************************************************************************
	
	//draws all the things
	public void drawing()
	{
		repaint();
	}
	
	private void drawSelectionBox(Graphics g2d){
		if (isDrawingSelectionBox){
			g2d.setColor(Color.white);
			if((xSelectionStart < Expansion.xMouse) && (ySelectionStart > Expansion.yMouse)){
				g2d.drawRect(xSelectionStart, Expansion.yMouse, Expansion.xMouse - xSelectionStart, ySelectionStart - Expansion.yMouse);
			} else if ((xSelectionStart < Expansion.xMouse) && (ySelectionStart < Expansion.yMouse)){
				g2d.drawRect(xSelectionStart, ySelectionStart, Expansion.xMouse - xSelectionStart, Expansion.yMouse - ySelectionStart);
			} else if ((xSelectionStart > Expansion.xMouse) && (ySelectionStart < Expansion.yMouse)){
				g2d.drawRect(Expansion.xMouse, ySelectionStart, xSelectionStart - Expansion.xMouse, Expansion.yMouse - ySelectionStart);
			} else if ((xSelectionStart > Expansion.xMouse) && (ySelectionStart > Expansion.yMouse)){
				g2d.drawRect(Expansion.xMouse, Expansion.yMouse, xSelectionStart - Expansion.xMouse, ySelectionStart - Expansion.yMouse);
			} 
		}
	}

	//Drawing
	public void paintComponent(Graphics g)
	{
		//graphics start up
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		//background
		g2d.drawImage(background, 0, 0, Expansion.panel.getWidth(), Expansion.panel.getHeight(), Expansion.panel);
		
		//drawing planets
		for (int i=0; i<Expansion.planets.size(); i++){
			if (Expansion.planets.get(i) != null){
				if (Math.abs(Expansion.planets.get(i).getX()) < Expansion.panel.getWidth() + 500){
					if (Math.abs(Expansion.planets.get(i).getY()) < Expansion.panel.getHeight() + 500){
						Expansion.planets.get(i).draw(g2d);
					}
				}
			}
		}
		
		//drawing ships
		for (int i=0; i<Expansion.ships.size(); i++){
			if (Expansion.ships.get(i) != null){
				Expansion.ships.get(i).draw(g2d);
			}
		}
		
		//drawing selection box
		drawSelectionBox(g2d);
		
		//drawing clickables
		if (Expansion.clickables[0] != null)
		for (int i=0; i<Expansion.clickables.length; i++){
			if (Expansion.clickables[i] != null){
				Expansion.clickables[i].draw(g2d);
			}
		}
		
		//map
		//drawing map, resources, and planets on map
		if (Expansion.stage == 0 || Expansion.stage == 2){
			g2d.drawImage(objBack, 339, 701, 552, 99, Expansion.panel);
			g2d.drawImage(mapImage, 0, Expansion.panel.getHeight() - 249, 350, 250, Expansion.panel);
			g2d.drawImage(resourcesImage, Expansion.panel.getWidth() - 350, Expansion.panel.getHeight() - 100, 350, 100, Expansion.panel);
			for (int i=0; i<Expansion.planets.size(); i++){
				if (Expansion.planets.get(i) != null)
				if (Expansion.planets.get(i).calcIsXOnMap() && Expansion.planets.get(i).calcIsYOnMap()){
					Expansion.planets.get(i).drawOnMap(g2d);
				}
			}
			g2d.drawImage(mapLinesImage, 0, Expansion.panel.getHeight() - 249, 350, 250, Expansion.panel);
			g2d.setColor(Color.white);
			g2d.setFont(new Font("Arial", Font.PLAIN, 16));
			g2d.drawString("Materials: " +Expansion.materials, Expansion.panel.getWidth() - 320, Expansion.panel.getHeight() - 70);
			g2d.drawString("People: " +Expansion.people, Expansion.panel.getWidth() - 320, Expansion.panel.getHeight() - 50);
			g2d.drawString("Knowledge: " +Expansion.knowledge, Expansion.panel.getWidth() - 320, Expansion.panel.getHeight() - 30);
			g2d.drawString("Turn Number: " +Expansion.turnNumber, Expansion.panel.getWidth() - 320, Expansion.panel.getHeight() - 10);
			//drawing planet details on the menu
		} else if (Expansion.stage == 3){
			g2d.setColor(Expansion.planets.get(Expansion.indexSelected_planet).getColor());
			g2d.fillOval(Expansion.panel.getWidth()/8 + 190 - Expansion.planets.get(Expansion.indexSelected_planet).getRadius()/2, Expansion.panel.getHeight()/8 + 295 - Expansion.planets.get(Expansion.indexSelected_planet).getRadius()/2,
					Expansion.planets.get(Expansion.indexSelected_planet).getRadius(), Expansion.planets.get(Expansion.indexSelected_planet).getRadius());
			g2d.setColor(Color.white);
			g2d.setFont(new Font("Arial", Font.PLAIN, 25));
			g2d.drawString("Planet Diameter: " +98*Expansion.planets.get(Expansion.indexSelected_planet).getRadius() +" Kilometers", 7*Expansion.panel.getWidth()/16, 23*Expansion.panel.getHeight()/64);
			if (Expansion.planets.get(Expansion.indexSelected_planet) instanceof HomePlanet || Expansion.planets.get(Expansion.indexSelected_planet) instanceof AttackPlanet ||
					Expansion.planets.get(Expansion.indexSelected_planet) instanceof MiningPlanet || Expansion.planets.get(Expansion.indexSelected_planet) instanceof TechPlanet){
				g2d.drawString("Planet Iron: " +Expansion.planets.get(Expansion.indexSelected_planet).getMaterials() +" Kilograms", 7*Expansion.panel.getWidth()/16, 26*Expansion.panel.getHeight()/64);
				g2d.drawString("Population: " +Expansion.planets.get(Expansion.indexSelected_planet).getPopulation(), 7*Expansion.panel.getWidth()/16, 31*Expansion.panel.getHeight()/64);
				switch(Expansion.planets.get(Expansion.indexSelected_planet).getToBeMade()){
				case 0:
					g2d.drawString("No Current Builds",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					break;
				case 1:
					g2d.drawString("Building: Expander",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 2:
					g2d.drawString("Building: Scout",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 3:
					g2d.drawString("Building: Defender",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 4:
					g2d.drawString("Building: Destroyer",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 5:
					g2d.drawString("Building: Town",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 6:
					g2d.drawString("Building: Mine",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 7:
					g2d.drawString("Building: Academy",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 8:
					g2d.drawString("Building: Quantum Tech",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 9:
					g2d.drawString("Building: Turret",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 10:
					g2d.drawString("Building: Radar Tech",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 11:
					g2d.drawString("Building: War Factory",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 12:
					g2d.drawString("Building: Plasma Tech",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 13:
					g2d.drawString("Building: Fighter",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 14:
					g2d.drawString("Building: Tank",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 15:
					g2d.drawString("Building: Disabler",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 16:
					g2d.drawString("Building: Excavate",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 17:
					g2d.drawString("Building: Clone",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 18:
					g2d.drawString("Building: Harvester",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 19:
					g2d.drawString("Building: Engineer",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 20:
					g2d.drawString("Building: Carrier",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 21:
					g2d.drawString("Building: EMP Tech",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 22:
					g2d.drawString("Building: Molecular Fusion",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				case 23:
					g2d.drawString("Building: Skid",  7*Expansion.panel.getWidth()/16, 34*Expansion.panel.getHeight()/64);
					g2d.drawString("Build Time: " +Expansion.planets.get(Expansion.indexSelected_planet).getBuildTime() +" Turn(s)",  7*Expansion.panel.getWidth()/16, 37*Expansion.panel.getHeight()/64);
					break;
				}
			} else {
				g2d.drawString("Planet Iron: " +Expansion.planets.get(Expansion.indexSelected_planet).toStringmaterials() +" Kilograms", 7*Expansion.panel.getWidth()/16, 26*Expansion.panel.getHeight()/64);
				String[] traits = new String[3];
				int pos = 31;
				for (int i=0; i<traits.length; i++){
					traits[i] = Expansion.planets.get(Expansion.indexSelected_planet).getTrait(i);
					if (!traits[i].equals("")){
						g2d.drawString("Trait: " +traits[i], 7*Expansion.panel.getWidth()/16, pos*Expansion.panel.getHeight()/64);
						pos+=3;
					}
				}
			}
			
			for (int i=0; i<Expansion.menuClickables.length; i++){
				if (Expansion.menuClickables[i] != null){
					Expansion.menuClickables[i].draw(g2d);
				}
			}
		} else if (Expansion.stage == 4){
			int index = 0;
			g2d.setColor(Color.white);
			g2d.setFont(new Font("Arial", Font.PLAIN, 25));
			while (index < Expansion.news.size()){
				g2d.drawString(Expansion.news.get(index), 3*Expansion.panel.getWidth()/16, 23*Expansion.panel.getHeight()/64 + index*40);
				index++;
			}
		} else if (Expansion.stage == 5){
			g2d.drawImage(ship1, 5*Expansion.panel.getWidth()/16, 3*Expansion.panel.getHeight()/8 + 50, 2*ship1Size, -2*ship1Size, Expansion.panel);
			g2d.drawImage(ship2, 5*Expansion.panel.getWidth()/16, 6*Expansion.panel.getHeight()/8 - 50, 2*ship2Size, 2*ship2Size, Expansion.panel);
			g2d.setColor(Color.white);
			g2d.drawString(ship1Type, 5*Expansion.panel.getWidth()/16 - 20, 3*Expansion.panel.getHeight()/8 - 20);
			g2d.drawString(ship2Type, 5*Expansion.panel.getWidth()/16 - 20, 6*Expansion.panel.getHeight()/8 + 20);
		}
		
		if (Expansion.stage == 0 || Expansion.stage == 2){
			g2d.setFont(new Font("Arial", Font.PLAIN, 18));
			switch (Expansion.stage_Objective){
			case 0:
				break;
			case 1:
				g2d.drawString("Let There Be Life!", 360, 740);
				g2d.drawString("Colonize a home planet", 360, 770);
				break;
			case 2:
				g2d.drawString("Go to Town!", 360, 740);
				g2d.drawString("Build a town", 360, 770);
				break;
			case 3:
				g2d.drawString("What is This, Minecraft?", 360, 740);
				g2d.drawString("Colonize a mining planet", 360, 770);
				break;
			case 4:
				g2d.drawString("Into the Abyss!", 360, 740);
				g2d.drawString("Train a scout", 360, 770);
				break;
			case 5:
				g2d.drawString("Standby For Ship-Fall!", 360, 740);
				g2d.drawString("Colonize an attack planet", 360, 770);
				break;
			case 6:
				g2d.drawString("Winter Is Coming!", 360, 740);
				g2d.drawString("Train 2 fighters", 360, 770);
				break;
			case 7:
				g2d.drawString("It's a space jam!", 360, 740);
				g2d.drawString("Kill 5 enemies", 360, 770);
				break;
			case 8:
				g2d.drawString("Set Phasers to Stun!", 360, 740);
				g2d.drawString("Colonize a tech planet", 360, 770);
				break;
			case 9:
				g2d.drawString("Requiem Inbound!", 360, 740);
				g2d.drawString("Train a destroyer", 360, 770);
				break;
			case 10:
				g2d.drawString("Ender's Game!", 360, 740);
				g2d.drawString("Destroy a planet", 360, 770);
				break;
			case 11:
				g2d.drawString("Continue... or naa?", 360, 740);
				g2d.drawString("Conquer the Universe", 360, 770);				
				break;
			}
		} else if (Expansion.stage == 10){
			g2d.setFont(new Font("Arial", Font.PLAIN, 30));
			g2d.setColor(Color.white);
			switch (Expansion.difficulty){
			case 0:
				g2d.drawString("Easy", 750, 325);
				break;
			case 1:
				g2d.drawString("Medium", 750, 325);
				break;
			case 2:
				g2d.drawString("Hard", 750, 325);
				break;
			}
			switch(Expansion.edge){
			case 25000:
				g2d.drawString("Small", 750, 525);
				break;
			case 30000:
				g2d.drawString("Large", 750, 525);
				break;
			}
		}
		
		//Draw actual cursor
		if (cursorType == 0){
			g2d.setColor(new Color(200, 200, 130));
		} else if (cursorType == 1){
			g2d.setColor(new Color(0, 130, 30));
		} else if (cursorType == 2){
			g2d.setColor(Color.red);
		}
		g2d.drawLine(Expansion.xMouse - 5, Expansion.yMouse, Expansion.xMouse + 5, Expansion.yMouse);
		g2d.drawLine(Expansion.xMouse, Expansion.yMouse + 5, Expansion.xMouse, Expansion.yMouse - 5);
	}
}