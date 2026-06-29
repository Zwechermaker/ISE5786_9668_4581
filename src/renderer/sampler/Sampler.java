package renderer.sampler;

import primitives.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class for sampling strategies used in anti-aliasing.
 * <p>
 * This class defines the basic structure for different sampling techniques,
 * which are used to generate multiple sample points within a single pixel
 * to reduce aliasing artifacts.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public abstract class Sampler {
    /**
     * The horizontal resolution of the sampling grid.
     */
    protected final int _resolutionX;
    /**
     * The vertical resolution of the sampling grid.
     */
    protected final int _resolutionY;

    /**
     * Constructs a {@link Sampler} with a specified resolution.
     *
     * @param resolutionX The horizontal resolution.
     * @param resolutionY The vertical resolution.
     */
    public Sampler(int resolutionX, int resolutionY) {
        this._resolutionX = resolutionX;
        this._resolutionY = resolutionY;
    }

    /**
     * Constructs a {@link Sampler} with a uniform resolution for both axes.
     *
     * @param resolution The resolution for both width and height.
     */
    public Sampler(int resolution) {
        this(resolution, resolution);
    }

    /**
     * Generates a 2D offset for a given pixel.
     * Subclasses must implement this method to provide a specific sampling strategy.
     *
     * @param column The column index of the pixel.
     * @param row    The row index of the pixel.
     * @return A {@link Point2D} representing the offset.
     */
    public abstract Point2D getOffset(int column, int row);

    /**
     * A unified mathematical helper for calculating normalized offsets on a 2D plane.
     * This prevents DRY violations between different grid-based samplers.
     *
     * @param column The column index of the pixel.
     * @param row    The row index of the pixel.
     * @param shiftX The horizontal shift (0.0 to 1.0) applied within the sub-pixel.
     * @param shiftY The vertical shift (0.0 to 1.0) applied within the sub-pixel.
     * @return A {@link Point2D} representing the calculated offset.
     */
    protected Point2D calculateOffset(int column, int row, double shiftX, double shiftY) {
        double xOffset = (column + shiftX) / _resolutionX - 0.5;
        double yOffset = -(row + shiftY) / _resolutionY + 0.5;
        return new Point2D(xOffset, yOffset);
    }

    /**
     * Generates a list of all sample offsets for the entire grid.
     *
     * @return A {@link List} of {@link Point2D} offsets.
     */
    public List<Point2D> generateAll() {
        List<Point2D> list = new ArrayList<>();
        for (int i = 0; i < _resolutionY; i++) {
            for (int j = 0; j < _resolutionX; j++) {
                list.add(getOffset(j, i));
            }
        }
        return list;
    }
}