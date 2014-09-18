package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Defender extends Ship{
	
	public Defender(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 30, 30, 10, 200, 500, 12);
		this.peopleOnboard = people;
		this.type = "Defender";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
