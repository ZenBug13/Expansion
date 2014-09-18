package ships.enemy;

import java.awt.Color;
import java.awt.Graphics;

public class EnemyScout_Moving extends EnemyScout{

	public EnemyScout_Moving(int x, int y, int people){
		super(x, y, people);
	}
	
	protected void drawDestination(Graphics g2d){
		g2d.setColor(Color.red);
		g2d.drawLine(xDest - 5, yDest, xDest + 5, yDest);
		g2d.drawLine(xDest, yDest - 5, xDest, yDest + 5);
		g2d.drawLine(xPos, yPos, xDest, yDest);
	}

	public void setDest(int x, int y){
		xDest = x;
		yDest = y;
		xDelta = xPos;
		yDelta = yPos;
	}
}
