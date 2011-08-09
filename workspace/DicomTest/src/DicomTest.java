import java.util.Random;

import com.pixelmed.dicom.ImageToDicom;

public class DicomTest {
	public static void main(String args[]) throws Exception {
		String[] names = {"bashful","doc","dopey","grumpy","happy","sleepy","sneezy"};
		Random r = new Random();
		for(String name : names) {
			new ImageToDicom(
				"C:/Users/emcy/Desktop/je/pix/amnesya/"+name+".jpg",
				"C:/Users/emcy/Desktop/je/Rx/output/"+name+".dcm",
				name,
				""+r.nextInt(1000),
				""+r.nextInt(1000),
				""+r.nextInt(1000),
				""+r.nextInt(1000));			
			System.out.println(name+" done");
		}
	}
}
