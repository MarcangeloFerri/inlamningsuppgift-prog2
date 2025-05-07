package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {
    private Map<T, Set<Edge<T>>> adjList = new HashMap<>();

    @Override
    public void add(T node) {
        adjList.putIfAbsent(node, new HashSet<>());
    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {
        if(!adjList.containsKey(node1) || !adjList.containsKey(node2)){
            throw new NoSuchElementException("One or both nodes dont exist");
        }
        add(node1);
        add(node2);

        if(weight < 0){
            throw new IllegalArgumentException("Weight can´t be negativ");
        }

        if(getEdgeBetween(node1, node2) != null){
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

    @Override
    public Edge<T> getEdgeBetween(T node1, T node2) {
        if(!adjList.containsKey(node1) || !adjList.containsKey(node2)){
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

    @Override
    public boolean pathExists(T from, T to) {
        if(!adjList.containsKey(from) || !adjList.containsKey(to)) {
            return false;
        }

        Set<T> visited = new HashSet<>();
        return recursiveVisitAll(from, to, visited);
    }

    private boolean recursiveVisitAll(T from, T to, Set<T> visited) {
        visited.add(from);
        if (from.equals(to)) {
            return true;
        }
        for (Edge<T> e : adjList.get(from)) {
            if (!visited.contains(e.getDestination())) {
                if (recursiveVisitAll(e.getDestination(), to, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {
        Map<T, T> connection = new HashMap<>();
        recursiveConnect(from, null, connection);

        if(!connection.containsKey(to)){
            return null;
        }

        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = to;

        while (current != null && !current.equals(from)) {
            T next = connection.get(current);
            Edge e = getEdgeBetween(next, current);
            path.addFirst(e);
            current = next;
        }
        return path;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // First, add all nodes
        for (T node : adjList.keySet()) {
            result.append(node).append("\n");
        }

        // Then add all edges
        for (T node : adjList.keySet()) {
            for (Edge<T> edge : adjList.get(node)) {
                result.append(edge.toString()).append("\n");
            }
        }

        return result.toString();
    }



    private void recursiveConnect(T to, T from, Map<T, T> connection) {
        connection.put(to, from);
        for (Edge<T> e : adjList.get(to)) {
            if (!connection.containsKey(e.getDestination())) {
                recursiveConnect(e.getDestination(), to, connection);
            }
        }

    }

/*
    public Collection<Edge> getShortestPath(T from, T to) {
        Map<T, T> connection = new HashMap<>();
        connection.put(from, null);

        LinkedList<T> queue = new LinkedList<>();
        queue.add(from);
        while (!queue.isEmpty()) {
            T current = queue.pollFirst();
            for (Edge<T> e : adjList.get(current)) {
                T next = e.getDestination();
                if (!connection.containsKey(next)) {
                    connection.put(next, current);
                    queue.add(next);
                }

            }
        }
        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = to;
        while(current != null && !current.equals(from)){
          T next = connection.get(current);
          Edge<T> e =getEdgeBetween(next, current);
          current = next;
          path.addFirst(e);
        }
        return null;
    }

*/
}