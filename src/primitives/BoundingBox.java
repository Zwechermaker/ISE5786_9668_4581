package primitives;

import java.util.List;


import static primitives.Util.isZero;

/**
 * A BoundingBox is a 3D box that is used to bound a geometry.
 * The box is aligned with the axes of the coordinate system.
 *
 * @author Dan
 */
public class BoundingBox {
    /**
     * The minimum point of the box.
     */
    public Point min;
    /**
     * The maximum point of the box.
     */
    public Point max;

    /**
     * Constructor for BoundingBox
     *
     * @param min The minimum point of the box.
     * @param max The maximum point of the box.
     */
    public BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Constructor for BoundingBox
     *
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param z1 The z coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @param z2 The z coordinate of the second point.
     */
    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(new Point(x1, y1, z1), new Point(x2, y2, z2));
    }

    /**
     * Constructor for BoundingBox
     *
     * @param points The points to be bounded by the box.
     */
    public BoundingBox(Point... points) {
        if (points.length == 0) {
            throw new IllegalArgumentException("Cannot create a bounding box for an empty set of points.");
        }
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (Point p : points) {
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
            if (p.getZ() < minZ) {
                minZ = p.getZ();
            }
            if (p.getX() > maxX) {
                maxX = p.getX();
            }
            if (p.getY() > maxY) {
                maxY = p.getY();
            }
            if (p.getZ() > maxZ) {
                maxZ = p.getZ();
            }
        }
        this.min = new Point(minX, minY, minZ);
        this.max = new Point(maxX, maxY, maxZ);
    }

    /**
     * Checks if the ray intersects the box.
     *
     * @param ray The ray to check.
     * @return True if the ray intersects the box, false otherwise.
     */
    public boolean intersects(Ray ray) {
        double tmin = (min.getX() - ray.origin().getX()) / ray.direction().getX();
        double tmax = (max.getX() - ray.origin().getX()) / ray.direction().getX();

        if (tmin > tmax) {
            double temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        double tymin = (min.getY() - ray.origin().getY()) / ray.direction().getY();
        double tymax = (max.getY() - ray.origin().getY()) / ray.direction().getY();

        if (tymin > tymax) {
            double temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return false;
        }

        if (tymin > tmin) {
            tmin = tymin;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        double tzmin = (min.getZ() - ray.origin().getZ()) / ray.direction().getZ();
        double tzmax = (max.getZ() - ray.origin().getZ()) / ray.direction().getZ();

        if (tzmin > tzmax) {
            double temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }

        if (tzmin > tmin) {
            tmin = tzmin;
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        return true;
    }

    /**
     * Returns the center of the bounding box.
     *
     * @return The center of the bounding box.
     */
    public Point getCenter() {
        return new Point((min.getX() + max.getX()) / 2, (min.getY() + max.getY()) / 2, (min.getZ() + max.getZ()) / 2);
    }
}
