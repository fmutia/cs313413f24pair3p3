package edu.luc.etl.cs313.android.shapes.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import java.util.List;
import edu.luc.etl.cs313.android.shapes.model.*;

/**
 * A Visitor for drawing a shape to an Android canvas.
 */
public class Draw implements Visitor<Void> {

    // TODO entirely your job (except onCircle)

    private final Canvas canvas;

    private final Paint paint;

    public Draw(final Canvas canvas, final Paint paint) {
        this.canvas = canvas; // FIXME
        this.paint = paint; // FIXME
        paint.setStyle(Style.STROKE);
    }

    @Override
    public Void onCircle(final Circle c) {
        canvas.drawCircle(0, 0, c.getRadius(), paint);
        return null;
    }

    @Override
    public Void onStrokeColor(final StrokeColor c) {
        paint.setColor(c.getColor());

        c.getShape().accept(this);

        paint.setColor(c.getColor());
        paint.setStyle(Paint.Style.STROKE);
        return null;
    }

    @Override
    public Void onFill(final Fill f) {
        Paint.Style oldStyle = paint.getStyle();

        paint.setStyle(Style.FILL_AND_STROKE);
        f.getShape().accept(this);

        paint.setStyle(oldStyle);
        return null;
    }

    @Override
    public Void onGroup(final Group g) {
        for (Shape s: g.getShapes()) {
            s.accept(this);
        }
        return null;
    }

    @Override
    public Void onLocation(final Location l) {
        canvas.save();
        canvas.translate(l.getX(), l.getY());
        l.getShape().accept(this);
        canvas.translate(-l.getX(), -l.getY());
        return null;
    }

    @Override
    public Void onRectangle(final Rectangle r) {
        canvas.drawRect(0, 0, r.getWidth(), r.getHeight(), paint);
        return null;
    }

    @Override
    public Void onOutline(final Outline o) {
        paint.setStyle(Style.STROKE);
        o.getShape().accept(this);
        paint.setStyle(Style.STROKE);
        return null;
    }

    @Override
    public Void onPolygon(final Polygon s) {
        List<? extends Point> points = s.getPoints();

        if (points.size() > 1) {
            float[] pts = new float[(points.size() - 1) * 4];
            int i = 0;
            for (int j = 0; j < points.size() - 1; j++) {
                Point p1 = points.get(j);
                Point p2 = points.get(j + 1);

                pts[i++] = p1.getX();
                pts[i++] = p1.getY();
                pts[i++] = p2.getX();
                pts[i++] = p2.getY();
            }


            canvas.drawLines(pts, paint);


            Point first = points.get(0);
            Point last = points.get(points.size() - 1);
            canvas.drawLine(last.getX(), last.getY(), first.getX(), first.getY(), paint);
        }

        return null;
    }
}
