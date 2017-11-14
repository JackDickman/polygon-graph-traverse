package assignment3;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		System.out.println("**Enter values as if your polygons were on a graph, each vertex...");
		System.out.println("**...having an x,y coordinate relative to the initial state at 0,0\n");

		Scanner s1 = new Scanner(System.in);
		Scanner s2 = new Scanner(System.in);

		System.out.println("Number of polygons: ");
		int numShapes = s1.nextInt();

		Shape[] shapes = new Shape[numShapes];

		ArrayList<Node> nodeList = new ArrayList<Node>();
		Node[] nodesArray;

		System.out.println("X coordinate of goal: ");
		int goalx = s1.nextInt();
		System.out.println("Y coordinate of goal: ");
		int goaly = s1.nextInt();
		Point2D goalPoint = new Point2D.Double(goalx, goaly);

		Node start = new Node(new Point2D.Double(0, 0), goalPoint);
		Node goal = new Node(goalPoint, goalPoint);

		ArrayList<Line2D> linesList = new ArrayList<Line2D>();; //all lines = edges + diagonals
		Line2D[] linesArray; //all lines = edges + diagonals

		ArrayList<Node> path = new ArrayList<Node>();
		ArrayList<Line2D> temp;

		long begin, end, time;

		System.out.println("\n**Enter coordinates of vertices for each polygon");
		System.out.println("**Enter \"next\" to move to the next polygon or to finish\n");

		String input = "";
		int x, y;

		for(int n = 0; n < numShapes; n++) {
			shapes[n] = new Shape();

			do {
				System.out.println("Shape " + (n+1) + ", X:");
				input = s2.nextLine();

				if(!input.equals("next")) {
					x = Integer.parseInt(input);
					System.out.println("Shape " + (n+1) + ", Y:");
					input = s2.nextLine();
					y = Integer.parseInt(input);
					Node blah = new Node(new Point2D.Double(x, y), goalPoint);
					nodeList.add(blah);
					shapes[n].addNode(blah);
					System.out.println("(" + x + ", " + y + ")");
				}
			} while(!input.equals("next"));

			temp = makeLinesPerShape(shapes[n].getNodes());
			linesList.addAll(temp);
			temp.clear();
		}

		System.out.println();

		nodeList.add(goal);

		nodesArray = new Node[nodeList.size()];

		for(int j = 0; j < nodeList.size(); j++) {
			nodesArray[j] = nodeList.get(j);
		}

		linesArray = new Line2D[linesList.size()];

		for(int i = 0; i < linesList.size(); i++) {
			linesArray[i] = linesList.get(i);
		}

		System.out.println("--- A* ---");

		begin = System.nanoTime();

		path = aStar(start, goal, nodesArray, linesArray);

		end = System.nanoTime();
		time = (end - begin);

		printPath(path, time);

		System.out.println("\n--- Greedy ---");

		begin = System.nanoTime();

		path = greedy(start, goal, nodesArray, linesArray);

		end = System.nanoTime();
		time = (end - begin);

		printPath(path, time);


		s1.close();
		s2.close();
	}

	//returns the path found using a greedy algorithm, picking the node with the lowest H value
	public static ArrayList<Node> greedy(Node start, Node goal, Node[] nodes, Line2D[] lines) {
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		ArrayList<Node> path = new ArrayList<Node>();
		Node current = start;

		open.add(start);

		while(open.size() > 0) {
			int i = findSmallestH(open);
			current = open.get(i);
			open.remove(i);
			closed.add(current);

			if(current.getPoint() == goal.getPoint()) {
				break;
			}

			for(int j = 0; j < nodes.length; j++) {
				if(closed.contains(nodes[j]) || checkIntersections(current, nodes[j], lines)) {
					j = j + 1 - 1;
				} else {
					if(!open.contains(nodes[j])) {
						nodes[j].setParent(current);
						nodes[j].setF();
						open.add(nodes[j]);
					} else { //nodes[j] is in open
						nodes[j].setParent(current);
						nodes[j].setF();
					}
				}
			}
		}

		path.add(current);
		while(current.getParent() != null) {
			current = current.getParent();
			path.add(current);
		}

		return path;
	}

	//returns the path found using the A* algorithm as an ArrayList
	public static ArrayList<Node> aStar(Node start, Node goal, Node[] nodes, Line2D[] lines) {
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		ArrayList<Node> path = new ArrayList<Node>();
		Node current = start;

		open.add(start);

		while(open.size() > 0) {
			int i = findSmallestF(open);
			current = open.get(i);
			open.remove(i);
			closed.add(current);

			if(current.getPoint() == goal.getPoint()) {
				break;
			}

			for(int j = 0; j < nodes.length; j++) {
				if(closed.contains(nodes[j]) || checkIntersections(current, nodes[j], lines)) {
					j = j + 1 - 1;
				} else {
					if(!open.contains(nodes[j])) {
						nodes[j].setParent(current);
						nodes[j].setF();
						open.add(nodes[j]);
					} else { //nodes[j] is in open
						Node test = new Node(nodes[j].getPoint(), nodes[j].getGoalPoint());
						test.setParent(current);
						test.setF();
						if(test.getF() < nodes[j].getF()) {
							nodes[j].setParent(current);
							nodes[j].setF();
						}
					}
				}
			}
		}

		path.add(current);
		while(current.getParent() != null) {
			current = current.getParent();
			path.add(current);
		}

		return path;
	}

	//returns ArrayList<Line2D> containing all of the edges and diagonals in the graph
	public static ArrayList<Line2D> makeLinesPerShape(Node[] nodes) {
		ArrayList<Line2D> lines = new ArrayList<Line2D>();

		for(int i = 0; i <= nodes.length - 2; i++) {
			for(int j = i + 1; j < nodes.length; j++) {
				lines.add(new Line2D.Double(nodes[i].getPoint(), nodes[j].getPoint()));
			}
		}

		return lines;
	}

	//prints solution/path in order from start -> goal
	public static void printPath(ArrayList<Node> path, long time) {
		System.out.println("Path represented as coordinates of vertices:");

		for(int i = path.size() - 1; i >= 0; i--) {
			System.out.println("(" + path.get(i).getPoint().getX() + ", " + path.get(i).getPoint().getY() + ")");
		}

		System.out.println("Total G Cost: " + path.get(0).getG());
		System.out.println("Time Elapsed: " + time + " nanoseconds");
	}

	//returns index of Node with smallest f value in open ArrayList
	public static int findSmallestH(ArrayList<Node> open) {
		int index = 0;
		Node blah = open.get(index);
		double h = blah.getH();

		for(int i = 1; i < open.size(); i++) {
			if(open.get(i).getH() < h) {
				index = i;
				h = open.get(i).getH();
			} else if(open.get(i).getH() == h) {
				index = i;
				h = open.get(i).getH();
			}
		}

		return index;
	}

	//returns index of Node with smallest f value in open ArrayList
	public static int findSmallestF(ArrayList<Node> open) {
		int index = 0;
		Node blah = open.get(index);
		double h = blah.getH();
		double f = blah.getF();

		for(int i = 1; i < open.size(); i++) {
			if(open.get(i).getF() < f) {
				index = i;
				f = open.get(i).getF();
				h = open.get(i).getH();
			} else if(open.get(i).getF() == f && open.get(i).getH() < h) {
				index = i;
				f = open.get(i).getF();
				h = open.get(i).getH();
			}
		}

		return index;
	}

	//returns true if the straight line created by Nodes a and b intersects with any of the lines/edges in arr
	public static boolean checkIntersections(Node a, Node b, Line2D[] arr) {
		boolean bool = false;

		Point2D apoint = a.getPoint();
		Point2D bpoint = b.getPoint();
		Line2D line = new Line2D.Double(apoint, bpoint);

		for(int i = 0; i < arr.length; i++) {
			Point2D p1 = arr[i].getP1();
			Point2D p2 = arr[i].getP2();

			//if line a->b shares a point with arr[i], don't count as intersection
			if(apoint.equals(p1) || apoint.equals(p2) || bpoint.equals(p1) || bpoint.equals(p2)) {
				if(line.intersectsLine(arr[i])) {
					bool = false;
				}
			} else {
				if(line.intersectsLine(arr[i])) {
					return true;
				}
			}
		}

		return bool;
	}
}
