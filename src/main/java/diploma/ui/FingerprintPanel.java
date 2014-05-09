package diploma.ui;

import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

public class FingerprintPanel extends JPanel {

	private Image image;

	{
		setSize(256, 384);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, 1, 1, null);
	}

	public void setImage(String imagePath) throws Exception {

		image = Imaging.getBufferedImage(new File(imagePath));
		setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
	}
}
