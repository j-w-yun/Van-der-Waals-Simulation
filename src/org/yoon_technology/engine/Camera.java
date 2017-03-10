package org.yoon_technology.engine;

import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Camera {

	public static final int ORTHOGRAPHIC_PROJECTION = 0;
	public static final int PERSPECTIVE_PROJECTION = 1;

	private Vector3d position;
	private Vector3d velocity;
	private Vector3d orientation;
	private int mode;
	private boolean rotatable;

	public Camera() {
		this(0, 0, 0);
	}

	public Camera(double x, double y, double z) {
		this(new Vector3d(x, y, z), ORTHOGRAPHIC_PROJECTION);
	}

	// Funnel here
	public Camera(Vector3d origin, int mode) {
		reset();
		this.mode = mode;
		this.position.set(origin);
		rotatable = true;
	}

	public void setRotatable(boolean rotate) {
		this.rotatable = rotate;
	}

	public Vector3d getOrientation() {
		return orientation;
	}

	public void setOrientation(Vector3d orientation) {
		this.orientation = orientation;
	}

	public Vector3d getPosition() {
		return position;
	}

	public void setPosition(Vector3d position) {
		this.position = position;
	}

	public void reset() {
		this.position = new Vector3d(0.0, 0.0, 0.0);
		this.velocity = new Vector3d(0.0, 0.0, 0.0);
		this.orientation = new Vector3d(0.0, 0.0, 0.0);
	}

	public Vector3d project(Vector3d position, double screenWidth, double screenHeight) {

		if(mode == ORTHOGRAPHIC_PROJECTION) {
			Vector3d objPos = new Vector3d(0.0, 0.0, 0.0);
			objPos.set(position);

			objPos.set(objPos.mul(new Vector3d(1.0, -1.0, 1.0)));

			// Translating this first allows rotation about screen's 0, 0 and not at the origin of the graph
			objPos.set(objPos.sub(new Vector3d(this.position.getX(), -this.position.getY(), this.position.getZ())));

			if(rotatable) {
				objPos.set(objPos.rotate(new Vector3d(1.0, 0.0, 0.0), getOrientation().getX()));
				objPos.set(objPos.rotate(new Vector3d(0.0, 1.0, 0.0), getOrientation().getY()));
				objPos.set(objPos.rotate(new Vector3d(0.0, 0.0, 1.0), getOrientation().getZ()));
			}

			return objPos.add(new Vector3d((int)(screenWidth / 2.0), (int)(screenHeight / 2.0), 0.0)); // Center at 0, 0

		} else if(mode == PERSPECTIVE_PROJECTION) {

			Vector3d objPos = new Vector3d(0.0, 0.0, 0.0);
			objPos.set(position);

			objPos.set(objPos.mul(new Vector3d(1.0, -1.0, 1.0)));

			// Translating this first allows rotation about screen's 0, 0 and not at the origin of the graph
			//			objPos.set(objPos.sub(new Vector3d(this.position.getX(), -this.position.getY(), this.position.getZ())));



			double aspectRatio = (double)screenWidth / (double)screenHeight;

			// TODO
			double fov = 45;

			// Camera's distance to projection plane
			double distance = 1.0 / (Math.tan(Math.toRadians(fov) / 2.0));

			// Calculate projection x and y from vec3
			double xProjection = objPos.getX() / (aspectRatio * objPos.getZ() * (Math.tan(Math.toRadians(fov) / 2.0)));
			double yProjection = objPos.getY() / (objPos.getZ() * (Math.tan(Math.toRadians(fov) / 2.0)));

			objPos.set(new Vector3d(xProjection, yProjection, 0.0));

			objPos.set(objPos.sub(new Vector3d(this.position.getX(), -this.position.getY(), this.position.getZ())));
			objPos.set(objPos.rotate(new Vector3d(1.0, 0.0, 0.0), getOrientation().getX()));
			objPos.set(objPos.rotate(new Vector3d(0.0, 1.0, 0.0), getOrientation().getY()));
			objPos.set(objPos.rotate(new Vector3d(0.0, 0.0, 1.0), getOrientation().getZ()));
			objPos.set(objPos.add(new Vector3d((int)(screenWidth / 2.0), (int)(screenHeight / 2.0), 0.0))); // Center at 0, 0

			return objPos;
		}

		return null;
	}
}
