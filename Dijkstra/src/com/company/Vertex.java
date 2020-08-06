package com.company;

import java.util.*;

public class Vertex implements Comparable {

    private final String name;
    private final HashMap<Vertex, Double> neighbours; //Neighbour Vertex key, edge distance value
    private Vertex predecessor;
    private Double distance; //Object type for POSITIVE_INFINITY use
    private boolean visited;
    private final int index; //Vertices stored in array

    public Vertex(String name) {
        this.name = name;
        index = Integer.parseInt(name); //Vertex name is same as array index number here
        neighbours = new HashMap<>();
        distance = Double.POSITIVE_INFINITY;
    }

    public String getName() {
        return name;
    }

    public int getIndex() { return index; }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public Vertex getPredecessor() {
        return predecessor;
    }

    public HashMap<Vertex, Double> getNeighbours() {
        return neighbours;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setPredecessor(Vertex predecessor) {
        this.predecessor = predecessor;
    }

    public void addNeighbour(Vertex neighbour, double distanceToNeighbour) {
        neighbours.putIfAbsent(neighbour, distanceToNeighbour);
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void printNeighbours() {
        for (Vertex v : neighbours.keySet()) {
            System.out.print(v + " ");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        return this.name + " (" + distance + ")";
    }

    @Override
    public int compareTo(Object o) {

        if (!(o instanceof Vertex)) {
            throw new RuntimeException("Cannot compare non Vertex object with Vertex");
        }

        Vertex other = (Vertex) o;

        return (int) (distance - other.getDistance());

    }

    public String printChain() {

        StringBuilder chain = new StringBuilder(this.name + " ");
        Vertex predecessor = this.getPredecessor();

        while (predecessor != null) {
            chain.insert(0, predecessor.name + " ");
            predecessor = predecessor.getPredecessor();
        }

//        System.out.println(chain);

        return chain.toString();
    }


}
