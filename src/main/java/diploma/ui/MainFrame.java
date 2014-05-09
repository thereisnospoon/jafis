package diploma.ui;

import diploma.model.Fingerprint;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

	private FingerprintPanel leftPanel, rightPanel;
	public MainFrame() throws Exception {

		setTitle("JAFIS");

		leftPanel = new FingerprintPanel();
		leftPanel.setImage("C:\\Users\\nagrizolich\\Desktop\\DB2_B\\103_6.tiff");
		rightPanel = new FingerprintPanel();
		rightPanel.setImage("C:\\Users\\nagrizolich\\Desktop\\DB2_B\\103_6.tiff");

		setLayout(new FlowLayout());
		add(leftPanel);
		add(rightPanel);
		setSize(600, 500);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new MainFrame().setVisible(true);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
