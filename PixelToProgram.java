import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

//A single method in a Java class may be at most 64KB of bytecode

public class PixelToProgram {
	
	private static final boolean USERANDOMVALUES = false;
	//How many statements to synthesize when using random values
	private static final int NUMBEROFSTATEMENTS = 100;
	private static JFileChooser fileChooser;
	
	public PixelToProgram() {
	}
	

	public static void main(String[] args) {
		ProgramSynthesis programSynthesis = null;
		// Read image and synthesize program
		if(USERANDOMVALUES){
			programSynthesis = new ProgramSynthesis(NUMBEROFSTATEMENTS);
			
		}else{
			// Only images
			fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Image File", "jpg", "gif", "png");
			fileChooser.setFileFilter(filter);
			
			// Ask for image until one is accepted
			int returnVal = -1;
			returnVal = fileChooser.showOpenDialog(null);
			if(returnVal != JFileChooser.APPROVE_OPTION){
				System.exit(0);
			}

			System.out.println("You chose to open this file: "
					+ fileChooser.getSelectedFile().getName());
			File file = fileChooser.getSelectedFile();
			
			try {
				BufferedImage image = ImageIO.read(file);
				//Uses pixel values
				programSynthesis = new ProgramSynthesis(imageToArray(image));
			} catch (IOException e) {
				System.err.println("File not found!");
			}
		}
		System.out.println("Synthesizing...");
		
		//Test
		//programSynthesis.testChoose(2, 100);
		
		Program program = programSynthesis.synthesize();
		System.out.println("Unparsing...");
		String programOutput = Unparse.unparse(program);
		System.out.println("Saving Program...");
		//System.out.println(programOutput);
		//Write output to file
		try{
			String fileName;
			String className;
			//Get date and time for file name and/or file notes
			DateFormat df = new SimpleDateFormat("MM_dd_yyyy HH_mm_ss");
			Date today = Calendar.getInstance().getTime();        
			String todayDate = df.format(today);
			if(USERANDOMVALUES){
				className = todayDate;
				fileName = className + ".java";
			}else{
				fileName = fileChooser.getSelectedFile().getName();
				fileName = fileName.replace(' ', '_');
				//jpg, png, or gif
				String suffix = fileName.substring((fileName.length() - 3), fileName.length());
				fileName = fileName.substring(0, (fileName.length()-4));
				fileName = fileName + "_" + suffix;
				className = fileName;
				fileName += ".java";
			}
			//Write beginning of program, program, and ending braces
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("/*Elliot K. Goldman\n");
			if(USERANDOMVALUES){
				bw.write(" *Program made from random values");
			}else{
				bw.write(" *Program made from ");
				bw.write(className);
			}
			bw.write(" on ");
			bw.write(todayDate);
			bw.write("\n */\n\n");
			bw.write("public class ");
			bw.write(className);
			bw.write("{\n\n   public static void main(String[] args) {\n");
			bw.write(programOutput);
			bw.write("   }\n}");
			bw.close();
		}catch(IOException e){
			System.err.println(e);
		}
		System.out.println("Saved");
	}
	
	// Read in the pixel values and return them in an int array
	private static int[] imageToArray(BufferedImage image){
		int[] imagePixels = new int[image.getHeight() * image.getWidth()];
		int pixelSlot = 0; // To keep track of place in array
		int pixelValue = 0;
		for (int h = 0; h < image.getHeight(); h++) {
			for (int w = 0; w < image.getWidth(); w++) {
				pixelValue = image.getRGB(w, h);
				// convert TYPE_INT_ARGB into individual colour values
				int red = (pixelValue & 0x00ff0000) >> 16;
				int green = (pixelValue & 0x0000ff00) >> 8;
				int blue = pixelValue & 0x000000ff;
				//The average measures how bright the pixel is 0...255
				int avg = (red + green + blue)/3;
				imagePixels[pixelSlot] = avg;

				pixelSlot++;
			}
		}
		return imagePixels;
	}

}
