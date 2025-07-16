package se.su.inlupp;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Place extends Circle{
    private String name;
    private boolean selected;

    public Place (String name, double x, double y) {
        super(x,y,5);
        this.name = name;
        this.selected = false;
        this.setFill(Color.BLUE);

    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (this.selected) {
            setFill(Color.GREEN);
        } else {
            setFill(Color.BLUE);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
