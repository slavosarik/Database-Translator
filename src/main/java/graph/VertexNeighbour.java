package graph;

//trieda, ktora oznacuje susedny vrchol v grafe spolu s vzdialenostou k nemu - ohodnotenie hrany
public class VertexNeighbour {

	private int weight;
	private Vertex vertex;

	public VertexNeighbour(Vertex v, int w) {
		this.setVertex(v);
		this.setWeight(w);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

}
