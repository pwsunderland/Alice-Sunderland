package sunderland.leapmotion;

import javax.vecmath.Point2d;


public class SensorTriangle {
	public Point2d v1;
	public Point2d v2;
	public Point2d v3; 
	
	// the barycentric coordinates of the triangle
	private double alpha;
	private double beta;
	private double gamma;
	
	public SensorTriangle(Point2d p1, Point2d p2, Point2d p3) {
		v1 = p1;
		v2 = p2;
		v3 = p3;
	}
	
	/**
	 * This method of determine whether a point lies within a triangle relies on the 
	 * barycentric coordinates of the triangle.
	 * Taken from: http://stackoverflow.com/questions/13300904/determine-whether-point-lies-inside-triangle
	 * @param p the point to be tested
	 * @return true if the point is in the triangle
	 */
	public boolean isPointInArea(Point2d p) {
		setAlpha(p);
		setBeta(p);
		setGamma();
		return (alpha > 0) && (beta > 0) && (gamma > 0);
	}
	
	private void setAlpha(Point2d p) {
		alpha = ((v2.y - v3.y)*(p.x - v3.x) + (v3.x - v2.x)*(p.y - v3.y)) / ((v2.y - v3.y)*(v1.x - v3.x) + (v3.x - v2.x)*(v1.y - v3.y));
	}
	
	private void setBeta(Point2d p) {
		beta = ((v3.y - v1.y)*(p.x - v3.x) + (v1.x - v3.x)*(p.y - v3.y)) / ((v2.y - v3.y)*(v1.x - v3.x) + (v3.x - v2.x)*(v1.y - v3.y));
	}
	
	private void setGamma() {
		gamma = 1.0 - alpha - beta;
	}
}