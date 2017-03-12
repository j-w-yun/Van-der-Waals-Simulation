package org.yoon_technology.simulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.yoon_technology.engine.Camera;
import org.yoon_technology.engine.Engine;
import org.yoon_technology.engine.objects.DistributionGraph2D;
import org.yoon_technology.engine.objects.TimelineGraph2D;
import org.yoon_technology.gui.Display;
import org.yoon_technology.gui.Window;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class MDSGUI {

	private Engine mdsEngine;
	private Engine graphEngine;
	private DistributionGraph2D distributionGraph1;
	//	private DistributionGraph2D distributionGraph1_1;
	private DistributionGraph2D distributionGraph2;
	//	private DistributionGraph2D distributionGraph2_1;
	private TimelineGraph2D timelineGraph3;
	private TimelineGraph2D timelineGraph4;
	private MDS mds;
	private Display mdsDisplay;
	private Display statDisplay1;
	//	private Display statDisplay1_1;
	private Display statDisplay2;
	//	private Display statDisplay2_1;
	private Display statDisplay3;
	private Display statDisplay4;

	private JPanel deleteParticleButtonPanel;
	private JPanel mdsPanel;
	private JPanel sidePanel;
	private JPanel statPanel;

	private JTextPane sidePanel_textPane;

	private JTextField xDim;
	private JTextField yDim;
	private JTextField zDim;
	private JTextField scaleRadius;
	private JTextField scaleMass;

	private JTextField numParticles;
	private JTextField speed;
	private JTextField radius;
	private JTextField mass;
	private JTextField xPosInsert;
	private JTextField yPosInsert;
	private JTextField zPosInsert;

	private ArrayList<JButton> deleteParticleButtons = new ArrayList<>();

	private void createMdsDisplay(int width, int height) {
		mdsEngine = new Engine();
		mdsEngine.addWorld(mds);
		mdsEngine.addCamera(new Vector3d(0.0, 0.0, 0.0), Camera.ORTHOGRAPHIC_PROJECTION);
		mdsEngine.getCamera().setOrientation(new Vector3d(0.0, 0.0, 0.0));

		mdsDisplay = new Display(width, height, mdsEngine);
		mdsDisplay.setWorld(mds);
		mdsDisplay.setLayout(new FlowLayout(SwingConstants.RIGHT));
		mdsDisplay.setBackground(new Color(1, 20, 26));
		mdsDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		JButton rotateButton = new JButton("S");
		rotateButton.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 14));
		rotateButton.setFocusPainted(false);
		rotateButton.setBackground(Color.BLACK);
		rotateButton.setForeground(Color.WHITE);
		rotateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mdsEngine.rotateOn(!mdsEngine.isRotateOn());
				if(mdsEngine.isRotateOn())
					rotateButton.setText("S");
				else
					rotateButton.setText("R");
			}
		});
		JButton focusComputationButton = new JButton("Rendering Focused");
		focusComputationButton.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 14));
		focusComputationButton.setFocusPainted(false);
		focusComputationButton.setBackground(Color.BLACK);
		focusComputationButton.setForeground(Color.WHITE);
		focusComputationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mdsEngine.focusComputation())
					focusComputationButton.setText("Computing Focused");
				else {
					mdsEngine.focusVisual();
					focusComputationButton.setText("Rendering Focused");
				}

				mdsEngine.rotateOn(false);
				rotateButton.setText("R");
			}
		});
		JButton zoomInButton = new JButton("+");
		zoomInButton.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		zoomInButton.setFocusPainted(false);
		zoomInButton.setBackground(Color.BLACK);
		zoomInButton.setForeground(Color.WHITE);
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mdsDisplay.setScale(mdsDisplay.getScale() * 1.25);
			}
		});
		JButton zoomOutButton = new JButton("-");
		zoomOutButton.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 14));
		zoomOutButton.setFocusPainted(false);
		zoomOutButton.setBackground(Color.BLACK);
		zoomOutButton.setForeground(Color.WHITE);
		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mdsDisplay.setScale(mdsDisplay.getScale() * 0.75);
			}
		});

		mdsDisplay.add(focusComputationButton);
		mdsDisplay.add(rotateButton);
		mdsDisplay.add(zoomInButton);
		mdsDisplay.add(zoomOutButton);

		mdsEngine.addDisplay(mdsDisplay);
	}

	private void createStatDisplay(int width, int height) {
		graphEngine = new Engine();

		graphEngine.addWorld(distributionGraph1);
		graphEngine.addWorld(distributionGraph2);
		//		graphEngine.addWorld(distributionGraph1_1);
		//		graphEngine.addWorld(distributionGraph2_1);
		graphEngine.addWorld(timelineGraph3);
		graphEngine.addWorld(timelineGraph4);
		graphEngine.addCamera(new Vector3d(0.0, 0.0, 0.0), Camera.ORTHOGRAPHIC_PROJECTION);
		graphEngine.getCamera().setRotatable(false);
		graphEngine.rotateOn(false);

		statDisplay1 = new Display(width, height, graphEngine);
		statDisplay1.setWorld(distributionGraph1);
		statDisplay1.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay1.setBackground(Color.BLACK);
		statDisplay1.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		statDisplay2 = new Display(width, height, graphEngine);
		statDisplay2.setWorld(distributionGraph2);
		statDisplay2.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay2.setBackground(Color.BLACK);
		statDisplay2.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		//		statDisplay1_1 = new Display(width, height, graphEngine);
		//		statDisplay1_1.setWorld(distributionGraph1_1);
		//		statDisplay1_1.setLayout(new FlowLayout(SwingConstants.RIGHT));
		//		statDisplay1_1.setBackground(Color.BLACK);
		//		statDisplay1_1.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		//		statDisplay2_1 = new Display(width, height, graphEngine);
		//		statDisplay2_1.setWorld(distributionGraph2_1);
		//		statDisplay2_1.setLayout(new FlowLayout(SwingConstants.RIGHT));
		//		statDisplay2_1.setBackground(Color.BLACK);
		//		statDisplay2_1.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		statDisplay3 = new Display(width, height, graphEngine);
		statDisplay3.setWorld(timelineGraph3);
		statDisplay3.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay3.setBackground(Color.BLACK);
		statDisplay3.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		statDisplay4 = new Display(width, height, graphEngine);
		statDisplay4.setWorld(timelineGraph4);
		statDisplay4.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay4.setBackground(Color.BLACK);
		statDisplay4.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		JButton reset1Button = new JButton("RESET");
		reset1Button.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
		reset1Button.setFocusPainted(false);
		reset1Button.setBackground(Color.BLACK);
		reset1Button.setForeground(Color.WHITE);
		reset1Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				distributionGraph1.clearObservations();
			}
		});

		JButton reset2Button = new JButton("RESET");
		reset2Button.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
		reset2Button.setFocusPainted(false);
		reset2Button.setBackground(Color.BLACK);
		reset2Button.setForeground(Color.WHITE);
		reset2Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				distributionGraph2.clearObservations();
			}
		});

		//		JButton reset1_1Button = new JButton("RESET");
		//		reset1_1Button.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
		//		reset1_1Button.setFocusPainted(false);
		//		reset1_1Button.setBackground(Color.BLACK);
		//		reset1_1Button.setForeground(Color.WHITE);
		//		reset1Button.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				distributionGraph1_1.clearObservations();
		//			}
		//		});
		//
		//		JButton reset2_1Button = new JButton("RESET");
		//		reset2_1Button.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
		//		reset2_1Button.setFocusPainted(false);
		//		reset2_1Button.setBackground(Color.BLACK);
		//		reset2_1Button.setForeground(Color.WHITE);
		//		reset2_1Button.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				distributionGraph2_1.clearObservations();
		//			}
		//		});

		JButton resetAvgButton = new JButton("RESET AVG");
		resetAvgButton.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
		resetAvgButton.setFocusPainted(false);
		resetAvgButton.setBackground(Color.BLACK);
		resetAvgButton.setForeground(Color.WHITE);
		resetAvgButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mds.cumulativePressure = 0;
				mds.simulationSecondsPressure = 0.0000001;
			}
		});

		statDisplay1.add(reset1Button); // Reset button for each
		statDisplay2.add(reset2Button);
		//		statDisplay1_1.add(reset1_1Button);
		//		statDisplay2_1.add(reset2_1Button);
		statDisplay3.add(resetAvgButton);

		graphEngine.addDisplay(statDisplay1);
		graphEngine.addDisplay(statDisplay2);
		graphEngine.addDisplay(statDisplay3);
		graphEngine.addDisplay(statDisplay4);
		//		graphEngine.addDisplay(statDisplay1_1);
		//		graphEngine.addDisplay(statDisplay2_1);
	}

	private void populateWindow() {
		Window.create("Yun Simulation");

		populateDisplayPanel();
		populateSidePanel();
		populateStatPanel();

		Window.addPanel(mdsPanel, BorderLayout.WEST);
		Window.addPanel(sidePanel, BorderLayout.EAST);
		Window.addPanel(statPanel, BorderLayout.CENTER);

		Window.start();
	}

	private void populateDisplayPanel() {
		mdsPanel = new JPanel(new BorderLayout());

		JPanel controlPanel = new JPanel(new GridLayout(0, 1)); // rows, cols
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		/*
		 * Border panels contain a block panel and a button to the left
		 */
		JPanel borderPanel1 = new JPanel(new BorderLayout());
		JPanel borderPanel2 = new JPanel(new BorderLayout());

		/*
		 * Block panels contain groups of 2
		 * (row 1, 2)
		 * (row 3, 4)
		 */
		JPanel blockPanel1 = new JPanel(new GridLayout(0, 1));
		blockPanel1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		JPanel blockPanel2 = new JPanel(new GridLayout(0, 1));
		blockPanel2.setBorder(BorderFactory.createLineBorder(Color.GRAY));


		/*
		 * Row # 1
		 */
		JPanel rowOnePanel = new JPanel(new FlowLayout(SwingConstants.LEADING));
		rowOnePanel.setBackground(new Color(1, 20, 26));

		JLabel dimensionLabel = new JLabel("SIZE          ");
		dimensionLabel.setForeground(Color.GRAY);
		JLabel xDimLabel = new JLabel("X:");
		xDimLabel.setForeground(Color.WHITE);
		JLabel yDimLabel = new JLabel("Y:");
		yDimLabel.setForeground(Color.WHITE);
		JLabel zDimLabel = new JLabel("Z:");
		zDimLabel.setForeground(Color.WHITE);

		xDim = new JTextField(5);
		xDim.setBackground(Color.DARK_GRAY);
		xDim.setForeground(Color.WHITE);
		xDim.setBorder(BorderFactory.createEmptyBorder());
		xDim.setText(Double.toString(mds.xMax - mds.xMin));
		yDim = new JTextField(5);
		yDim.setBackground(Color.DARK_GRAY);
		yDim.setForeground(Color.WHITE);
		yDim.setBorder(BorderFactory.createEmptyBorder());
		yDim.setText(Double.toString(mds.yMax - mds.yMin));
		zDim = new JTextField(5);
		zDim.setBackground(Color.DARK_GRAY);
		zDim.setForeground(Color.WHITE);
		zDim.setBorder(BorderFactory.createEmptyBorder());
		zDim.setText(Double.toString(mds.zMax - mds.zMin));

		JLabel scaleLabel = new JLabel("SCALE      ");
		scaleLabel.setForeground(Color.GRAY);
		JLabel scaleRadiusLabel = new JLabel("R:");
		scaleRadiusLabel.setForeground(Color.WHITE);
		JLabel scaleMassLabel = new JLabel("M:");
		scaleMassLabel.setForeground(Color.WHITE);

		scaleRadius = new JTextField(3);
		scaleRadius.setBackground(Color.DARK_GRAY);
		scaleRadius.setForeground(Color.WHITE);
		scaleRadius.setBorder(BorderFactory.createEmptyBorder());
		scaleRadius.setText(Double.toString(mds.scaleRadius));
		scaleMass = new JTextField(3);
		scaleMass.setBackground(Color.DARK_GRAY);
		scaleMass.setForeground(Color.WHITE);
		scaleMass.setBorder(BorderFactory.createEmptyBorder());
		scaleMass.setText(Double.toString(mds.scaleMass));



		rowOnePanel.add(dimensionLabel);
		rowOnePanel.add(xDimLabel);
		rowOnePanel.add(xDim);
		rowOnePanel.add(yDimLabel);
		rowOnePanel.add(yDim);
		rowOnePanel.add(zDimLabel);
		rowOnePanel.add(zDim);
		//		rowOnePanel.add(updateSettingsButton);

		/*
		 * Row # 2
		 */
		JPanel rowTwoPanel = new JPanel(new FlowLayout(SwingConstants.LEADING));
		rowTwoPanel.setBackground(new Color(1, 20, 26));

		rowTwoPanel.add(scaleLabel);
		rowTwoPanel.add(scaleRadiusLabel);
		rowTwoPanel.add(scaleRadius);
		rowTwoPanel.add(scaleMassLabel);
		rowTwoPanel.add(scaleMass);

		/*
		 * Row # 3
		 */
		JPanel rowThreePanel = new JPanel(new FlowLayout(SwingConstants.LEADING));
		rowThreePanel.setBackground(new Color(1, 20, 26));

		JLabel insertLabel = new JLabel("ADD          ");
		insertLabel.setForeground(Color.GRAY);
		JLabel numParticlesLabel = new JLabel("N:");
		numParticlesLabel.setForeground(Color.WHITE);
		JLabel speedLabel = new JLabel("V:");
		speedLabel.setForeground(Color.WHITE);
		JLabel radiusLabel = new JLabel("R:");
		radiusLabel.setForeground(Color.WHITE);
		JLabel massLabel = new JLabel("M:");
		massLabel.setForeground(Color.WHITE);
		JLabel posInsertLabel = new JLabel("POSITION");
		posInsertLabel.setForeground(Color.GRAY);
		JLabel xPosInsertLabel = new JLabel("X:");
		xPosInsertLabel.setForeground(Color.WHITE);
		JLabel yPosInsertLabel = new JLabel("Y:");
		yPosInsertLabel.setForeground(Color.WHITE);
		JLabel zPosInsertLabel = new JLabel("Z:");
		zPosInsertLabel.setForeground(Color.WHITE);

		numParticles = new JTextField(3);
		numParticles.setBackground(Color.DARK_GRAY);
		numParticles.setForeground(Color.WHITE);
		numParticles.setBorder(BorderFactory.createEmptyBorder());
		numParticles.setText(Integer.toString(MDS.NUM_PARTICLES_INSERT));
		speed = new JTextField(4);
		speed.setBackground(Color.DARK_GRAY);
		speed.setForeground(Color.WHITE);
		speed.setBorder(BorderFactory.createEmptyBorder());
		speed.setText(Double.toString(MDS.SPEED_PARTICLES));
		radius = new JTextField(3);
		radius.setBackground(Color.DARK_GRAY);
		radius.setForeground(Color.WHITE);
		radius.setBorder(BorderFactory.createEmptyBorder());
		radius.setText(Double.toString(MDS.RADIUS_PARTICLES));
		mass = new JTextField(3);
		mass.setBackground(Color.DARK_GRAY);
		mass.setForeground(Color.WHITE);
		mass.setBorder(BorderFactory.createEmptyBorder());
		mass.setText(Double.toString(MDS.MASS_PARTICLES));

		xPosInsert = new JTextField(3);
		xPosInsert.setBackground(Color.DARK_GRAY);
		xPosInsert.setForeground(Color.WHITE);
		xPosInsert.setBorder(BorderFactory.createEmptyBorder());
		xPosInsert.setText(Double.toString(MDS.X_POSITION_PARTICLES));
		yPosInsert = new JTextField(3);
		yPosInsert.setBackground(Color.DARK_GRAY);
		yPosInsert.setForeground(Color.WHITE);
		yPosInsert.setBorder(BorderFactory.createEmptyBorder());
		yPosInsert.setText(Double.toString(MDS.Y_POSITION_PARTICLES));
		zPosInsert = new JTextField(3);
		zPosInsert.setBackground(Color.DARK_GRAY);
		zPosInsert.setForeground(Color.WHITE);
		zPosInsert.setBorder(BorderFactory.createEmptyBorder());
		zPosInsert.setText(Double.toString(MDS.Z_POSITION_PARTICLES));

		rowThreePanel.add(insertLabel);
		rowThreePanel.add(numParticlesLabel);
		rowThreePanel.add(numParticles);
		rowThreePanel.add(speedLabel);
		rowThreePanel.add(speed);
		rowThreePanel.add(radiusLabel);
		rowThreePanel.add(radius);
		rowThreePanel.add(massLabel);
		rowThreePanel.add(mass);
		//		rowThreePanel.add(insertMoleculesButton);

		/*
		 * Row # 4
		 */
		JPanel rowFourPanel = new JPanel(new FlowLayout(SwingConstants.LEADING));
		rowFourPanel.setBackground(new Color(1, 20, 26));
		rowFourPanel.setPreferredSize(new Dimension(300, 0));

		final String[] colors = {"Red", "Yellow", "Green", "Cyan", "Blue", "Pink"};
		JComboBox<String> colorBox = new JComboBox<>(colors);
		colorBox.setBackground(new Color(1, 20, 26));
		colorBox.setForeground(Color.WHITE);
		colorBox.setPreferredSize(new Dimension(65, 20));
		colorBox.setBorder(BorderFactory.createEmptyBorder());

		rowFourPanel.add(posInsertLabel);
		rowFourPanel.add(xPosInsertLabel);
		rowFourPanel.add(xPosInsert);
		rowFourPanel.add(yPosInsertLabel);
		rowFourPanel.add(yPosInsert);
		rowFourPanel.add(zPosInsertLabel);
		rowFourPanel.add(zPosInsert);
		rowFourPanel.add(colorBox);

		/*
		 * Row # 5
		 */
		JPanel rowFivePanel = new JPanel(new GridLayout(0, 2));
		rowFivePanel.setBackground(Color.GRAY);
		rowFivePanel.setPreferredSize(new Dimension(300, 0));

		JButton pauseOrResumeButton = new JButton("PAUSE");
		pauseOrResumeButton.setBackground(new Color(1, 20, 26));
		pauseOrResumeButton.setForeground(Color.WHITE);
		pauseOrResumeButton.setFocusPainted(false);

		pauseOrResumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mdsEngine.pause()) {
					pauseOrResumeButton.setText("RESUME");
				} else {
					mdsEngine.resume();
					pauseOrResumeButton.setText("PAUSE");
				}
			}
		});
		JButton startButton = new JButton("REMOVE ALL");
		startButton.setBackground(new Color(1, 20, 26));
		startButton.setForeground(Color.WHITE);
		startButton.setFocusPainted(false);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double xDim_ = 0.0, yDim_ = 0.0, zDim_ = 0.0;
				try {
					xDim_ = Double.parseDouble(xDim.getText());
				} catch (NumberFormatException nfe) {}
				try {
					yDim_ = Double.parseDouble(yDim.getText());
				} catch (NumberFormatException nfe) {}
				try {
					zDim_ = Double.parseDouble(zDim.getText());
				} catch (NumberFormatException nfe) {}

				double scaleRadius_ = 0.0, scaleMass_ = 0.0;
				try {
					scaleRadius_ = Double.parseDouble(scaleRadius.getText());
				} catch (NumberFormatException nfe) {}
				try {
					scaleMass_ = Double.parseDouble(scaleMass.getText());
				} catch (NumberFormatException nfe) {}

				mds.updateWorldSettings(scaleRadius_, scaleMass_,
						(xDim_ - (xDim_/2)),
						-(xDim_/2),
						(yDim_ - (yDim_/2)),
						-(yDim_/2),
						(zDim_ - (zDim_/2)),
						-(zDim_/2));

				// Remove all particle type buttons
				deleteParticleButtons.clear();
				deleteParticleButtonPanel.removeAll();
				deleteParticleButtonPanel.revalidate();
				deleteParticleButtonPanel.repaint();

				mds.initialize(); // Restarts
				mdsDisplay.setScale(mds.resized(mdsDisplay.getWidth(), mdsDisplay.getHeight()));
			}
		});


		rowFivePanel.add(pauseOrResumeButton);
		rowFivePanel.add(startButton);

		/*
		 * Update and Insert Buttons
		 */
		JButton updateSettingsButton = new JButton("<HTML>UPDATE<br>SIZE/SCALE</HTML>");
		updateSettingsButton.setPreferredSize(new Dimension(120, 50));
		updateSettingsButton.setBackground(new Color(1, 20, 26));
		updateSettingsButton.setForeground(Color.WHITE);
		updateSettingsButton.setFocusPainted(false);
		updateSettingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double xDim_ = 0.0, yDim_ = 0.0, zDim_ = 0.0;
				try {
					xDim_ = Double.parseDouble(xDim.getText());
				} catch (NumberFormatException nfe) {}
				try {
					yDim_ = Double.parseDouble(yDim.getText());
				} catch (NumberFormatException nfe) {}
				try {
					zDim_ = Double.parseDouble(zDim.getText());
				} catch (NumberFormatException nfe) {}
				double scaleRadius_ = 0.0, scaleMass_ = 0.0;
				try {
					scaleRadius_ = Double.parseDouble(scaleRadius.getText());
				} catch (NumberFormatException nfe) {}
				try {
					scaleMass_ = Double.parseDouble(scaleMass.getText());
				} catch (NumberFormatException nfe) {}

				mds.updateWorldSettings(scaleRadius_, scaleMass_,
						(xDim_ - (xDim_/2)),
						-(xDim_/2),
						(yDim_ - (yDim_/2)),
						-(yDim_/2),
						(zDim_ - (zDim_/2)),
						-(zDim_/2));
				mdsDisplay.setScale(mds.resized(mdsDisplay.getWidth(), mdsDisplay.getHeight()));
			}
		});

		deleteParticleButtonPanel = new JPanel(new GridLayout(0, colors.length));
		deleteParticleButtonPanel.setPreferredSize(new Dimension(0, 20));
		deleteParticleButtonPanel.setBackground(Color.BLACK);
		deleteParticleButtonPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		JButton insertMoleculesButton = new JButton("<HTML>INSERT<br>PARTICLES</HTML>");
		insertMoleculesButton.setPreferredSize(new Dimension(120, 50));
		insertMoleculesButton.setBackground(new Color(1, 20, 26));
		insertMoleculesButton.setForeground(Color.WHITE);
		insertMoleculesButton.setFocusPainted(false);
		insertMoleculesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numParticles_ = 0;
				double speed_ = 0.0, radius_ = 0.0, mass_ = 0.0;
				double xPosInsert_ = 0.0, yPosInsert_ = 0.0, zPosInsert_ = 0.0;
				try {
					numParticles_ = Integer.parseInt(numParticles.getText());
				} catch (NumberFormatException nfe) {}

				try {
					speed_ = Double.parseDouble(speed.getText());
				} catch (NumberFormatException nfe) {}
				try {
					radius_ = Double.parseDouble(radius.getText());
				} catch (NumberFormatException nfe) {}
				try {
					mass_ = Double.parseDouble(mass.getText());
				} catch (NumberFormatException nfe) {}

				try {
					xPosInsert_ = Double.parseDouble(xPosInsert.getText());
				} catch (NumberFormatException nfe) {}
				try {
					yPosInsert_ = Double.parseDouble(yPosInsert.getText());
				} catch (NumberFormatException nfe) {}
				try {
					zPosInsert_ = Double.parseDouble(zPosInsert.getText());
				} catch (NumberFormatException nfe) {}


				// Get color from String
				Color insertParticleColor = getColor((String)(colorBox.getSelectedItem()));

				mds.insertParticles(numParticles_, speed_, radius_, mass_,
						xPosInsert_, yPosInsert_, zPosInsert_,
						insertParticleColor);

				// Add a button to remove the particle
				boolean addButton = true;
				for(JButton button : deleteParticleButtons) {
					if(button.getBackground().equals(insertParticleColor)) {
						addButton = false;
						break;
					}
				}

				if(addButton) {
					JButton deleteButton = new JButton("REMOVE");
					deleteButton.setMargin(new Insets(0,0,0,0));
					deleteButton.setBackground(insertParticleColor);
					deleteButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							deleteParticleButtons.remove(deleteButton);
							deleteParticleButtonPanel.remove(deleteButton);
							mds.removeParticles(deleteButton.getBackground()); // Get color
							deleteParticleButtonPanel.revalidate();
							deleteParticleButtonPanel.repaint();
						}
					});
					deleteParticleButtons.add(deleteButton);
					deleteParticleButtonPanel.add(deleteButton);

				}
			}
		});

		blockPanel1.add(rowOnePanel);
		blockPanel1.add(rowTwoPanel);
		blockPanel2.add(rowThreePanel);
		blockPanel2.add(rowFourPanel);

		borderPanel1.add(blockPanel1, BorderLayout.CENTER);
		borderPanel1.add(updateSettingsButton, BorderLayout.EAST);
		borderPanel2.add(blockPanel2, BorderLayout.CENTER);
		borderPanel2.add(insertMoleculesButton, BorderLayout.EAST);

		controlPanel.add(borderPanel1);
		controlPanel.add(borderPanel2);
		//		controlPanel.add(rowOnePanel);
		//		controlPanel.add(rowTwoPanel);
		//		controlPanel.add(rowThreePanel);
		//		controlPanel.add(rowFourPanel);
		controlPanel.add(rowFivePanel);

		mdsPanel.add(deleteParticleButtonPanel, BorderLayout.NORTH);
		mdsPanel.add(mdsDisplay, BorderLayout.CENTER);
		mdsPanel.add(controlPanel, BorderLayout.SOUTH);
	}

	private void populateSidePanel() {
		sidePanel = new JPanel(new BorderLayout());
		sidePanel.setPreferredSize(new Dimension(150, 500));

		sidePanel_textPane = new JTextPane();
		sidePanel_textPane.setEditable(false);
		sidePanel_textPane.setMargin(new Insets(5, 5, 5, 5));
		sidePanel_textPane.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 10));
		sidePanel_textPane.setBackground(new Color(1, 20, 26));
		sidePanel_textPane.setForeground(Color.WHITE);
		sidePanel_textPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		sidePanel.add(sidePanel_textPane);
	}

	private void populateStatPanel() {
		statPanel = new JPanel(new GridLayout(0, 1));

		statPanel.add(statDisplay1);
		statPanel.add(statDisplay2);
		//		statPanel.add(statDisplay1_1);
		//		statPanel.add(statDisplay2_1);
		statPanel.add(statDisplay3);
		statPanel.add(statDisplay4);
	}

	private static Color getColor(String colorName) {
		if(colorName.equals("Red")) {
			return Color.RED;
		} else if(colorName.equals("Yellow")) {
			return Color.YELLOW;
		} else if(colorName.equals("Green")) {
			return Color.GREEN;
		} else if(colorName.equals("Blue")) {
			return Color.BLUE;
		} else if(colorName.equals("Cyan")) {
			return Color.CYAN;
		} else if(colorName.equals("Pink")) {
			return Color.PINK;
		} else {
			return null;
		}
	}

	public void setText(Object[] labels, Object[] numbers) {
		sidePanel_textPane.setText(""); // Clear text

		try {
			StyledDocument doc = sidePanel_textPane.getStyledDocument();

			SimpleAttributeSet labelAttrib = new SimpleAttributeSet();
			StyleConstants.setAlignment(labelAttrib, StyleConstants.ALIGN_LEFT);
			StyleConstants.setForeground(labelAttrib, Color.GRAY);

			SimpleAttributeSet numberAttrib = new SimpleAttributeSet();
			StyleConstants.setAlignment(numberAttrib, StyleConstants.ALIGN_CENTER);
			StyleConstants.setForeground(numberAttrib, Color.WHITE);

			if(labels.length != numbers.length)
				System.err.println("Labels length does not match numbers length!");


			for(int j = 0; j < labels.length; j++) {
				doc.setParagraphAttributes(doc.getLength(), 1, labelAttrib, false);
				doc.insertString(doc.getLength(), labels[j] + "", labelAttrib);

				doc.setParagraphAttributes(doc.getLength(), 1, numberAttrib, false);
				doc.insertString(doc.getLength(), numbers[j] + "\n", numberAttrib);
			}

			SimpleAttributeSet myAttrib = new SimpleAttributeSet();
			StyleConstants.setAlignment(myAttrib, StyleConstants.ALIGN_CENTER);
			StyleConstants.setForeground(myAttrib, Color.DARK_GRAY);

			doc.setParagraphAttributes(doc.getLength(), 1, myAttrib, false);
			doc.insertString(doc.getLength(), "\nMade by\nJaewan Yun\njay50@pitt.edu", myAttrib);

		} catch (BadLocationException e) {}
	}

	public MDSGUI() {
		mds = new MDS();
		mds.restoreWorldSettings();
		mds.initialize();

		distributionGraph1 = new DistributionGraph2D("Speed Distribution");
		distributionGraph1.restoreWorldSettings();
		distributionGraph1.initialize();

		//		distributionGraph1_1 = new DistributionGraph2D("Total Speed");
		//		distributionGraph1_1.restoreWorldSettings();
		//		distributionGraph1_1.initialize();

		distributionGraph2 = new DistributionGraph2D("Transferred Momentum Distribution");
		distributionGraph2.restoreWorldSettings();
		distributionGraph2.initialize();

		//		distributionGraph2_1 = new DistributionGraph2D("Total Rate of Momentum Transfer");
		//		distributionGraph2_1.restoreWorldSettings();
		//		distributionGraph2_1.initialize();

		timelineGraph3 = new TimelineGraph2D("Current Pressure");
		timelineGraph3.restoreWorldSettings();
		timelineGraph3.initialize();
		timelineGraph3.addUniqueObservation("Cur", Color.BLUE);
		timelineGraph3.addUniqueObservation("Avg", Color.WHITE);

		timelineGraph4 = new TimelineGraph2D("Collisions Per Milisecond");
		timelineGraph4.restoreWorldSettings();
		timelineGraph4.initialize();
		timelineGraph4.addUniqueObservation("Total", Color.BLUE);
		timelineGraph4.addUniqueObservation("Inter", Color.WHITE);
		timelineGraph4.addUniqueObservation("Wall", Color.MAGENTA);

		mds.addStatGraph(distributionGraph1);
		mds.addStatGraph(distributionGraph2);
		//		mds.addStatGraph(distributionGraph1_1);
		//		mds.addStatGraph(distributionGraph2_1);
		mds.addTimelineGraph(timelineGraph3);
		mds.addTimelineGraph(timelineGraph4);

		createMdsDisplay(400, 400);
		createStatDisplay(500, 150);

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					populateWindow();
				}
			});
		} catch (Exception e) {
			System.err.println("Failed to create GUI.");
			e.printStackTrace();
		}

		new Thread(() -> {
			mdsEngine.start();
		}).start();

		new Thread(() -> {
			graphEngine.start();
		}).start();

		// TODO
	}
}
