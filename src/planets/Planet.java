package planets;

//Planet object

import java.awt.Color;
import java.util.Random;
import java.awt.*;

import main.Expansion;

public class Planet{
	//generator
	private Random gen = new Random();
	//planet shutff
	protected int xPos, yPos, radius;
	protected Color color;
	protected String type, name;
	protected String[] traits = {"", "", ""};
	protected final int MAP_OBJ_SIZE = 7;
	protected int toBeMade, buildTime;

	//resources
	//materials -> produce gold
	protected int materialsMin;
	protected int materialsMax;
	protected int materialsActual;
	//people -> allow ships to be built
	protected int populationCurrent;
	protected int populationMax;

	//radius of planets
	protected final int MAX_RADIUS = 100;
	protected final int MIN_RADIUS = 30;

	//for various things, the Color specifically
	private int rand;

	//consttructor
	public Planet(){
		type = "Generic Planet";
		name = "Generic Name - FIX";

		//planet stats
		xPos = gen.nextInt(Expansion.edge) - Expansion.edge/2;
		yPos = gen.nextInt(Expansion.edge) - Expansion.edge/2;
		radius = gen.nextInt(MAX_RADIUS) + MIN_RADIUS;

		//materials
		materialsActual = gen.nextInt(radius * 100);
		materialsMin = materialsActual - gen.nextInt(800) - 200;
		materialsMax = materialsActual + gen.nextInt(800) + 200;
		
		//people
		populationCurrent = 0;
		populationMax = radius*2;

		//so no negative values will be displayed
		if (materialsMin < 0 ){
			materialsMin = 0;
		}
		
		//sets traits
		for (int i=0; i<traits.length; i++){
			int r = gen.nextInt(10);
			switch (r){
			case 0:
				if (!traits[0].equals("Desert Wasteland") && !traits[1].equals("Desert Wasteland")){
					traits[i] = "Desert Wasteland";
				} else {
					traits[i] = "";
				}
				break;
			case 1:
				if (!traits[0].equals("Rocky") && !traits[1].equals("Rocky")){
					traits[i] = "Rocky";
				} else {
					traits[i] = "";
				}
				break;
			case 2:
				if (!traits[0].equals("Cliffs") && !traits[1].equals("Cliffs")){
					traits[i] = "Cliffs";
				} else {
					traits[i] = "";
				}
				break;
			case 3:
				if (!traits[0].equals("Traces of Microbes Found") && !traits[1].equals("Traces of Microbes Found")){
					traits[i] = "Traces of Microbes Found";
				} else {
					traits[i] = "";
				}
				break;
			case 4:
				if (!traits[0].equals("Remnants of an Ancient Civilization") && !traits[1].equals("Remnants of an Ancient Civilization")){
					traits[i] = "Remnants of an Ancient Civilization";
				} else {
					traits[i] = "";
				}
				break;
			default:
				traits[i] = "";
				break;
			}
		}

		//Color being selected
		rand = gen.nextInt(7);
		switch (rand){
			case 0:
				color = Color.pink;
				break;
			case 1:
				color = Color.magenta;
				break;
			case 2:
				color = Color.blue;
				break;
			case 3:
				color = Color.cyan;
				break;
			case 4:
				color = Color.yellow;
				break;
			case 5:
				color = Color.green;
				break;
			case 6:
				color = Color.gray;
				break;
			default:
				color = Color.white;
		}
	}

	//Constructor based on existing planet p, used for subclasses
	public Planet(Planet p, String t){
		//planet shutff
		xPos =  p.xPos;
		yPos = p.yPos;
		radius = p.radius;
		color = p.color;
		type = t;

		//resources
		//materials -> produce gold
		materialsMin = p.materialsMin;
		materialsMax = p.materialsMax;
		materialsActual = p.materialsActual;

		//people
		populationCurrent = 40;
		populationMax = radius*2;
		
		//sets traits
		for (int i=0; i<traits.length; i++){
			traits[i] = p.traits[i];
		}
	}
	
	public Planet(int x, int y){
		this();
		xPos = x;
		yPos = y;
	}
	
	//*************************************************************************************************************************************
	//methods
	//**************************************************************************************************************************************

	//Actually draws the planet
	public void draw(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos - radius, yPos - radius, radius*2, radius*2);
	}

	//Draws the little guy
	public void drawOnMap(Graphics g2d){
		//Planet
		g2d.setColor(color);
		g2d.fillOval(xPos / (MAP_OBJ_SIZE*5) + 148 - radius/10, yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 - radius/10, radius/5, radius/5);
	}
	
	//mutator**********************************************************************************************************************
	public void setNewLocation(){
		xPos = gen.nextInt(Expansion.edge) - Expansion.edge/2; 
		yPos = gen.nextInt(Expansion.edge) - Expansion.edge/2;
	}
	
	public void takematerials(){
		materialsActual--;
	}

	//Some accessors****************************************************************************************************************
	public int getX (){
		return xPos;
	}

	public int getY (){
		return yPos;
	}

	public int getRadius (){
		return radius;
	}

	public Color getColor(){
		return color;
	}
	
	public int getMaterials(){
		return materialsActual;
	}

	public String getTrait(int n){
		if (n < traits.length){
			return traits[n];
		} else {
			return "";
		}
	}
	
	public int getPopulation(){
		return populationCurrent;
	}
	
	//finds the x coordinate of the planet on the map
	public boolean calcIsXOnMap(){
		if (xPos / (MAP_OBJ_SIZE*5) + 148 < 332){
			return true;
		} else {
			return false;
		}
	}
	
	//finds y coordinate of the planet on the map
	public boolean calcIsYOnMap(){
		if (yPos / (MAP_OBJ_SIZE*5) + Expansion.panel.getHeight() - 130 > Expansion.panel.getHeight() - 226){
			return true;
		} else {
			return false;
		}
	}
	
	//**************************************************************************************************************************
	//for when screen moves
	public void move(int x, int y){
		xPos += x;
		yPos += y;
	}
	
	//returns true if possible
	public boolean addPeople(int x){
		if (populationCurrent + x <= populationMax){
			populationCurrent += x;
			return true;
		} else {
			populationCurrent = populationMax;
		}
		return false;
	}
	
	public void addPeople(int x, boolean b){
		populationCurrent += x;
		populationMax += x;
	}
	
	public boolean decMaterials(int x){
		if (materialsActual - x >= 0){
			materialsActual -= x;
			return true;
		} else {
			materialsActual = 0;
		}
		return false;
	}
	
	public void takePeople(int x){
		populationCurrent -= x;
	}
	
	//for building and training stuff
	public int getToBeMade(){
		return toBeMade;
	}
	
	public int getBuildTime(){
		return buildTime;
	}
	
	//mutators
	public void setToBeMade(int buildType, int buildTime){
		this.toBeMade = buildType;
		this.buildTime = buildTime;
	}
	
	public void decBuildTime(){
		if (buildTime > 0){
			buildTime--;
		}
	}

	//Various toStrings for use when drawing hover menu************************************************************************
	public String toStringradius(){
		return ("Planet radius: " + radius);
	}

	public String toStringmaterials(){
		return ("" + materialsMin + " ~ " + materialsMax);

	}
}