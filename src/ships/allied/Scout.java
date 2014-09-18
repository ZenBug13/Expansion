package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Scout extends Ship{
	
	public Scout(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 18, 18, 12, 60, 1000, 6);
		this.peopleOnboard = people;
		this.type = "Scout";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
