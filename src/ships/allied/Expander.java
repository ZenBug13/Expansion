package ships.allied;

import images.ImageFinder;
import ships.Ship;

public class Expander extends Ship{
	
	public Expander(int x, int y, int people){
		//wid height range health move dmg
		super(x, y, 20, 20, 10, 100, 600, 10);
		this.peopleOnboard = people;
		this.type = "Expander";
		this.image = ImageFinder.getImage(type +".png");
	}
	
}
