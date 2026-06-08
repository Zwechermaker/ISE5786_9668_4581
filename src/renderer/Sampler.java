package renderer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * an abstract class that defines sampling functionality
 */
public abstract class Sampler {
    /**
     * the x resolution
     */
    private final int _resolutionX;
    /**
     * the y resolution
     */
    private final int _resolutionY;

    /**
     * returns an offset for a spescific index of a pixel
     * @param row dfnoe
     * @param column kernri
     * @return an offset
     */
    public Point2D sample(int row, int column){
        return null;
    }

    /**
     * a function that generates all offset
     * @return a list of offset
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


    /**
     * a constructor
     * @param resolutionX parameter
     * @param resolutionY parameter
     */
    public Sampler(int resolutionX, int resolutionY) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
    }

    /**
     * a constructor
     * @param resolution parameter
     */
    public Sampler(int resolution) {
        this.resolutionX = resolution;
        this.resolutionY = resolution;
    }
}
