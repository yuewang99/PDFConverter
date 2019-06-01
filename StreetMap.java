
/* CSC 172
 * Project 3
 * Yue Wang 30602434
 * Lab MW 2:00
 * Bowen Fu 30812468
 * Lab MW 3:25
 * 11/28/2018*/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StreetMap {
	public static Node[] nodeList;
	public static int sourceIndex;
	public static HashMap<String, Integer> indexMap = new HashMap<String, Integer>();
//	public static double maxlongitude, minlongitude, maxlatitude, minlatitude;
	
	public static class Node {
		Color c;// if the node has been visited
		public Double d;// distance from the root
		public int n;// index of the node
		public String name;// name of the node
		public int parent;// index of the parent
		public Double latitude;
		public Double longitude;
		public ArrayList<Integer> neighbors;
		public ArrayList<Double> distances;// distance from the vertex to the neighbor vertices stored in the neighbors
											// list

		public Node(int n) {
			this.c = Color.WHITE; // haven't been visited
			this.n = n;
			this.d = Double.MAX_VALUE;// impossible distance for any reachable vertex
			this.parent = Integer.MAX_VALUE;
			neighbors = new ArrayList<Integer>();
			distances = new ArrayList<Double>();
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return n;
		}

		public String getName() {
			return name;
		}

		public ArrayList<Integer> getNeighbors() {
			return neighbors;
		}

		public ArrayList<Double> getDistances() {
			return distances;
		}

		public void addNeighbor(int newNode, Double dis) {
			this.neighbors.add(newNode);
			this.distances.add(dis);
		}

		public void setLatitude(Double d) {
			this.latitude = d;
		}

		public void setLongitude(Double d) {
			this.longitude = d;
		}

		public Double getLatitude() {
			return this.latitude;
		}

		public Double getLongitude() {
			return this.longitude;
		}

		public void setDist(Double d) {
			this.d = d;
		}

		public Double getDist() {
			return d;
		}

		public void setColor(Color c) {
			this.c = c;
		}

		public void setParent(int index) {
			this.parent = index;
		}

		public Color getColor() {
			return c;
		}

	}

	public static class Graph extends JPanel {
		private static final long serialVersionUID = 1L;

		public double maxlongitude, minlongitude, maxlatitude, minlatitude;
		public double scalelong, scalelat;
		public double width, height;
		public boolean shtpath;
		public Node[] node;
		public String[] name;
		public HashMap<String, Integer> indexMap;

		public Graph(Node[] node) {
			this.node = node;
			shtpath = false;
		}

		public Graph(Node[] node, String[] name, HashMap<String, Integer> indexMap) {
			this.node = node;
			this.name = name;
			this.indexMap = indexMap;
			shtpath = true;
		}

		public double getwidth() {
			this.width = getWidth();
			return width;
		}

		public double getheight() {
			this.height = getHeight();
			return height;
		}

		public Node[] scalegraph(Node[] node) {
			Node[] scale = node;
			maxlongitude = node[0].getLongitude();
			minlongitude = node[0].getLongitude();
			maxlatitude = node[0].getLatitude();
			minlatitude = node[0].getLatitude();
			for (int i = 0; i < node.length; i++) {
				if (node[i].getLongitude() > maxlongitude) {
					maxlongitude = node[i].getLongitude();
				}
				if (node[i].getLongitude() < minlongitude) {
					minlongitude = node[i].getLongitude();
				}
				if (node[i].getLatitude() > maxlatitude) {
					maxlatitude = node[i].getLatitude();
				}
				if (node[i].getLatitude() < minlatitude) {
					minlatitude = node[i].getLatitude();
				}
			}

			scalelat = getheight() / (maxlatitude - minlatitude);
			scalelong = getwidth() / (maxlongitude - minlongitude);
			for (int i = 0; i < node.length; i++) {
				double newlat = Math.abs(node[i].getLatitude() - minlatitude) * scalelat;
				double newlong = Math.abs(node[i].getLongitude() - minlongitude) * scalelong;
				scale[i].setLatitude(newlat);
				scale[i].setLongitude(newlong);
				scale[i].setColor(Color.WHITE);
			}
			return scale;
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			Node[] scale = scalegraph(node);
			for (int i = 0; i < scale.length; i++) {
				ArrayList<Integer> adj = scale[i].neighbors;
				scale[i].setColor(Color.BLACK);
				double x1 = scale[i].getLongitude();
				double y1 = getheight() - scale[i].getLatitude();
				for (int j = 0; j < adj.size(); j++) {
					if (scale[adj.get(j)].getColor() != Color.BLACK) {
						double x2 = scale[adj.get(j)].getLongitude();
						double y2 = getheight() - scale[adj.get(j)].getLatitude();
						g2.draw(new Line2D.Double(x1, y1, x2, y2));
					}
				}
			}
			if (shtpath) {
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(10));
				for (int i = 0; i < name.length - 1; i++) {
					int index1 = indexMap.get(name[i]);
					int index2 = indexMap.get(name[i + 1]);
					double x1 = scale[index1].getLongitude();
					double y1 = getheight() - scale[index1].getLatitude();
					double x2 = scale[index2].getLongitude();
					double y2 = getheight() - scale[index2].getLatitude();
					g2.draw(new Line2D.Double(x1, y1, x2, y2));
				}
			}

		}

	}

	// initialization
	public static void ini(int Vn) {
		nodeList = new Node[Vn];
		for (int i = 0; i < nodeList.length; i++) {
			Node node = new Node(i);
			nodeList[i] = node;
		}
	}

	public static int minVertex(ArrayList<Node> list) {
		Double minDist = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			if ((list.get(i).getDist() < minDist)) {
				minDist = list.get(i).getDist();
				index = i;
			}
		}
		int name = list.get(index).getIndex();
		list.remove(index);
		return name;

	}

	public static Double findWeight(int i, int j) {
		Double dx = nodeList[i].getLatitude() - nodeList[j].getLatitude();
		Double dy = nodeList[i].getLongitude() - nodeList[j].getLongitude();
		return Math.sqrt(dx * dx + dy * dy);
	}

	// assign distance
	public static void findDist() {
		nodeList[sourceIndex].setDist(0.0);
		ArrayList<Node> nodeCollection = new ArrayList<Node>();
		for (int i = 0; i < nodeList.length; i++) {
			nodeCollection.add(nodeList[i]);
		}
		while (nodeCollection.size() > 0) {
			int minIndex = minVertex(nodeCollection);
			for (int i = 0; i < nodeList[minIndex].getDistances().size(); i++) {
				int neighbor = nodeList[minIndex].getNeighbors().get(i);
				if (nodeList[minIndex].getDist() != Integer.MAX_VALUE && nodeList[neighbor]
						.getDist() > nodeList[minIndex].getDistances().get(i) + nodeList[minIndex].getDist()) {
					nodeList[neighbor].setDist(nodeList[minIndex].getDistances().get(i) + nodeList[minIndex].getDist());
					nodeList[neighbor].setParent(minIndex);
				}
			}
		}
	}

	public static int visits(int t) {
		int visit = 0;
		for (int i = 1; i < nodeList.length; i++) {
			if (nodeList[i].getDist() <= t) {
				visit = visit + 1;
			}
		}
		return visit;
	}

	public static String[] path(Node n) {
		Node node = n;
		ArrayList<String> path = new ArrayList<String>();
		while (node.parent != Integer.MAX_VALUE) {
			path.add(nodeList[node.parent].getName());
			node = nodeList[node.parent];
		}
		// reverse the order
		String[] pathPrint = new String[path.size() + 1];
		for (int i = path.size() - 1; i >= 0; i--) {
			pathPrint[i] = path.get(path.size() - 1 - i);
		}
		pathPrint[pathPrint.length - 1] = n.getName();

		return pathPrint;
	}

	public static void main(String[] args) throws IOException {
		boolean showgraph = false;
		boolean showdir = false;
		Graph map;
		String file = args[0];
		int arglength = args.length;
		String[] intersections = new String[2];
		int intersectionIndex = 0;
		for (int i = 1; i < args.length; i++) {
			if (args[i].substring(0, 2).equals("--")) {// graphics commands
				if (args[i].equals("--show")) {
					showgraph = true;
				} else if (args[i].equals("--directions")) {
					showdir = true;
				}
				arglength = arglength - 1;

			} else {
				intersections[intersectionIndex] = args[i];
				intersectionIndex = intersectionIndex + 1;
			}
		}
		if (arglength != 3) {
			intersections[0] = "i1";
			intersections[1] = "i1";
		}
		FileReader fileReader = new FileReader(file);
		Scanner scanner = new Scanner(fileReader);
		int intersectNum = 0;
		int roadNum = 0;
		Double minLongitude = Double.MAX_VALUE;
		Double minLatitude = Double.MAX_VALUE;
		Double maxLongitude = -1*Double.MAX_VALUE;
		Double maxLatitude = Double.MIN_VALUE;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.isEmpty()) {
				String[] nums = line.split("\\s+");
				if (nums[0].equals("i")) {// intersection
					if (nums[1].equals(intersections[0])) {
						sourceIndex = intersectNum;// as the source
					}
					if (nums[0].equals("i")) {// intersection
						Double latitude;
						Double longitude;
						if (nums[2].substring(0, 1).equals("-")) {// negative
							latitude = Double.parseDouble(nums[2].substring(1, nums[2].length()));
							latitude = -1 * latitude;
							if (latitude<minLatitude) {
								minLatitude = latitude;
							} else if (latitude>maxLatitude) {
								maxLatitude = latitude;
							}
						} else {// positive
							latitude = Double.parseDouble(nums[2]);
							if (latitude<minLatitude) {
								minLatitude = latitude;
							} else if (latitude>maxLatitude) {
								maxLatitude = latitude;
							}
						}
						if (nums[3].substring(0, 1).equals("-")) {// negative
							longitude = Double.parseDouble(nums[3].substring(1, nums[3].length()));
							longitude = -1 * longitude;
							if (longitude<minLongitude) {
								minLongitude = longitude;
							} else if (longitude>maxLongitude) {
								maxLongitude = longitude;
							}
						} else {// positive
							longitude = Double.parseDouble(nums[3]);
							if (longitude<minLongitude) {
								minLongitude = longitude;
							} else if (longitude>maxLongitude) {
								maxLongitude = longitude;
							}
						}
					intersectNum = intersectNum + 1;
				}
				}}}
			

		System.out.println(minLongitude);
		System.out.println(minLatitude);
		System.out.println(maxLongitude);
		System.out.println(maxLatitude);
			
		ini(intersectNum);
		fileReader = new FileReader(file);
		scanner = new Scanner(fileReader);
		int index = -1;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.isEmpty()) {
				String[] nums = line.split("\\s+");
				if (nums[0].equals("i")) {// intersection
					index = index + 1;
					String name = nums[1];// name of the vertex
					Double latitude;
					Double longitude;
					if (nums[2].substring(0, 1).equals("-")) {// negative
						latitude = Double.parseDouble(nums[2].substring(1, nums[2].length()));
						latitude = -1 * latitude;
					} else {// positive
						latitude = Double.parseDouble(nums[2]);
					}
					if (nums[3].substring(0, 1).equals("-")) {// negative
						longitude = Double.parseDouble(nums[3].substring(1, nums[3].length()));
						longitude = -1 * longitude;
					} else {// positive
						longitude = Double.parseDouble(nums[3]);
					}
					nodeList[index].setName(name);
					nodeList[index].setLatitude(latitude);
					nodeList[index].setLongitude(longitude);
					indexMap.put(name, index);// add the name-index pair into the map
				} else if (nums[0].equals("r")) {// road
					String v1 = nums[2];// vertex1
					String v2 = nums[3];// vertex2
					int i1 = indexMap.get(v1);// index of vertex1
					int i2 = indexMap.get(v2);// index of vertex2
					Double weight = findWeight(i1, i2);
					boolean added = false;
					int i = 0;
					while ((i < nodeList[i1].getNeighbors().size()) && (!added)) {// only add new roads
						if (nodeList[i1].getNeighbors().get(i) == i2) {
							added = true;
						}
						i++;
					}
					if (!added) {
						nodeList[i1].addNeighbor(i2, weight);// undirected graph
						nodeList[i2].addNeighbor(i1, weight);// undirected graph
					}
				}

			}
		}

		nodeList[sourceIndex].setDist(0.0);
		findDist();// assign min distance to all intersections
		int destinationIndex = indexMap.get(intersections[1]);
		Node node = nodeList[destinationIndex];
		String[] path = path(node);
		// print out the path nodes in reverse order
		if (showdir) {
			for (String s : path) {
				System.out.println(s);
			}
		}
		System.out.println(indexMap.get(intersections[1]));
		Double distance = nodeList[destinationIndex].getDist();
		System.out.println(distance);

		if (showdir) {
			map = new Graph(nodeList, path, indexMap);
		} else {
			map = new Graph(nodeList);
		}
		JFrame window = new JFrame("Map");
		map.setSize(500, 700);
		window.add(map);
		window.setSize(500, 700);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(showgraph);
	}

}
