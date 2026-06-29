package renderer.sampler;

import primitives.Point2D;

/**
 * A sampling strategy that uses a regular grid pattern to generate sample points within a pixel.
 * <p>
 * This sampler places sample points at the center of each sub-pixel in a uniform grid.
 * Mathematically equivalent to a Jittered grid with a locked 0.5 shift.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class RegularGrid extends Sampler {
    /**
     * Constructs a {@link RegularGrid} sampler with a specified resolution.
     *
     * @param resolutionX The horizontal resolution.
     * @param resolutionY The vertical resolution.
     */
    public RegularGrid(int resolutionX, int resolutionY) {
        super(resolutionX, resolutionY);
    }

    /**
     * Constructs a {@link RegularGrid} sampler with a uniform resolution for both axes.
     *
     * @param resolution The resolution for both width and height.
     */
    public RegularGrid(int resolution) {
        super(resolution);
    }

    @Override
    public Point2D getOffset(int column, int row) {
        // A regular grid always samples exactly from the mathematical center (0.5) of the sub-pixel
        return calculateOffset(column, row, 0.5, 0.5);
    }
}