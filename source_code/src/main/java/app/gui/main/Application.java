package app.gui.main;	

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Application extends JFrame {

	private static final long serialVersionUID = 1L;
	public static double SCREEN_WIDTH;
	public static double SCREEN_HEIGHT;
	public static double LAYOUT_QUERY_VIEW_WIDTH;
	public static double LAYOUT_QUERY_VIEW_HEIGHT;
	
	public static double LAYOUT_HISTOGRAM_HEIGHT;
	
	public static double LAYOUT_EMBEDDINGS_VIEW_WIDTH;

	public Application() throws MalformedURLException, IOException {
		
		//1024x768
		//1280x800
		//1366×768
		//1980x900
		SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width; // 1980;// 
		SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;//1080;//
		
		//WIDTHS
		LAYOUT_QUERY_VIEW_WIDTH = SCREEN_WIDTH * 0.22; //0.20
		LAYOUT_EMBEDDINGS_VIEW_WIDTH = SCREEN_WIDTH * 0.28; //0.20
		
		//HEIGHTS
		LAYOUT_QUERY_VIEW_HEIGHT = SCREEN_HEIGHT * 0.40;
		LAYOUT_HISTOGRAM_HEIGHT = SCREEN_HEIGHT * 0.46;	

		
		setExtendedState(Frame.MAXIMIZED_BOTH);
//		setExtendedState(Frame.NORMAL);
		
		setLocationRelativeTo(null);
		setTitle(Constants.MAIN_TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(Constants.FRAME_MINIMUM_SIZE_WIDTH, Constants.FRAME_MINIMUM_SIZE_HEIGHT));
		BufferedImage image = ImageIO.read(new File("icon.png"));
		setIconImage(image);

		MainSplitPanel splitPane = new MainSplitPanel();
		setContentPane(splitPane);
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		Application root = new Application();
		root.setVisible(true);
	}

}
