package renderer;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Jittered extends Sampler {
    public Jittered(int resolutionX, int resolutionY) {
        super(resolutionX, resolutionY);
    }

    public Jittered(int resolution) {
        super(resolution);
    }
    @Override
    public Point2D getOffset(int row, int column) {
        //prevent access to the same random generator from different threads.
        double randomX = ThreadLocalRandom.current().nextDouble();
        double randomY = ThreadLocalRandom.current().nextDouble();

        double xOffset = (column + randomX) / _resolutionX - 0.5;

        double yOffset = -(row + randomY) / _resolutionY + 0.5;

        return new Point2D(xOffset, yOffset);
    }

}