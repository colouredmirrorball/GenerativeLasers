package be.cmbsoft.livecontrol.chase;

import java.util.ArrayList;
import java.util.List;

public class Step
{
    public record Coordinate(int x, int y)
    {
    }

    private final List<Coordinate> coordinates = new ArrayList<>();

    public void addCoordinate(int x, int y)
    {
        coordinates.add(new Coordinate(x, y));
    }

    public List<Coordinate> getCoordinates()
    {
        return coordinates;
    }

    public void setXForAllCoordinates(Integer newX)
    {
        coordinates.replaceAll(c -> new Coordinate(newX, c.y));
    }
}
