package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * A class representing a finite cylinder in 3D space, defined by a central axis, a radius, and a height.
 * <p>
 * A cylinder is a {@link Tube} with a finite height, capped by two circular bases.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Cylinder extends Tube {
    /**
     * The height of the cylinder.
     */
    private final double _height;

    /**
     * A plane representing the top cap of the cylinder, used for intersection calculations.
     */
    private final Plane _topPlane;

    /**
     * A plane representing the bottom cap of the cylinder, used for intersection calculations.
     */
    private final Plane _bottomPlane;

    /**
     * The center point of the top base of the cylinder.
     */
    private final Point _topCenter;

    /**
     * Constructs a {@link Cylinder} with a specified radius, axis, and height.
     *
     * @param radius The radius of the cylinder. Must be a positive value.
     * @param axis   The central axis of the cylinder.
     * @param height The height of the cylinder. Must be a positive value.
     * @throws IllegalArgumentException if the height is not a positive number.
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        if (Util.alignZero(height) <= 0) {
            throw new IllegalArgumentException("Cylinder height must be greater than 0.");
        }
        this._height = height;

        _topCenter = axis.getPoint(height);
        _topPlane = new Plane(_topCenter, axis.direction().scale(-1));
        _bottomPlane = new Plane(_axis.origin(), axis.direction());
    }

    /**
     * Helper method to find the intersection of a ray with a circular cylinder cap.
     *
     * @param capPlane    The plane of the cap.
     * @param capCenter   The center point of the cap.
     * @param ray         The ray to check for intersection.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return The intersection point if it is valid (i.e., on the circular disc), otherwise {@code null}.
     */
    private Point getDiscIntersection(Plane capPlane, Point capCenter, Ray ray, double maxDistance) {
        // A ray can intersect a plane at most once.
        List<Point> hits = capPlane.findIntersections(ray, maxDistance);
        if (hits == null || hits.isEmpty()) {
            return null;
        }

        Point p = hits.get(0);

        // Check if the intersection point is within the circular cap.
        if (Util.alignZero(p.distanceSquared(capCenter) - _radiusSquared) < 0) {
            return p;
        }
        return null;
    }

    /**
     * Calculates the intersection points of a ray with the cylinder.
     * <p>
     * The method considers intersections with both the cylindrical body and the two circular end caps.
     * It involves these steps:
     * <ol>
     *   <li>Find intersections with the infinite tube that forms the cylinder's body.</li>
     *   <li>Filter these intersections to include only those that lie within the finite height of the cylinder.</li>
     *   <li>Find intersections with the top and bottom circular caps.</li>
     *   <li>Combine and return the valid intersection points, sorted by distance from the ray's origin.</li>
     * </ol>
     *
     * @param ray         The ray to intersect with the cylinder.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of {@link Intersection} points, or {@code null} if no intersections are found.
     */
    @Override
    public List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // Get intersections from the infinite tube body.
        List<Intersection> lst = super.calcIntersectionsHelper(ray, maxDistance);

        Point[] foundIntersections = new Point[2];
        int count = 0;

        if (lst != null) {
            for (Intersection intersection : lst) {
                Point point = intersection.point;
                // Calculate the projection of the intersection point onto the cylinder's axis
                // to determine its height relative to the bottom base.
                double height = point.subtract(_axis.origin()).dotProduct(_axis.direction());

                // Check if the intersection is within the cylinder's height.
                if (Util.alignZero(height) >= 0 && Util.alignZero(height - this._height) <= 0) {
                    foundIntersections[count++] = point;
                }
            }
        }

        // If we haven't found two intersections yet, check the caps.
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

        // Return the intersections sorted by distance from the ray's origin.
        if (foundIntersections[0].distanceSquared(ray.origin()) > foundIntersections[1].distanceSquared(ray.origin())) {
            return List.of(new Intersection(this, foundIntersections[1]),
                    new Intersection(this, foundIntersections[0]));
        }
        return List.of(new Intersection(this, foundIntersections[0]),
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
        if (!super.equals(obj)) return false;
        Cylinder cylinder = (Cylinder) obj;
        return Util.isZero(_height - cylinder._height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _height);
    }

    /**
     * Calculates the normal vector to the cylinder at a given point.
     * <p>
     * If the point is on one of the circular caps, the normal is the axis direction (or its negative).
     * If the point is on the cylindrical body, the normal is calculated as it would be for an infinite tube.
     *
     * @param point The point on the surface of the cylinder.
     * @return The normal vector at the specified point.
     */
    @Override
    public Vector getNormal(Point point) {
        // If the point is at the center of the bottom base.
        if (point.equals(_axis.origin())) {
            return _axis.direction().scale(-1).normalize();
        }

        double projection = _axis.direction().dotProduct(point.subtract(_axis.origin()));

        // If the point is on the bottom cap.
        if (Util.isZero(projection)) {
            return _axis.direction().scale(-1).normalize();
        }
        // If the point is on the top cap.
        else if (Util.isZero(projection - _height)) {
            return _axis.direction().normalize();
        }
        // If the point is on the cylindrical body.
        else {
            // Pass the already calculated projection to the parent class to avoid recalculation.
            return super.getNormal(point, projection);
        }
    }
}
