package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;

import java.util.List;
import java.util.Objects;
import primitives.Vector;
/**
 * A class that represents a cylinder in space.
 */
public final class Cylinder extends Tube{
    /**
     * The height of the cylinder.
     */
    private final double _height;

    /**
     * a plane at the top of the cylinder for ray-intersections purposes
     */
    private final Plane _topPlane;

    /**
     * a plane at the bottom of the cylinder for ray-intersections purposes
     */
    private final Plane _bottomPlane;

    /**
     * a point at the center of the top base for ray-intersection purposes.
     */
    private final Point _topCenter;

    /**
     * A parameter constructor for a cylinder
     * @param radius the radius of the cylinder
     * @param axis   the axis of the cylinder
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, primitives.Ray axis, double height) {
        super(radius, axis);
        if (primitives.Util.alignZero(height) <= 0) {
            throw new IllegalArgumentException("Cylinder height must be greater than 0");
        }
        this._height = height;

        _topCenter = axis.getPoint(height);
        _topPlane = new Plane(_topCenter, axis.direction().scale(-1));
        _bottomPlane = new Plane(_axis.origin(), axis.direction());
    }

    /**
     * Helper method to check if a ray intersects a cylinder cap.
     * @param capPlane the plane of the cap
     * @param capCenter the center point of the cap
     * @param ray the ray to check
     * @param maxDistance limits the distance we are looking for intersections in.
     * @return the intersection point if valid. if it isn't: null.
     */
    private Point getDiscIntersection(Plane capPlane, Point capCenter, Ray ray, double maxDistance) {
        // The new API returns List<Intersection>. A ray intersects a plane at most once.
        List<Point> hits = capPlane.findIntersections(ray);
        if (hits == null || hits.isEmpty()) {
            return null;
        }

        Point p = hits.get(0);

        if (Util.alignZero(p.distance(ray.origin()) - maxDistance) > 0) {
            return null;
        }
        // check if the intersection isn't too far from the axis.
        if (Util.alignZero(p.distanceSquared(capCenter) - _radiusSquared) < 0) {
            return p;
        }
        return null;
    }
    @Override
    public List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // Get intersections from the infinite tube body
        List<Intersection> lst = super.calcIntersectionsHelper(ray, maxDistance);

        Point[] foundIntersections = new Point[2];
        int count = 0;

        if (lst != null) {
            for (Intersection intersection : lst) {
                Point point = intersection.point;
                double height = point.subtract(_axis.origin()).dotProduct(_axis.direction()); // distance from bottom base

                if (Util.alignZero(height) >= 0 && Util.alignZero(height - this._height) <= 0) {
                    foundIntersections[count++] = point;
                }
            }
        }

        if (count < 2) {
            Point pBottom = getDiscIntersection(_bottomPlane, _axis.origin(), ray, maxDistance);
            if (pBottom != null) {
                foundIntersections[count++] = pBottom;
            }
        }

        if (count < 2) {
            Point pTop = getDiscIntersection(_topPlane, _topCenter, ray, maxDistance);
            if (pTop != null) {
                foundIntersections[count++] = pTop;
            }
        }

        if (count == 0) {
            return null;
        }
        if (count == 1) {
            return List.of(new Intersection(this, foundIntersections[0]));
        }

        // return sorted by distance from ray origin.
        if (foundIntersections[0].distanceSquared(ray.origin()) > foundIntersections[1].distanceSquared(ray.origin())) {
            return List.of(new Intersection(this,foundIntersections[1]),
                    new Intersection(this, foundIntersections[0]));
        }
        return List.of(new Intersection(this,foundIntersections[0]),
                new Intersection(this, foundIntersections[1]));
    }

    @Override
    public String toString() {
        return super.toString() + ", height: " + _height;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        if (!super.equals(obj)) return false; // if there is inheritance
        Cylinder cylinder = (Cylinder) obj;
        return Util.isZero(_height - cylinder._height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _height);
    }


    @Override
    public Vector getNormal(Point point){
        if (point.equals(_axis.origin())) {
            return _axis.direction().scale(-1).normalize();
        }

        double projection = _axis.direction().dotProduct(point.subtract(_axis.origin()));

        if (Util.isZero(projection)) {
            return _axis.direction().scale(-1).normalize();
        } else if (Util.isZero(projection - _height)) {
            return _axis.direction().normalize();
        } else {
            // Pass the already calculated projection down to the parent
            return super.getNormal(point, projection);
        }
    }
}
