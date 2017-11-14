import java.awt.geom.Point2D;
import java.lang.Math;

public class Node {
	private Point2D point;
	private Node parent;
	private Point2D goal;
	private double h; //straight line distance from goal
	private double g; //previous path distance from start
	private double f; //h + g
	
	public Node(Point2D _point, Node _parent, Point2D _goal) {
		point = _point;
		parent = _parent;
		goal = _goal;
	}
	
	public Node(Point2D _point, Point2D _goal) {
		point = _point;
		parent = null;
		goal = _goal;
	}
	
	public void setParent(Node p) {
		parent = p;
		setG();
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setPoint(Point2D p) {
		point = p;
	}
	
	public Point2D getPoint() {
		return point;
	}
	
	public double getF() {
		return f;
	}
	
	public void setF() {
		f = getG() + getH();
	}
	
	public void setG() {
		double x = 0;
		
		if(parent == null) {
			x = 0;
		} else {
			x = distance(parent.getPoint()) + parent.getG();
		}
		
		g = x;
	}
	
	public double getG() {
		setG();
		return g;
	}
	
	public void setH() {
		h = distance(goal);
	}
	
	public double getH() {
		setH();
		return h;
	}
	
	public double distance(Point2D n) {
		double px = point.getX();
		double py = point.getY();
		double nx = n.getX();
		double ny = n.getY();
		
		double xd = (px - nx) * (px - nx);
		double yd = (py - ny) * (py - ny);
		double sum = xd + yd;
		double root = Math.sqrt(sum);
		
		return root;
	}
	
	public Point2D getGoalPoint() {
		return goal;
	}
}
