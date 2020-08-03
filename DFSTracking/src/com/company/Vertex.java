package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vertex {

    private final String name;
    private final List<Vertex> connections;
    private boolean visited; //Not used here

    public Vertex(String name) {
        this.name = name;
        connections = new ArrayList<>();
    }

    public List<Vertex> getConnections() {
        return connections;
    }

    public String getName() {
        return name;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void addConnections(Vertex... vertices) {
        connections.addAll(Arrays.asList(vertices));
    }

    @Override
    public String toString() {
        return this.name;
    }
}
