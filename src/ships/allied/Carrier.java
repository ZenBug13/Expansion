package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Carrier extends Ship{
	public Carrier(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 20, 20, 8, 100, 600, 6);
		this.peopleOnboard = people;
		this.type = "Carrier";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
