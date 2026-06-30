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
     * Checks if the ray intersects the box using the optimized slab method.
     * This implementation is allocation-free and handles axis-parallel rays.
     *
     * @param ray The ray to check.
     * @return True if the ray intersects the box, false otherwise.
     */
    public boolean intersects(Ray ray) {
        double tMin = 0.0;
        double tMax = Double.POSITIVE_INFINITY;

        Point rayP0 = ray.origin();
        Vector rayDir = ray.direction();

        // Slab test for X dimension
        double dirX = rayDir.getX();
        if (Math.abs(dirX) < 1e-6) { // Ray is parallel to X plane
            if (rayP0.getX() < min.getX() || rayP0.getX() > max.getX()) {
                return false; // Parallel and outside of the slab
            }
        } else {
            double t1 = (min.getX() - rayP0.getX()) / dirX;
            double t2 = (max.getX() - rayP0.getX()) / dirX;
            if (t1 > t2) {
                double temp = t1;
                t1 = t2;
                t2 = temp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }

        // Slab test for Y dimension
        double dirY = rayDir.getY();
        if (Math.abs(dirY) < 1e-6) { // Ray is parallel to Y plane
            if (rayP0.getY() < min.getY() || rayP0.getY() > max.getY()) {
                return false; // Parallel and outside of the slab
            }
        } else {
            double t1 = (min.getY() - rayP0.getY()) / dirY;
            double t2 = (max.getY() - rayP0.getY()) / dirY;
            if (t1 > t2) {
                double temp = t1;
                t1 = t2;
                t2 = temp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }

        // Slab test for Z dimension
        double dirZ = rayDir.getZ();
        if (Math.abs(dirZ) < 1e-6) { // Ray is parallel to Z plane
            if (rayP0.getZ() < min.getZ() || rayP0.getZ() > max.getZ()) {
                return false; // Parallel and outside of the slab
            }
        } else {
            double t1 = (min.getZ() - rayP0.getZ()) / dirZ;
            double t2 = (max.getZ() - rayP0.getZ()) / dirZ;
            if (t1 > t2) {
                double temp = t1;
                t1 = t2;
                t2 = temp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
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
