package dev.undefinedteam.gensh1n.utils.render;

public class TextureUtils {
    public static void processPixelValues(int[] pixelBuffer, int width, int height)
    {
        int[] vBuffer = new int[width];
        int hBuffer = height / 2;

        for (int i = 0; i < hBuffer; ++i)
        {
            System.arraycopy(pixelBuffer, i * width, vBuffer, 0, width);
            System.arraycopy(pixelBuffer, (height - 1 - i) * width, pixelBuffer, i * width, width);
            System.arraycopy(vBuffer, 0, pixelBuffer, (height - 1 - i) * width, width);
        }
    }
}
