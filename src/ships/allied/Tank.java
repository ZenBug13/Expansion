package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Tank extends Ship{
	public Tank(int x, int y, int damageMod, int people){
		//wid height range health move dmg
		super(x, y, 40, 40, 15, 200, 500, 16 + damageMod);
		this.peopleOnboard = people;
		this.type = "Tank";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}