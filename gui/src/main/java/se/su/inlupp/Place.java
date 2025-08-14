// PROG2 VT2025, Inlämningsuppgift, del 2
// Grupp 045
// MarcAngelo Ferri mafe1831
// Simon Sundvisson sisu5284

package se.su.inlupp;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Place extends Circle {
    private String name;
    private boolean selected;


    public Place(String name, double x, double y) {
        super(x, y, 5);
        this.name = name;
        this.setFill(Color.BLUE);
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return getCenterX();
    }

    public double getY() {
        return getCenterY();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (this.selected) {
            setFill(Color.RED);
        } else {
            setFill(Color.BLUE);
        }
    }

}
