package pragma.rocks.dataLit.container;

public class Edge {
	int from;
	int to;
	String label;

	public Edge() {

	}

	public Edge(int from, int to, String label) {
		this.from = from;
		this.to = to;
		this.label = label;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
