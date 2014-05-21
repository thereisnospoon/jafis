package diploma.ui;

import ij.ImagePlus;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

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
				image = new ImagePlus(imagePath).getBufferedImage();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			image = null;
		}

		repaint();
	}

	public void setImage(ImageIcon image) {

		this.image = image.getImage();
		repaint();
	}
}
