package edu.indiana.pragma.container;

import java.util.List;

public class Provenance {
	private List<Node> nodes;
	private List<Edge> edges;

	public Provenance() {

	}

	public Provenance(List<Node> nodes, List<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
}
