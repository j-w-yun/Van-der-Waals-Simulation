package org.yoon_technology.engine;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.yoon_technology.gui.Display;
import org.yoon_technology.gui.Window;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Engine {

	public static final double SECONDS_PER_UPDATE = 1.0 / 120.0;
	private ArrayList<Display> displays;
	private ArrayList<World> worlds;
	private Camera camera;
	private boolean running;
	private boolean paused;
	private boolean focusComputation;
	private boolean rotate = true;

	private int fpsCounter = 0;
	private int upsCounter = 0;

	public Engine() {
		displays = new ArrayList<>();
		worlds = new ArrayList<>();
	}

	public void addDisplay(Display display) {
		this.displays.add(display);
	}

	public void addWorld(World world) {
		this.worlds.add(world);
	}

	public void addEmptyWorld() {
		this.addWorld(World.createEmptyWorld());
	}

	// TODO Ortho, Perspective
	public void addCamera(Vector3d position, int mode) {
		camera = new Camera(position, mode);
	}

	public ArrayList<Display> getDisplay() {
		return displays;
	}

	public Camera getCamera() {
		return camera;
	}

	public ArrayList<World> getWorld() {
		return worlds;
	}

	// Returns false if engine was already paused
	public boolean pause() {
		return paused != true ? paused = true : false;
	}

	// Returns false if engine was not paused before
	public boolean resume() {
		return paused == true ? !(paused = false) : false;
	}

	public void rotateOn(boolean rotate) {
		this.rotate = rotate;
	}

	public boolean isRotateOn() {
		return rotate;
	}

	public boolean focusComputation() {
		return focusComputation != true ? focusComputation = true : false;
	}

	public boolean focusVisual() {
		return focusComputation == true ? !(focusComputation = false) : false;
	}

	public void start() {
		running = true;

		long laggingTime = System.nanoTime();
		long leadingTime = System.nanoTime();
		double delta = (leadingTime - laggingTime) / 1000000000.0;

		double rotationCounter = 0.0;

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				for(Display display : displays) {
					display.setDisplayInformation("U:" + upsCounter + "|F:" + fpsCounter);
				}
				upsCounter = 0;
				fpsCounter = 0;
			}
		};
		timer.schedule(task, 0, 1000);

		double systemSeconds = 0.0;

		// Main loop
		while(running) {

			delta += (leadingTime - laggingTime) / 1000000000.0;
			laggingTime = leadingTime;
			leadingTime = System.nanoTime();

			boolean idle = true;
			//			if(focusComputation)
			//				idle = false;

			while(delta >= SECONDS_PER_UPDATE) {
				Window.input(this, SECONDS_PER_UPDATE);

				if(!paused) {
					update(SECONDS_PER_UPDATE);
					upsCounter++;

					systemSeconds += SECONDS_PER_UPDATE;
					if(systemSeconds >= 1.0) {
						for(World world : worlds) {
							world.sendSecondTick();
						}
						systemSeconds = 0;
					}
				}

				delta -= SECONDS_PER_UPDATE;

				// Rock the graph about slowly
				if(rotate) {
					rotationCounter += SECONDS_PER_UPDATE;
					camera.setOrientation(
							new Vector3d(Math.cos(rotationCounter / 16.0) * 2.0, -Math.sin(rotationCounter / 16.0) * 2.0, 0.0));
				}

				if(!focusComputation) {
					render();
					fpsCounter++;
				}
				idle = false;
			}
			if(focusComputation) {
				render();
				fpsCounter++;
			}

			// Sleep if undemanding
			if(idle)
				try { Thread.sleep(10); } catch (InterruptedException e) {}
		}
	}

	private void update(double timePassed) {
		for(World world : worlds) {
			world.update(timePassed);
		}
	}

	private void render() {
		for(Display display : displays) {
			display.render();
		}
	}
}
