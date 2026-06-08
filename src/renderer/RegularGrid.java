package renderer;

import java.awt.geom.Point2D;

public class RegularGrid extends Sampler {
    public RegularGrid(int resolutionX, int resolutionY) {
        super(resolutionX, resolutionY);
    }

    public RegularGrid(int resolution) {
        super(resolution);
    }

    @Override
    public Point2D getOffset(int row, int column){
        double xOffset = (column - (_resolutionX - 1) / 2.0) / _resolutionX;
        double yOffset = -(row - (_resolutionY - 1) / 2.0) / _resolutionY;
        return new Point2D(xOffset, yOffset);
    }
}
