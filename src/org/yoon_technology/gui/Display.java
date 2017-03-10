package org.yoon_technology.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ConcurrentModificationException;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.yoon_technology.engine.Engine;
import org.yoon_technology.engine.World;
import org.yoon_technology.engine.WorldObject;
import org.yoon_technology.engine.WorldObjectProperty;
import org.yoon_technology.engine.WorldText;
import org.yoon_technology.math.Function;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Display extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 3L;
	private Engine engine;
	private World world;
	private String displayInformation;
	private double scale;

	public Display(int width, int height, Engine engine) {
		super(new FlowLayout(SwingConstants.CENTER));
		this.engine = engine;
		engine.addDisplay(this);
		setFocusable(true);
		setPreferredSize(new Dimension(width, height));

		addComponentListener(this);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setDisplayInformation(String displayInformation) {
		this.displayInformation = displayInformation;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getScale() {
		return scale;
	}

	public void render() {
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// Antialiasing
		Graphics2D g2 = (Graphics2D)g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		g2.setRenderingHints(rh);

		// TODO Concurrent

		// Draw points
		try {
			WorldObject lastObject = null;	// For lines

			for(WorldObject object : world.getObjects()) {
				Color objectColor = Color.GREEN; // Default color
				for(Object property : object.getProperties()) {
					if(property instanceof Color) {
						objectColor = (Color)property;
					}
				}

				double radius = object.getRadius() * scale;

				Vector3d projectionVector = engine.getCamera().project(
						object.getPosition().mul(scale),
						(int)((double)getWidth()),			// offset x
						(int)((double)getHeight()));		// offset y

				if(object.getVelocity() != null) {
					double factor = Function.clamp(10000.0/object.getVelocity().dot(object.getVelocity()), 0.4, 1.0);
					int red = (int) Function.clamp(factor * objectColor.getRed() * 2.0 + 50, 55.0, 250.0);
					int green = (int) Function.clamp(factor * objectColor.getGreen() * 2.0 + 50, 55.0, 250.0);
					int blue = (int) Function.clamp(factor * objectColor.getBlue() * 2.0 + 50, 55.0, 250.0);
					g.setColor(new Color(red, green, blue));
				} else {
					g.setColor(objectColor);
				}

				if(object.getProperties().getDrawMode() == WorldObjectProperty.POINTS) {
					if(radius < 1)
						radius = 1.0;

					g.fillOval((int)(projectionVector.getX() - radius),
							(int)(projectionVector.getY() - radius),
							(int)(radius*2.0),
							(int)(radius*2.0));

				} else if(object.getProperties().getDrawMode() == WorldObjectProperty.LINES) {
					if(lastObject != null) {
						Vector3d projectionVector2 = engine.getCamera().project(
								lastObject.getPosition().mul(scale),
								(double)getWidth(),			// offset x to center at 0, 0
								(double)getHeight());		// offset y

						g.drawLine((int) projectionVector.getX(),
								(int) projectionVector.getY(),
								(int) projectionVector2.getX(),
								(int) projectionVector2.getY());
					}
					lastObject = object;

				} else if(object.getProperties().getDrawMode() == WorldObjectProperty.END_LINES) {
					if(lastObject == null) {
						if(radius < 1)
							radius = 2.0;

						g.drawOval((int)(projectionVector.getX() - radius),
								(int)(projectionVector.getY() - radius),
								(int)(radius*2.0),
								(int)(radius*2.0));

					} else {
						Vector3d projectionVector2 = engine.getCamera().project(
								lastObject.getPosition().mul(scale),
								(double)getWidth(),			// offset x to center at 0, 0
								(double)getHeight());		// offset y

						g.drawLine((int) projectionVector.getX(),
								(int) projectionVector.getY(),
								(int) projectionVector2.getX(),
								(int) projectionVector2.getY());
					}
					lastObject = null;
				}

			}

			// Draw text
			for(WorldText text : world.getTexts()) {
				Color textColor = Color.GREEN; // Default color
				for(Object property : text.getProperties()) {
					if(property instanceof Color) {
						textColor = (Color)property;
					}
				}
				g.setFont(text.getFont());


				Vector3d projectionVector = engine.getCamera().project(
						text.getPosition().mul(scale),
						(double)getWidth(),
						(double)getHeight());

				double factor = Function.clamp(projectionVector.normalized().getZ(), 0.6, 1.0);
				int red = (int) Function.clamp(factor * textColor.getRed() + 50, 100.0, 200.0);
				int green = (int) Function.clamp(factor * textColor.getGreen() + 50, 100.0, 200.0);
				int blue = (int) Function.clamp(factor * textColor.getBlue() + 50, 100.0, 200.0);
				g.setColor(new Color(red, green, blue));

				g.drawString(text.getText(), (int) projectionVector.getX(), (int) projectionVector.getY());
			}

			if(displayInformation != null) {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
				g.drawString(displayInformation, 10, 15);
			}
		} catch (ConcurrentModificationException cme) {} // TODO
	}

	@Override
	public void componentResized(ComponentEvent e) {
		render();
		for(World world : engine.getWorld()) {
			this.scale = world.resized(getWidth(), getHeight());
		}
	}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
}
