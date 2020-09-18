package com.company;

public class Edge implements Comparable<Edge> {

    private final Vertex origin;
    private final Vertex destination;
    private final double weight;

    public Edge(Vertex origin, Vertex destination, double weight) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Edge edge) {
        return Double.compare(weight, edge.getWeight());
    }

    @Override
    public String toString() {
        return origin + "->" + destination +
                " (" + weight + ')';
    }
}
