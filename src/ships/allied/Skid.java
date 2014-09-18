package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Skid extends Ship{
	
	public Skid(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 15, 15, 16, 50, 900, 8);
		this.peopleOnboard = people;
		this.type = "Skid";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
