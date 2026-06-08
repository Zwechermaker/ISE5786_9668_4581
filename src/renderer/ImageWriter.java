package renderer;

import primitives.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A class for writing pixel data to an image file.
 * <p>
 * This class wraps a {@link BufferedImage} to allow setting individual pixel colors
 * and then exporting the final image as a PNG file.
 *
 * @author Dan Zilberstein
 */
final class ImageWriter {
    /**
     * The output directory for the generated image files, relative to the project's working directory.
     */
    private static final String FOLDER_PATH = System.getProperty("user.dir") + "/images";

    /**
     * The internal image buffer where pixel color data is stored.
     */
    private final BufferedImage _image;

    /**
     * Constructs an {@link ImageWriter} for a specified resolution.
     *
     * @param nX The horizontal resolution (width) of the image in pixels.
     * @param nY The vertical resolution (height) of the image in pixels.
     * @throws IllegalArgumentException if the width or height is not a positive number.
     */
    ImageWriter(int nX, int nY) {
        if (nX <= 0 || nY <= 0)
            throw new IllegalArgumentException("Image resolution must be positive.");
        _image = new BufferedImage(nX, nY, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Writes the buffered image to a PNG file in the default output directory.
     *
     * @param fileName The name of the output file (without the .png extension).
     * @throws IllegalStateException if the output directory cannot be created or if there is an I/O error.
     */
    void writeToImage(String fileName) {
        try {
            File folder = new File(FOLDER_PATH);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IllegalStateException("Could not create output directory: " + FOLDER_PATH);
            }
            File file = new File(folder, fileName + ".png");
            ImageIO.write(_image, "png", file);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error while writing image to " + FOLDER_PATH, e);
        }
    }

    /**
     * Sets the color of a specific pixel in the image buffer.
     *
     * @param xIndex The x-coordinate (column) of the pixel.
     * @param yIndex The y-coordinate (row) of the pixel.
     * @param color  The {@link Color} to write to the pixel.
     */
    void writePixel(int xIndex, int yIndex, Color color) {
        _image.setRGB(xIndex, yIndex, color.getColor().getRGB());
    }
}
