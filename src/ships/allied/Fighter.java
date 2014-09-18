package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Fighter extends Ship{
	public Fighter(int x, int y, int damageMod, int people){
		//wid height range health move dmg
		super(x, y, 25, 25, 15, 170, 750, 15 + damageMod);
		this.peopleOnboard = people;
		this.type = "Fighter";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
