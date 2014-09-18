package images;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class ImageFinder {

	private ImageFinder(){}
	
	public static Image getImage(String path){
		Image tempImage = null;
		try {
			URL imageURL = ImageFinder.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e){
			System.out.println("Image not found");
		}
		return tempImage;
	}
}