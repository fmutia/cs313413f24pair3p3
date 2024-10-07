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
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape shape : g.getShapes()) {
            Location l = shape.accept(this);
            minX = Math.min(minX, l.getX());
            minY = Math.min(minY, l.getY());
            maxX = Math.max(maxX, l.getX() + l.getShape().accept(new BoundingBox()).getX());
            maxY = Math.max(maxY, l.getY() + l.getShape().accept(new BoundingBox()).getY());
        }
        return new Location(minX, minY, new Rectangle(maxX - minX, maxY - minY));
    }

    @Override
    public Location onLocation(final Location l) {
        Location l2 = l.getShape().accept(this);
        return new Location(l.getX() + l2.getX(), l.getY() + l2.getY(), l2.getShape());
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

        if (points.isEmpty()) {
            return new Location(0, 0, new Rectangle(0, 0));
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : points) {
            int x = point.getX();
            int y = point.getY();

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        int width = maxX - minX;
        int height = maxY - minY;

        return new Location(minX, minY, new Rectangle(width, height));
    }
}
