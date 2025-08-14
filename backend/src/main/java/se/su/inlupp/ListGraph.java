// PROG2 VT2025, Inlämningsuppgift, del 1
// Grupp 045
// MarcAngelo Ferri mafe1831
// Simon Sundvisson sisu5284

package se.su.inlupp;

import java.util.*;

// Implementerar all funktionalitet från GRAPH-interface
public class ListGraph<T> implements Graph<T> {
    private Map<T, Set<Edge<T>>> adjList = new HashMap<>();

    @Override
    public void add(T node) {
        adjList.putIfAbsent(node, new HashSet<>());
    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {
        if (!adjList.containsKey(node1) || !adjList.containsKey(node2)) {
            throw new NoSuchElementException("One or both nodes dont exist");
        }
        add(node1);
        add(node2);

        if (weight < 0) {
            throw new IllegalArgumentException("Weight can not be negativ");
        }

        if (getEdgeBetween(node1, node2) != null) {
            throw new IllegalStateException("A edge already exists");
        }

        Set<Edge<T>> fromNodes = adjList.get(node1);
        Set<Edge<T>> toNodes = adjList.get(node2);

        fromNodes.add(new EdgeClass(node2, name, weight));
        toNodes.add(new EdgeClass(node1, name, weight));

    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }

        if (!adjList.containsKey(node1) || !adjList.containsKey(node2)) {
            throw new NoSuchElementException("One or both nodes do not exist in the graph");
        }

        boolean found1 = false;
        for (Edge e : adjList.get(node1)) {
            if (e.getDestination().equals(node2)) {
                e.setWeight(weight);
                found1 = true;
                break;
            }
        }

        boolean found2 = false;
        for (Edge e : adjList.get(node2)) {
            if (e.getDestination().equals(node1)) {
                e.setWeight(weight);
                found2 = true;
                break;
            }
        }

        if (!found1 || !found2) {
            throw new NoSuchElementException("No edge exists between the nodes");
        }
    }

    @Override
    public Set<T> getNodes() {
        return new HashSet<>(adjList.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {
        if (!adjList.containsKey(node)) {
            throw new NoSuchElementException("Node does not exist in the graph");
        }
        return new HashSet<>(adjList.get(node));
    }

    //Hittar och returnerar kanten mellan två noder
    @Override
    public Edge<T> getEdgeBetween(T node1, T node2) {
        if (!adjList.containsKey(node1) || !adjList.containsKey(node2)) {
            throw new NoSuchElementException("One or both nodes dont exist");
        }
        Set<Edge<T>> edges = adjList.get(node1);
        for (Edge e : edges) {
            if (e.getDestination().equals(node2)) {
                return e;
            }
        }
        return null;
    }

    //Tar bort kanten mellan två noder
    @Override
    public void disconnect(T node1, T node2) {
        if (!adjList.containsKey(node1) || !adjList.containsKey(node2)) {
            throw new NoSuchElementException("One or both nodes are missing in the graph.");
        }

        Edge<T> edge1 = getEdgeBetween(node1, node2);
        Edge<T> edge2 = getEdgeBetween(node2, node1);

        if (edge1 == null || edge2 == null) {
            throw new IllegalStateException("No edge exists between the given nodes.");
        }

        adjList.get(node1).remove(edge1);
        adjList.get(node2).remove(edge2);
    }

    //Tar bort en vald nod och alla dess kanter i graph
    @Override
    public void remove(T node) {
        if (!adjList.containsKey(node)) {
            throw new NoSuchElementException("Node does not exist in the graph.");
        }

        for (Edge<T> edge : adjList.get(node)) {
            T destination = edge.getDestination();
            adjList.get(destination).removeIf(e -> e.getDestination().equals(node));
        }

        adjList.remove(node);
    }

    //konntrolerar om det finns en kant mellan två noder
    @Override
    public boolean pathExists(T from, T to) {
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) {
            return false;
        }

        Set<T> visited = new HashSet<>();
        return recursiveVisitAll(from, to, visited);
    }

    //Provar alla vägar recursivt för att se om det finns en väg mellan from och to
    private boolean recursiveVisitAll(T from, T to, Set<T> visited) {
        visited.add(from);
        if (from.equals(to)) {   //om from = to så har vi hittat rätt direkt
            return true;
        }
        for (Edge<T> e : adjList.get(from)) {   //annars går vi igenom alla kanter och utgår från from, tills vi hittar rätt
            if (!visited.contains(e.getDestination())) {
                if (recursiveVisitAll(e.getDestination(), to, visited)) {
                    return true;
                }
            }
        }
        return false; //Bryter om vi inte kan finna en väg mellan from och to
    }

    // BFS för att hitta kortaste vägen baserat på antal kanter
    @Override
    public List<Edge<T>> getPath(T from, T to) {
        if (!adjList.containsKey(from) || !adjList.containsKey(to)) {
            return null;
        }

        if (from.equals(to)) {
            return new ArrayList<>();
        }

        // BFS för kortaste väg baserat på antal hop
        Queue<T> queue = new LinkedList<>();
        Map<T, T> parent = new HashMap<>();

        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            T current = queue.poll();

            if (current.equals(to)) {
                // Bygger vägen bakåt
                LinkedList<Edge<T>> path = new LinkedList<>();
                T node = to;

                while (parent.get(node) != null) {
                    T parentNode = parent.get(node);
                    Edge<T> edge = getEdgeBetween(parentNode, node);
                    path.addFirst(edge);
                    node = parentNode;
                }
                return path;
            }

            for (Edge<T> edge : adjList.get(current)) {
                T neighbor = edge.getDestination();
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return null; // Om ingen väg hittades
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (T node : adjList.keySet()) {
            result.append(node).append("\n");
        }

        for (T node : adjList.keySet()) {
            for (Edge<T> edge : adjList.get(node)) {
                result.append(edge.toString()).append("\n");
            }
        }

        return result.toString();
    }


    // Används inte pga att vi använder BFS
    private void recursiveConnect(T to, T from, Map<T, T> connection) {
        connection.put(to, from);
        for (Edge<T> e : adjList.get(to)) {
            if (!connection.containsKey(e.getDestination())) {
                recursiveConnect(e.getDestination(), to, connection);
            }
        }

    }


}