package com.company;

public class Edge implements Comparable {

    private final Vertex origin;
    private final Vertex destination;
    private final int weight;

    public Edge(Vertex origin, Vertex destination, int weight) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getOrigin() {
        return origin;
    }


    @Override
    public int compareTo(Object o) { //for Priority Queue

        if (!(o instanceof Edge)) {
            throw new ClassCastException("Cannot compare non-Edge object to Edge");
        }

        return this.weight - ((Edge) o).weight;
    }

    @Override
    public String toString() {
        return "" + origin + "->" + destination + " (" + weight + ")";
    }
}
