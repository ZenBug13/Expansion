package ships.enemy;

import images.ImageFinder;
import ships.Ship;

public class EnemyFighter extends Ship{
	public EnemyFighter(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 25, 25, 16, 180, 700, 15);
		this.peopleOnboard = people;
		this.type = "EnemyFighter";
		this.image = ImageFinder.getImage(type +".png");
		this.isSelected = false;
	}
	
	public void setIsSelected(boolean b){
		//do nothing
	}
	
	public void setDest(int x, int y){
		//do nothing
	}
}
