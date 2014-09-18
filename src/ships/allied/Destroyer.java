package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Destroyer extends Ship{
	
	public Destroyer(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 50, 50, 8, 350, 500, 2);
		this.peopleOnboard = people;
		this.type = "Destroyer";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
