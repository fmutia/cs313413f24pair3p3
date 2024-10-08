package edu.luc.etl.cs313.android.shapes.model;
import java.util.List;
/**
 * A shape visitor for calculating the bounding box, that is, the smallest
 * rectangle containing the shape. The resulting bounding box is returned as a
 * rectangle at a specific location.
 */
public class BoundingBox implements Visitor<Location> {

    // TODO entirely your job (except onCircle)

    @Override
    public Location onCircle(final Circle c) {
        final int radius = c.getRadius();
        return new Location(-radius, -radius, new Rectangle(2 * radius, 2 * radius));
    }

    @Override
    public Location onFill(final Fill f) {
        return f.getShape().accept(this);
    }

    @Override
    public Location onGroup(final Group g) {

        int[] bounds = findGroupBounds(g.getShapes());
        return new Location(bounds[0], bounds[1], new Rectangle(bounds[2] - bounds[0], bounds[3] - bounds[1]));


    }

    @Override
    public Location onLocation(final Location l) {
        Location shapeLoc = l.getShape().accept(this);
        return new Location(l.getX() + shapeLoc.getX(), l.getY() + shapeLoc.getY(), shapeLoc.getShape());
    }

    @Override
    public Location onRectangle(final Rectangle r) {
        return new Location(0, 0, r);
    }

    @Override
    public Location onStrokeColor(final StrokeColor c) {
        return c.getShape().accept(this);
    }

    @Override
    public Location onOutline(final Outline o) {
       return o.getShape().accept(this);
    }

    @Override
    public Location onPolygon(final Polygon s) {
        List<? extends Point> points = s.getPoints();


        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;


        for (Point point : points) {
            int x = point.getX();
            int y = point.getY();
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }


        return new Location(minX, minY, new Rectangle(maxX - minX, maxY - minY));
    }

    private int[] findGroupBounds(List<? extends Shape> shapes) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape s : shapes) {
            Location loc = s.accept(this);
            Rectangle bbox = (Rectangle) loc.getShape();
            int x = loc.getX();
            int y = loc.getY();
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + bbox.getWidth());
            maxY = Math.max(maxY, y + bbox.getHeight());
        }

        return new int[]{minX, minY, maxX, maxY};
    }



}
