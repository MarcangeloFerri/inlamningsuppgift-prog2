package se.su.inlupp;

import java.util.Objects;

public class EdgeClass<T> implements Edge<T> {
    private int weight;
    private final String name;
    private final T destination;

    public EdgeClass(T destination, String name, int weight) {
        this.destination = Objects.requireNonNull(destination);
        this.name = Objects.requireNonNull(name);
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public T getDestination() {
        return destination;
    }

    public String getName() {
        return name;
    }

    public void setWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.weight = weight;
    }

    public boolean equals(Object obj) {
        if(obj instanceof EdgeClass edgeC){
            return destination.equals(edgeC.destination) && name.equals(edgeC.name);
        }
        return false;
    }

    public int hashCode(){
        return Objects.hash(destination, name);
    }

    @Override
    public String toString() {
        return "till "+destination+ " med "+name+" tar "+weight;
    }
}
