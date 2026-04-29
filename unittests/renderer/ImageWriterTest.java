package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

import static org.junit.jupiter.api.Assertions.*;

class ImageWriterTest {
    private final static int resolutionX = 800;
    private final static int resolutionY = 500;
    private final static int squareSize = 50;
    private final static Color netColor = new Color(214, 41, 140);
    private final static Color gridColor = new Color(41, 214, 115);

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