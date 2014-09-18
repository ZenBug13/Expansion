package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Disabler extends Ship{
	public Disabler(int x, int y, int damageMod, int people){
		//wid height range health move dmg
		super(x, y, 30, 30, 13, 150, 700, 5 + damageMod);
		this.peopleOnboard = people;
		this.type = "Disabler";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
