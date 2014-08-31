package graph;

import java.util.ArrayList;
import java.util.List;

//trieda oznacujuca vrchol v grafe
public class Vertex implements Comparable<Vertex> {

	private int id;
	private final String name;
	private List<VertexNeighbour> adjacencies;
	private int minDistance = Integer.MAX_VALUE;
	private Vertex previous;

	public Vertex(String argName) {
		name = argName;
		this.adjacencies = new ArrayList<>();
	}

	public String toString() {
		return name;
	}

	public int compareTo(Vertex other) {
		return Integer.compare(minDistance, other.minDistance);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<VertexNeighbour> getAdjacencies() {
		return adjacencies;
	}

	public void setAdjacencies(List<VertexNeighbour> adjacencies) {
		this.adjacencies = adjacencies;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	public Vertex getPrevious() {
		return previous;
	}

	public void setPrevious(Vertex previous) {
		this.previous = previous;
	}

	public String getName() {
		return name;
	}

	
}