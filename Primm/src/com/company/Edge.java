package com.company;

public class Edge implements Comparable<Edge> {

    private final Vertex origin;
    private final Vertex destination;
    private final int weight;

    public Edge(Vertex origin, Vertex destination, int weight) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public Vertex getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Edge edge) {
        return this.weight - edge.weight;
    }

    @Override
    public String toString() {
        return origin + "->" + destination + " (" + weight + ")";
    }
}
