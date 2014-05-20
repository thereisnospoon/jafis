package diploma.ui;

import diploma.matching.FingerprintsDatabase;
import diploma.matching.Matcher;
import diploma.model.Finger;
import diploma.model.Fingerprint;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainFrame extends JFrame {

	public static final double THRESHOLD = 1;

	private MainFrame mainFrame;

	private String loadedImagePath;

	private FingerprintPanel leftPanel, rightPanel;
	private JButton matchButton, loadButton, removeFingerButton, removeImageButton;
	private JComboBox<Finger> fingerBox;
	private JComboBox<Fingerprint> imageBox;
	private JLabel loadedFileLabel;
	private JMenuItem addFingerItem, addFPImage;

	private FingerprintsDatabase fingerprintsDatabase;

	private JFileChooser fileChooser = new JFileChooser(".");

	private static Insets DEFAULT_INSETS = new Insets(0, 0, 0, 0);

	public MainFrame() throws Exception {

		mainFrame = this;
		setTitle("JAFIS");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addElements();
		addListeners();

		fingerprintsDatabase = FingerprintsDatabase.loadExistent("db");
		updateFingerLists();
	}

	private void addListeners() {

		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					File selectedFile = fileChooser.getSelectedFile();

					if (selectedFile != null) {
						leftPanel.setImage(selectedFile.getAbsolutePath());
						loadedFileLabel.setText(selectedFile.getName());
						loadedImagePath = selectedFile.getAbsolutePath();
					}
				}
			}
		});

		addFingerItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String fingerId = JOptionPane
						.showInputDialog(mainFrame, "Finger Id", "Add new finger", JOptionPane.QUESTION_MESSAGE);

				if (!StringUtils.isEmpty(fingerId)) {
					mainFrame.getFingerprintsDatabase().addFinger(new Finger(fingerId));
					updateFingerLists();
				}
			}
		});

		addFPImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (fingerBox.getSelectedIndex() == -1) {

					JOptionPane.showMessageDialog(mainFrame, "Finger should be selected", "Warning",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					final File selectedFile = fileChooser.getSelectedFile();

					if (selectedFile != null) {

						rightPanel.setImage(selectedFile.getAbsolutePath());
						Fingerprint fingerprint = Fingerprint.extractFeatures(selectedFile.getAbsolutePath());
						mainFrame.getFingerprintsDatabase().addFingerprintToFinger((Finger) fingerBox.getSelectedItem(),
								fingerprint);
						updateImageList(fingerprint);
					}
				}
			}
		});

		fingerBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					updateImageList(null);
				}
				String nullPath = null;
				rightPanel.setImage(nullPath);
			}
		});

		imageBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					rightPanel.setImage(((Fingerprint) e.getItem()).getImage());
				}
			}
		});

		removeFingerButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				Finger finger = (Finger) fingerBox.getSelectedItem();
				if (finger != null
						&& JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mainFrame,
						"Do you really want to delete this finger?",
						"Warning", JOptionPane.YES_NO_OPTION)) {

					fingerprintsDatabase.remove(finger);
					fingerBox.setSelectedIndex(-1);
					updateFingerLists();
					updateImageList(null);
				}
			}
		});

		removeImageButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				Fingerprint fingerprint = (Fingerprint) imageBox.getSelectedItem();
				Finger finger = (Finger) fingerBox.getSelectedItem();
				if (finger != null && fingerprint != null
						&& JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mainFrame,
						"Do you really want to delete this fingerprint?",
						"Warning", JOptionPane.YES_NO_OPTION)) {

					fingerprintsDatabase.remove(finger, fingerprint);
					updateImageList(null);
					imageBox.setSelectedIndex(-1);
					String nullPath = null;
					rightPanel.setImage(nullPath);
				}
			}
		});

		matchButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (loadedImagePath != null) {

					Fingerprint newFingerprint = Fingerprint.extractFeatures(loadedImagePath);
					Fingerprint matchedFingerprint = Matcher.match(fingerprintsDatabase, newFingerprint, THRESHOLD);

					if (matchedFingerprint != null) {

						Finger finger = fingerprintsDatabase.getFinger(matchedFingerprint);
						fingerBox.setSelectedItem(finger);
						updateImageList(matchedFingerprint);
						JOptionPane.showMessageDialog(mainFrame, "Identified");
					} else {
						JOptionPane.showMessageDialog(mainFrame,"Couldn't identify given fingerprint");
					}
				} else {
					JOptionPane.showMessageDialog(mainFrame, "Please, load fingerprint image");
				}
			}
		});
	}

	private void addElements() throws Exception {

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		addFingerItem = new JMenuItem("Add finger");
		addFPImage = new JMenuItem("Add fingerprint image");
		menu.add(addFingerItem);
		menu.add(addFPImage);
		menuBar.add(menu);
		setJMenuBar(menuBar);


		leftPanel = new FingerprintPanel();
		rightPanel = new FingerprintPanel();

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(28, 0, 0, 52);
		loadButton = new JButton("Load");
		add(loadButton, c);

		loadedFileLabel = new JLabel();
		c.gridx = 1;
		add(loadedFileLabel, c);
		c.insets = DEFAULT_INSETS;

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		add(leftPanel, c);

		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		matchButton = new JButton("Identify");
		add(matchButton, c);

		c.gridx = 3;
		c.gridwidth = 2;
		add(rightPanel, c);

		fingerBox = new JComboBox<>();
		imageBox = new JComboBox<>();
		fingerBox.setPreferredSize(new Dimension(100, 20));
		imageBox.setPreferredSize(new Dimension(100, 20));
		c.insets = new Insets(25, 0, 0, 0);
		c.gridwidth = 1;
		c.gridy = 0;
		add(fingerBox, c);

		c.gridx = 4;
		add(imageBox, c);
		c.insets = DEFAULT_INSETS;

		removeFingerButton = new JButton("Remove finger");
		removeImageButton = new JButton("Remove image");
		removeImageButton.setPreferredSize(new Dimension(128, 20));
		removeFingerButton.setPreferredSize(new Dimension(128, 20));

		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 3;
		c.gridy = 2;
		add(removeFingerButton, c);

		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 4;
		add(removeImageButton, c);

		setSize(730, 500);
		setResizable(false);
	}

	private void updateImageList(Fingerprint currentFingerprint) {

		imageBox.removeAllItems();

		if (fingerBox.getSelectedItem() != null) {
			for (Fingerprint fingerprint : mainFrame.getFingerprintsDatabase()
					.getFingerprints((Finger) fingerBox.getSelectedItem())) {

				imageBox.addItem(fingerprint);
			}
		}

		if (currentFingerprint != null) {
			imageBox.setSelectedItem(currentFingerprint);
		} else {
			imageBox.setSelectedIndex(-1);
		}
	}

	private void updateFingerLists() {

		fingerBox.removeAllItems();
		for (Finger finger : fingerprintsDatabase.getFingerDb().keySet()) {
			fingerBox.addItem(finger);
		}
		fingerBox.setSelectedIndex(-1);
	}

	public FingerprintsDatabase getFingerprintsDatabase() {
		return fingerprintsDatabase;
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

	@Override
	public void dispose() {

		try {
			fingerprintsDatabase.saveDB("db");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.dispose();
	}
}
