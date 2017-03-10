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

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
	private StatLineGraph2D distributionGraph1;
	private StatLineGraph2D distributionGraph2;
	private SlidingLineGraph2D timelineGraph3;
	private SlidingLineGraph2D timelineGraph4;
	private MDS mds;
	private Display mdsDisplay;
	private Display statDisplay1;
	private Display statDisplay2;
	private Display statDisplay3;
	private Display statDisplay4;

	private JPanel mdsPanel;
	private JPanel sidePanel;
	private JPanel statPanel;

	private JTextPane sidePanel_textPane;

	private JTextField xDim;
	private JTextField yDim;
	private JTextField zDim;
	private JTextField numMolecules;
	private JTextField initSpeed;
	private JTextField radius;
	private JTextField mass;

	private void createMdsDisplay(int width, int height) {
		mdsEngine = new Engine();
		mdsEngine.addWorld(mds);
		mdsEngine.addCamera(new Vector3d(0.0, 0.0, 0.0), Camera.ORTHOGRAPHIC_PROJECTION);
		mdsEngine.getCamera().setOrientation(new Vector3d(0.0, 0.0, 0.0));

		mdsDisplay = new Display(width, height, mdsEngine);
		mdsDisplay.setWorld(mds);
		mdsDisplay.setLayout(new FlowLayout(SwingConstants.RIGHT));
		mdsDisplay.setBackground(new Color(1, 20, 26));

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
		graphEngine.addWorld(timelineGraph3);
		graphEngine.addWorld(timelineGraph4);
		graphEngine.addCamera(new Vector3d(0.0, 0.0, 0.0), Camera.ORTHOGRAPHIC_PROJECTION);
		graphEngine.getCamera().setRotatable(false);
		graphEngine.rotateOn(false);

		statDisplay1 = new Display(width, height, graphEngine);
		statDisplay1.setWorld(distributionGraph1);
		statDisplay1.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay1.setBackground(Color.BLACK);
		statDisplay2 = new Display(width, height, graphEngine);
		statDisplay2.setWorld(distributionGraph2);
		statDisplay2.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay2.setBackground(Color.BLACK);
		statDisplay3 = new Display(width, height, graphEngine);
		statDisplay3.setWorld(timelineGraph3);
		statDisplay3.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay3.setBackground(Color.BLACK);
		statDisplay4 = new Display(width, height, graphEngine);
		statDisplay4.setWorld(timelineGraph4);
		statDisplay4.setLayout(new FlowLayout(SwingConstants.RIGHT));
		statDisplay4.setBackground(Color.BLACK);

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

		statDisplay1.add(reset1Button); // Reset button for each
		statDisplay2.add(reset2Button);

		graphEngine.addDisplay(statDisplay1);
		graphEngine.addDisplay(statDisplay2);
		graphEngine.addDisplay(statDisplay3);
		graphEngine.addDisplay(statDisplay4);
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
		 * Row # 1
		 */
		JPanel rowOnePanel = new JPanel(new FlowLayout());
		rowOnePanel.setBackground(new Color(1, 20, 26));

		JLabel dimensionLabel = new JLabel("CONFINED DIMENSION            ");
		dimensionLabel.setForeground(Color.WHITE);
		JLabel xDimLabel = new JLabel("            X: ");
		xDimLabel.setForeground(Color.WHITE);
		JLabel yDimLabel = new JLabel("              Y: ");
		yDimLabel.setForeground(Color.WHITE);
		JLabel zDimLabel = new JLabel("           Z: ");
		zDimLabel.setForeground(Color.WHITE);

		xDim = new JTextField(4);
		xDim.setBackground(Color.DARK_GRAY);
		xDim.setForeground(Color.WHITE);
		xDim.setBorder(BorderFactory.createEmptyBorder());
		xDim.setText(Double.toString(mds.xMax - mds.xMin));
		yDim = new JTextField(4);
		yDim.setBackground(Color.DARK_GRAY);
		yDim.setForeground(Color.WHITE);
		yDim.setBorder(BorderFactory.createEmptyBorder());
		yDim.setText(Double.toString(mds.yMax - mds.yMin));
		zDim = new JTextField(4);
		zDim.setBackground(Color.DARK_GRAY);
		zDim.setForeground(Color.WHITE);
		zDim.setBorder(BorderFactory.createEmptyBorder());
		zDim.setText(Double.toString(mds.zMax - mds.zMin));

		rowOnePanel.add(dimensionLabel);
		rowOnePanel.add(xDimLabel);
		rowOnePanel.add(xDim);
		rowOnePanel.add(yDimLabel);
		rowOnePanel.add(yDim);
		rowOnePanel.add(zDimLabel);
		rowOnePanel.add(zDim);

		/*
		 * Row # 2
		 */
		JPanel rowTwoPanel = new JPanel(new FlowLayout());
		rowTwoPanel.setBackground(new Color(1, 20, 26));

		JLabel numMoleculesLabel = new JLabel("MOLECULES *: ");
		numMoleculesLabel.setForeground(Color.WHITE);
		JLabel initSpeedLabel = new JLabel("  VELOCITY *: ");
		initSpeedLabel.setForeground(Color.WHITE);
		JLabel radiusLabel = new JLabel("  RADIUS: ");
		radiusLabel.setForeground(Color.WHITE);
		JLabel massLabel = new JLabel("  MASS: ");
		massLabel.setForeground(Color.WHITE);

		numMolecules = new JTextField(4);
		numMolecules.setBackground(Color.DARK_GRAY);
		numMolecules.setForeground(Color.WHITE);
		numMolecules.setBorder(BorderFactory.createEmptyBorder());
		numMolecules.setText(Integer.toString(mds.numMolecules));
		initSpeed = new JTextField(4);
		initSpeed.setBackground(Color.DARK_GRAY);
		initSpeed.setForeground(Color.WHITE);
		initSpeed.setBorder(BorderFactory.createEmptyBorder());
		initSpeed.setText(Double.toString(mds.initSpeed));
		radius = new JTextField(4);
		radius.setBackground(Color.DARK_GRAY);
		radius.setForeground(Color.WHITE);
		radius.setBorder(BorderFactory.createEmptyBorder());
		radius.setText(Double.toString(mds.radius));
		mass = new JTextField(4);
		mass.setBackground(Color.DARK_GRAY);
		mass.setForeground(Color.WHITE);
		mass.setBorder(BorderFactory.createEmptyBorder());
		mass.setText(Double.toString(mds.mass));

		rowTwoPanel.add(numMoleculesLabel);
		rowTwoPanel.add(numMolecules);
		rowTwoPanel.add(initSpeedLabel);
		rowTwoPanel.add(initSpeed);
		rowTwoPanel.add(radiusLabel);
		rowTwoPanel.add(radius);
		rowTwoPanel.add(massLabel);
		rowTwoPanel.add(mass);

		/*
		 * Row # 3
		 */
		JPanel rowThreePanel = new JPanel(new GridLayout(0, 3));
		rowThreePanel.setBackground(Color.GRAY);
		rowThreePanel.setPreferredSize(new Dimension(300, 0));

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
		JButton updateSettingsButton = new JButton("UPDATE");
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
				double radius_ = 0.0, mass_ = 0.0;
				try {
					radius_ = Double.parseDouble(radius.getText());
				} catch (NumberFormatException nfe) {}
				try {
					mass_ = Double.parseDouble(mass.getText());
				} catch (NumberFormatException nfe) {}

				mds.updateWorldSettings(mds.numMolecules, mds.initSpeed, radius_, mass_,
						(xDim_ - (xDim_/2)),
						-(xDim_/2),
						(yDim_ - (yDim_/2)),
						-(yDim_/2),
						(zDim_ - (zDim_/2)),
						-(zDim_/2));
				mdsDisplay.setScale(mds.resized(mdsDisplay.getWidth(), mdsDisplay.getHeight()));
			}
		});
		JButton startButton = new JButton("RESTART *");
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

				int numMolecules_ = 0;
				try {
					numMolecules_ = Integer.parseInt(numMolecules.getText());
				} catch (NumberFormatException nfe) {}

				double initSpeed_ = 0.0, radius_ = 0.0, mass_ = 0.0;
				try {
					initSpeed_ = Double.parseDouble(initSpeed.getText());
				} catch (NumberFormatException nfe) {}
				try {
					radius_ = Double.parseDouble(radius.getText());
				} catch (NumberFormatException nfe) {}
				try {
					mass_ = Double.parseDouble(mass.getText());
				} catch (NumberFormatException nfe) {}

				mds.updateWorldSettings(numMolecules_, initSpeed_, radius_, mass_,
						(xDim_ - (xDim_/2)),
						-(xDim_/2),
						(yDim_ - (yDim_/2)),
						-(yDim_/2),
						(zDim_ - (zDim_/2)),
						-(zDim_/2));
				mds.initialize(); // Restarts
				mdsDisplay.setScale(mds.resized(mdsDisplay.getWidth(), mdsDisplay.getHeight()));
			}
		});


		rowThreePanel.add(pauseOrResumeButton);
		rowThreePanel.add(updateSettingsButton);
		rowThreePanel.add(startButton);

		controlPanel.add(rowOnePanel);
		controlPanel.add(rowTwoPanel);
		controlPanel.add(rowThreePanel);

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

		sidePanel.add(sidePanel_textPane);
	}

	private void populateStatPanel() {
		statPanel = new JPanel(new GridLayout(0, 1));

		statPanel.add(statDisplay1);
		statPanel.add(statDisplay2);
		statPanel.add(statDisplay3);
		statPanel.add(statDisplay4);
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

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public MDSGUI() {
		mds = new MDS();
		mds.restoreWorldSettings();
		mds.initialize();

		distributionGraph1 = new StatLineGraph2D("Speed Distribution");
		distributionGraph1.restoreWorldSettings();
		distributionGraph1.initialize();

		distributionGraph2 = new StatLineGraph2D("Momentum Transfer Distribution");
		distributionGraph2.restoreWorldSettings();
		distributionGraph2.initialize();

		timelineGraph3 = new SlidingLineGraph2D("Pressure");
		timelineGraph3.restoreWorldSettings();
		timelineGraph3.initialize();
		timelineGraph3.addUniqueObservation("P", Color.BLUE);

		timelineGraph4 = new SlidingLineGraph2D("Collisions per Second");
		timelineGraph4.restoreWorldSettings();
		timelineGraph4.initialize();
		timelineGraph4.addUniqueObservation("Total", Color.BLUE);
		timelineGraph4.addUniqueObservation("Molecule", Color.CYAN);
		timelineGraph4.addUniqueObservation("Wall", Color.MAGENTA);

		mds.addStatGraph(distributionGraph1);
		mds.addStatGraph(distributionGraph2);
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
