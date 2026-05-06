package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the ImageWriter class.
 */
class ImageWriterTest {
    /** The horizontal resolution of the test image. */
    private final static int resolutionX = 800;

    /** The vertical resolution of the test image. */
    private final static int resolutionY = 500;

    /** The size of the grid squares in pixels. */
    private final static int squareSize = 50;

    /** The color used for the grid lines. */
    private final static Color netColor = new Color(214, 41, 140);

    /** The background color used inside the grid squares. */
    private final static Color gridColor = new Color(41, 214, 115);
    /**
     * Default constructor for Javadoc purposes.
     */
    ImageWriterTest() {}
    /** Tests the creation of an image with a basic grid pattern and writes it to a file. */

    @Test
    void testImageWriter() {
        ImageWriter imageWriter = new ImageWriter(resolutionX, resolutionY);
        for (int i = 0; i < resolutionX; i++) {
            for (int j = 0; j < resolutionY; j++) {
                imageWriter.writePixel(i, j, i % squareSize == 0 || j % squareSize == 0 ? netColor:gridColor);
            }
        }
        imageWriter.writeToImage("myFirstPicture");

    }
}