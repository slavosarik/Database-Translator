package graph;

//trieda reprezentujuca hranu v grafe urcenu cudzim klucom, ktorym sa napaja hrana na 2 vrcholy - 2 entity tabulky
public class Edge {

	private String source;
	private int weight;
	private String target;

	public Edge(String source, String target, int weigth) {
		this.source = source;
		this.target = target;
		this.weight = weigth;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}