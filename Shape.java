import java.util.ArrayList;

public class Shape {
	private ArrayList<Node> nodes;
	
	public Shape() {
		nodes = new ArrayList<Node>();
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public Node[] getNodes() {
		Node[] n = new Node[nodes.size()];
		
		for(int i = 0; i < n.length; i++) {
			n[i] = nodes.get(i);
		}
		
		return n;
	}
}
