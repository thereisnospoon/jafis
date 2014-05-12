package diploma.ui;

import org.apache.commons.imaging.Imaging;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.File;

public class FingerprintPanel extends JPanel {

	private Image image;

	public FingerprintPanel() {

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		setPreferredSize(new Dimension(256, 364));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, 1, 1, null);
	}

	public void setImage(String imagePath) {

		if (imagePath != null) {
			try {
				image = Imaging.getBufferedImage(new File(imagePath));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			image = null;
		}

		repaint();
	}
}
