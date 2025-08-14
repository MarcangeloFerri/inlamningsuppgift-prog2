// PROG2 VT2025, Inlämningsuppgift, del 1
// Grupp 045
// MarcAngelo Ferri mafe1831
// Simon Sundvisson sisu5284

package se.su.inlupp;

public interface Edge<T> {

    int getWeight();

    void setWeight(int weight);

    T getDestination();

    String getName();

}
