package com.github.posdorfer;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ColorConversion {

    public BufferedImage image;
    Color[] colors;

    public ColorConversion(BufferedImage image, Color[] colors) {
        this.image = image;
        this.colors = colors;
    }

    public void convertImage() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                int rgb = image.getRGB(x, y);

                Color original = new Color(rgb, false);
                Color repaint = getClosestColor(original);

                image.setRGB(x, y, repaint.getRGB());
            }
        }
    }

    public Color getClosestColor(Color destination) {
        double distance = Integer.MAX_VALUE;
        Color found = Color.WHITE;

        for (Color color : colors) {
            double dist = colorDistance(color, destination);
            if (dist < distance) {
                distance = dist;
                found = color;
            }
        }

        return found;
    }

    public double colorDistance(Color c1, Color c2) {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
    }

    public double colorDistance2(Color c1, Color c2) {
        int r1 = c1.getRed();
        int r2 = c2.getRed();
        int g1 = c1.getGreen();
        int g2 = c2.getGreen();
        int b1 = c1.getBlue();
        int b2 = c2.getBlue();

        return Math.sqrt(Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));

    }

    public double colorDistanceHue(Color c1, Color c2) {

        float[] h1 = toHSV(c1);
        float[] h2 = toHSV(c2);

        // return Math.abs(h1[0] - h2[0]);

        return Math.sqrt(1.457 * Math.pow(h1[0] - h2[0], 2) + 1.2875 * Math.pow(h1[1] - h2[1], 2) + 1.2375 * Math.pow(h1[2] - h2[2], 2));
    }

    public float[] toHSV(Color c) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
        return hsv;
    }

}
