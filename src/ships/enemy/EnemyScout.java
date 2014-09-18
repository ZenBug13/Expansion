package ships.enemy;

import images.ImageFinder;
import ships.Ship;

public class EnemyScout extends Ship{
	public EnemyScout(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 24, 24, 12, 70, 900, 7);
		this.peopleOnboard = people;
		this.type = "EnemyScout";
		this.image = ImageFinder.getImage(type +".png");
	}
	
	public void setIsSelected(boolean b){
		//do nothing
	}
	
	public void setDest(int x, int y){
		//do nothign
	}
	
}
