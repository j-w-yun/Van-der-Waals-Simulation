package org.yoon_technology.math;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Vector3d extends Vector2d {

	private static final long serialVersionUID = 11L;
	private double Z;

	public Vector3d(double X, double Y, double Z) {
		super(X, Y);
		this.Z = Z;
	}

	@Override
	public double length() {
		return Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z);
	}

	@Override
	public double max() {
		return Math.max(this.X, Math.max(this.Y, this.Z));
	}

	public double dot(Vector3d r) {
		return this.X * r.getX() + this.Y * r.getY() + this.Z * r.getZ();
	}

	public Vector3d cross(Vector3d r) {
		double x_ = this.Y * r.getZ() - this.Z * r.getY();
		double y_ = this.Z * r.getX() - this.X * r.getZ();
		double z_ = this.X * r.getY() - this.Y * r.getX();

		return new Vector3d(x_, y_, z_);
	}

	@Override
	public Vector3d normalized() {
		double length = length();

		if(length == 0) {
			length += 0.00000001;
		}

		return new Vector3d(this.X / length, this.Y / length, this.Z / length);
	}

	public Vector3d rotate(Vector3d axis, double angle) {
		double sinAngle = Math.sin(-angle);
		double cosAngle = Math.cos(-angle);

		return this.cross(axis.mul(sinAngle)).add(						// Rotation on X
				(this.mul(cosAngle)).add(								// Rotation on Z
						axis.mul(this.dot(axis.mul(1.0 - cosAngle)))));	// Rotation on Y
	}

	public Vector3d add(Vector3d r) {
		return new Vector3d(this.X + r.getX(), this.Y + r.getY(), this.Z + r.getZ());
	}

	@Override
	public Vector3d add(double r) {
		return new Vector3d(this.X + r, this.Y + r, this.Z + r);
	}

	public Vector3d sub(Vector3d r) {
		return new Vector3d(this.X - r.getX(), this.Y - r.getY(), this.Z - r.getZ());
	}

	@Override
	public Vector3d sub(double r) {
		return new Vector3d(this.X - r, this.Y - r, this.Z - r);
	}

	public Vector3d mul(Vector3d r) {
		return new Vector3d(this.X * r.getX(), this.Y * r.getY(), this.Z * r.getZ());
	}

	@Override
	public Vector3d mul(double r) {
		return new Vector3d(this.X * r, this.Y * r, this.Z * r);
	}

	public Vector3d div(Vector3d r) {
		return new Vector3d(this.X / r.getX(), this.Y / r.getY(), this.Z / r.getZ());
	}

	@Override
	public Vector3d div(double r) {
		return new Vector3d(this.X / r, this.Y / r, this.Z / r);
	}

	@Override
	public Vector3d abs() {
		return new Vector3d(Math.abs(this.X), Math.abs(this.Y), Math.abs(this.Z));
	}

	@Override
	public String toString() {
		return "(" + this.X + " " + this.Y + " " + this.Z + ")";
	}

	public Vector3d set(double x, double y, double z) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		return this;
	}

	public Vector3d set(Vector3d r) {
		set(r.getX(), r.getY(), r.getZ());
		return this;
	}

	@Override
	public double getX() {
		return this.X;
	}

	@Override
	public void setX(double x) {
		this.X = x;
	}

	@Override
	public double getY() {
		return this.Y;
	}

	@Override
	public void setY(double y) {
		this.Y = y;
	}

	public double getZ() {
		return this.Z;
	}

	public void setZ(double z) {
		this.Z = z;
	}

	public boolean equals(Vector3d r) {
		return this.X == r.getX() && this.Y == r.getY() && this.Z == r.getZ();
	}
}
