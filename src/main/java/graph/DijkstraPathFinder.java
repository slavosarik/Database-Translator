package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import xmlLoader.Table;

//trieda, kde hladam najkratsie vzdialenosti medzi viacerymi vrcholmi v grafe pomocou Dijkstrovho algoritmu - hlada sa najkratsia cesta vzdy mediz dvomi vrcholmi
//ako zdroj som pouzil implementaciu Dijkstrovho algoritmu, ktoru som upravil pre nase databazove rozhranie
//ZDROJ dijkstrovho algoritmu http://pages.cs.wisc.edu/~agember/sdn/code/Dijkstra.java

public class DijkstraPathFinder {

	// robim ohodnotenie vrcholou pri hladani najkratsej cesty k danemu vrcholu
	private void computePaths(Vertex source) {
		source.setMinDistance(0);
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// susedne vrcholy pozeram
			for (VertexNeighbour n : u.getAdjacencies()) {
				Vertex v = n.getVertex();
				int weight = n.getWeight();
				int distanceThroughU = u.getMinDistance() + weight;
				if (distanceThroughU < v.getMinDistance()) {
					vertexQueue.remove(v);
					v.setMinDistance(distanceThroughU);
					v.setPrevious(u);
					vertexQueue.add(v);
				}
			}
		}
	}

	// vytvorenie cesty preiterovanim susedov vrcholu
	private List<Vertex> getShortestPathTo(Vertex target) {

		List<Vertex> path = new ArrayList<Vertex>();

		for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious())
			path.add(vertex);

		Collections.reverse(path);

		return path;
	}

	// Dijkstrov algoritmus pre ziskanie najkratsej cesty medzi zoznamom
	// tabuliek
	public List<Vertex> DijkstraAlgorithm(List<Table> tableList,
			List<String> entityNodes) {

		List<Edge> edgeList = new ArrayList<>();
		HashMap<String, Integer> edgeMap = new HashMap<String, Integer>();
		List<Vertex> vertices = new ArrayList<>();
		int count = 0;

		// nacitanie grafu -hrany + vrcholy
		for (Table table : tableList) {
			for (xmlLoader.Neighbour neighbour : table.getListOfNeighbours()) {
				edgeList.add(new Edge(table.getName(), neighbour.getTarget(), 1));
			}
		}

		for (Edge edge : edgeList) {
			if (edgeMap.get(edge.getSource()) == null) {
				edgeMap.put(edge.getSource(), count++);
				vertices.add(new Vertex(edge.getSource()));
			}
			if (edgeMap.get(edge.getTarget()) == null) {
				edgeMap.put(edge.getTarget(), count++);
				vertices.add(new Vertex(edge.getTarget()));

			}

		}

		// pridavanie susedov
		for (Edge e : edgeList) {
			vertices.get(edgeMap.get(e.getSource())).getAdjacencies()
					.add(new VertexNeighbour(vertices.get(edgeMap.get(e
							.getTarget())), e.getWeight()));

			// cesta moze viest obojsmerne
			vertices.get(edgeMap.get(e.getTarget())).getAdjacencies()
					.add(new VertexNeighbour(vertices.get(edgeMap.get(e
							.getSource())), e.getWeight()));
		}

		// priprava na permutacie - generovanie kombinacii najdenych najkratsich
		// usekov
		int pocetNajdenychEntit = entityNodes.size();
		int[][] vzdialenostiMedziEntitami = new int[pocetNajdenychEntit][pocetNajdenychEntit];
		List<List<List<Vertex>>> zoznam = new ArrayList<>();

		for (int i = 0; i < pocetNajdenychEntit; i++) {
			computePaths(vertices.get(edgeMap.get(entityNodes.get(i))));

			List<List<Vertex>> l = new ArrayList<>();
			for (int j = 0; j < pocetNajdenychEntit; j++) {

				vzdialenostiMedziEntitami[i][j] = vertices.get(edgeMap
						.get(entityNodes.get(j))).getMinDistance();

				// treba si aj najdene cesty pamatat
				List<Vertex> path = getShortestPathTo(vertices.get(edgeMap
						.get(entityNodes.get(j))));

				l.add(path);

			}

			// pridavanie ciest do zoznamu
			zoznam.add(l);

			for (Vertex v : vertices) {
				v.setMinDistance(Integer.MAX_VALUE);
				v.setPrevious(null);
			}

		}

		List<Vertex> vyslednaCesta = new ArrayList<>();

		int[] inputVerticeNumbers = new int[pocetNajdenychEntit];
		for (int i = 0; i < pocetNajdenychEntit; i++) {
			inputVerticeNumbers[i] = i;
		}
		int[] najdenaCesta = null;
		int dlzkaAktualnejCesty;
		int minimalnaDlzkaCesty = Integer.MAX_VALUE;

		ArrayList<int[]> permutationList = Permutation
				.permutations(inputVerticeNumbers);

		for (int[] permutation : permutationList) {
			dlzkaAktualnejCesty = 0;

			// moze to zhavarovat pri kratkych cestach!!!!!
			for (int i = 0; i < pocetNajdenychEntit - 1; i++) {
				dlzkaAktualnejCesty += vzdialenostiMedziEntitami[permutation[i]][permutation[i + 1]];
			}

			if (dlzkaAktualnejCesty <= minimalnaDlzkaCesty) {
				minimalnaDlzkaCesty = dlzkaAktualnejCesty;
				najdenaCesta = permutation;
			}
		}

		for (int i = 0; i < pocetNajdenychEntit - 1; i++) {

			for (Vertex v : zoznam.get(najdenaCesta[i]).get(
					najdenaCesta[(i + 1)])) {
				if (i == 0
						|| zoznam.get(najdenaCesta[i])
								.get(najdenaCesta[(i + 1)]).indexOf(v) != 0)
					vyslednaCesta.add(v);
			}

		}

		System.out.println("Vypis vyslednej cesty z Dijkstrovho algoritmu");
		for (Vertex v : vyslednaCesta) {
			System.out.println("Vrchol: " + v.getName());
		}
		System.out.println("****");

		return vyslednaCesta;
	}
}
