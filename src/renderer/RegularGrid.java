package renderer;

import primitives.Point2D;

/**
 * A sampling strategy that uses a regular grid pattern to generate sample points within a pixel.
 * <p>
 * This sampler places sample points at the center of each sub-pixel in a uniform grid.
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
        double xOffset = (column - (_resolutionX - 1) / 2.0) / _resolutionX;
        double yOffset = -(row - (_resolutionY - 1) / 2.0) / _resolutionY;
        return new Point2D(xOffset, yOffset);
    }
}
