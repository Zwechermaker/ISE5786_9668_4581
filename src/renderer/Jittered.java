package renderer;

import primitives.Point2D;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A sampling strategy that uses jittering to reduce aliasing artifacts.
 * <p>
 * This sampler adds a random offset to each sample point within its pixel,
 * which helps to break up regular patterns and create a more natural-looking image.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class Jittered extends Sampler {
    /**
     * Constructs a {@link Jittered} sampler with a specified resolution.
     *
     * @param resolutionX The horizontal resolution.
     * @param resolutionY The vertical resolution.
     */
    public Jittered(int resolutionX, int resolutionY) {
        super(resolutionX, resolutionY);
    }

    /**
     * Constructs a {@link Jittered} sampler with a uniform resolution for both axes.
     *
     * @param resolution The resolution for both width and height.
     */
    public Jittered(int resolution) {
        super(resolution);
    }

    @Override
    public Point2D getOffset(int column, int row) {
        // Use ThreadLocalRandom to ensure thread safety in multi-threaded rendering.
        double randomX = ThreadLocalRandom.current().nextDouble();
        double randomY = ThreadLocalRandom.current().nextDouble();

        double xOffset = (column + randomX) / _resolutionX - 0.5;
        double yOffset = -(row + randomY) / _resolutionY + 0.5;

        return new Point2D(xOffset, yOffset);
    }
}
