package ships;

import images.ImageFinder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import main.Expansion;

public class Ship {

	protected boolean isSelected, isMoving;
	protected int xPos, yPos, width, height, xDelta, yDelta,
				attackRange, movementRange, health, maxHealth, damage,
				xDest, yDest, widthColBox, heightColBox,
				peopleOnboard;
	protected String type;
	protected Image image = null;
	
	//default constr
	public Ship(){
		this.isSelected = false;
		this.xPos = 100;
		this.yPos = 100;
		this.xDelta = xPos;
		this.yDelta = yPos;
		this.width = 16;
		this.height = 16;
		this.xDest = xPos;
		this.yDest = yPos;
		this.attackRange = 10;
		this.maxHealth = 100;
		this.health = maxHealth;
		this.movementRange = 600;
		this.damage = 10;
		this.peopleOnboard = 0;
		this.widthColBox = 30;
		this.heightColBox = 30;
		this.type = "GenericShip";
		this.image = ImageFinder.getImage(type +".png");
	}
	
	//position constr
	public Ship(int x, int y){
		this();
		this.xPos = x;
		this.yPos = y;
		this.xDelta = xPos;
		this.yDelta = yPos;
	}
	
	//type constr
	public Ship(int range, int health, int movement, int damage){
		this();
		this.xDest = xPos;
		this.yDest = yPos;
		this.attackRange = range;
		this.maxHealth = health;
		this.health = maxHealth;
		this.movementRange = movement;
		this.damage = damage;
	}

	//position and type constr
	public Ship(int x, int y, int range, int health, int movement, int damage){
		this();
		this.xPos = x;
		this.yPos = y;
		this.xDelta = xPos;
		this.yDelta = yPos;
		this.xDest = xPos;
		this.yDest = yPos;
		this.attackRange = range;
		this.maxHealth = health;
		this.health = maxHealth;
		this.movementRange = movement;
		this.damage = damage;
	}
	

	//position size and type constr
	public Ship(int x, int y, int width, int height, int range, int health, int movement, int damage){
		this();
		this.xPos = x;
		this.yPos = y;
		this.xDelta = xPos;
		this.yDelta = yPos;
		this.width = width;
		this.height = height;
		this.xDest = xPos;
		this.yDest = yPos;
		this.attackRange = range;
		this.maxHealth = health;
		this.health = maxHealth;
		this.movementRange = movement;
		this.damage = damage;
	}
	
	public void draw(Graphics g2d){
		g2d.drawImage(image, xPos - width/2, yPos - height/2, width, height, Expansion.panel);
		drawMenu(g2d);
		if (xDest != xPos || yDest != yPos){
			drawDestination(g2d);
		}
	}
	
	protected void drawMenu(Graphics g2d){
		//Hover menu
		g2d.setColor(Color.white);
		if((Expansion.rectPointCollision(xPos - width/2, yPos - height/2, width, height, Expansion.getMouseX(), Expansion.getMouseY())) || (isSelected)){
			//Backing box
			g2d.setColor(Color.gray);
			g2d.fillRect(xPos - calcHealthSize()/2 - 1, yPos + 9, calcHealthSize() + 2, 7);

			//Health Bar Backing
			g2d.setColor(Color.red);
			g2d.fillRect(xPos - calcHealthSize()/2, yPos + 10, calcHealthSize(), 5);

			//Health Bar
			g2d.setColor(Color.green);
			g2d.fillRect(xPos - calcHealthSize()/2, yPos + 10, calcHealthBar(), 5);

			//Health Num
			if((double)(health / maxHealth) >= .5)
				g2d.setColor(Color.green);
			else
				g2d.setColor(Color.red);

			g2d.drawString(toStringHealth(), xPos - 10, yPos + 30);
		}
	}
	
	protected void drawDestination(Graphics g2d){
		g2d.setColor(new Color(0, 130, 30));
		g2d.drawLine(xDest - 5, yDest, xDest + 5, yDest);
		g2d.drawLine(xDest, yDest - 5, xDest, yDest + 5);
		g2d.drawLine(xPos, yPos, xDest, yDest);
	}
	

	//For health popup
	protected int calcHealthSize(){
		return maxHealth / 10 * 3;
	}

	protected int calcHealthBar(){
		return health / 10 * 3;
	}

	protected String toStringHealth(){
		return ("" + health);
	}
	
	public void setHealth(int hp){
		health = hp;
	}
	
	public void setDest(int x, int y){
		xDest = x;
		yDest = y;
		xDelta = xPos;
		yDelta = yPos;
	}
	
	public void setIsSelected(boolean b){
		isSelected = b;
	}
	
	public void setIsMoving(boolean b){
		isMoving = b;
	}

	//Moves ships
	public void move(){
		if (xPos < xDest){
			xPos += 1;
		} else if (xPos > xDest){
			xPos -= 1;
		}
		if (yPos < yDest){
			yPos += 1;
		} else if (yPos > yDest){
			yPos -= 1;
		}
		if (xPos == xDest && yPos == yDest){
			isMoving = false;
		}
	}
	
	//moves ships with screen
	public void move(int x, int y){
		xPos += x;
		yPos += y;
		xDest += x;
		yDest += y;
		xDelta = xPos;
		yDelta = yPos;
	}
	
	public void moveBack(){
		xPos = xDelta;
		yPos = yDelta;
		xDest = xPos;
		yDest = yPos;
		isMoving = false;
	}
	
	public void addHealth(int hp){
		if (health + hp < maxHealth){
			health += hp;
		} else {
			health = maxHealth;
		}
	}

	//Accessors
	public int getX(){
		return xPos;
	}

	public int getY(){
		return yPos;
	}
	
	public String getType(){
		return type;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getDestX(){
		return xDest;
	}
	
	public int getDestY(){
		return yDest;
	}

	public int getMovement(){
		return movementRange;
	}
	
	public int getRange(){
		return attackRange;
	}
	
	public int getHealth(){
		return health;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public int getPeopleOn(){
		return peopleOnboard;
	}

	public void select(Boolean s){
		isSelected = s;
	}

	public boolean getIsSelected(){
		return isSelected;
	}
	
	public boolean getIsMoving(){
		return isMoving;
	}
	
	public boolean collidesWith(int x, int y){
		if (Expansion.rectPointCollision(xPos - widthColBox/2, yPos - heightColBox/2, widthColBox, heightColBox, x, y)){
			return true;
		} else {
			return false;
		}
	}
}
;