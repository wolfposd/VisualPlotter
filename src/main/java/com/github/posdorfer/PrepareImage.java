package com.github.posdorfer;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

public class PrepareImage {

    public static void main(String[] args) throws IOException {

        BufferedImage read = ImageIO.read(new File("C:\\Users\\Wolf\\Desktop\\testimage.jpg"));

        ConferenceRoom room = new BigBlueButton();
        ColorConversion colCon = new ColorConversion(read, room.getColors());
        colCon.convertImage();

        printColors(colCon.image);

        Color[] colors = room.getColors();
        List<List<Line>> makeLines = makeLines(colCon.image, colors);

        printLines(makeLines, room.getColors());

    }
    
    public static void printLines(List<List<Line>> makeLines, Color[] colors) {
        for (int i = 0; i < makeLines.size(); i++) {
            List<Line> line = makeLines.get(i);

            System.out.println(Integer.toHexString(colors[i].getRGB()) + " " + line.size());
        }
    }

    static void printColors(BufferedImage image) {

        HashMap<Color, Integer> countmap = new HashMap<>();

        for (int y = 0; y < image.getHeight(null); y++) {
            for (int x = 0; x < image.getWidth(null); x++) {
                Color c = new Color(image.getRGB(x, y), true);
                Integer count = countmap.getOrDefault(c, 0);
                countmap.put(c, count + 1);
            }
        }

        countmap.forEach((k, v) -> {
            System.out.println(Integer.toHexString(k.getRGB()) + " : " + v + " pixels");
        });

    }

    static List<List<Line>> makeLines(BufferedImage image, Color[] colors) {

        List<List<Line>> lines = new ArrayList<>();
        
        for (int i = 0; i < colors.length; i++) {
            Color color = colors[i];

            ArrayList<Line> line = new ArrayList<>();

            for (int y = 0; y < image.getHeight(null); y++) {
                for (int x = 0; x < image.getWidth(null); x++) {

                    Color currentColor = new Color(image.getRGB(x, y), true);

                    if (Objects.equals(currentColor, color)) {

                        Point start = new Point(x, y);
                        Point end = start;

                        int untoX = findLineUntilDifferentColor(image, y, x, currentColor);

                        if (untoX != -1) {
                            end = new Point(untoX, y);

                            x = untoX;
                        }

                        line.add(new Line(start, end));

                    } else {
                    }
                }

            }

            lines.add(line);

        }

        return lines;
    }

    static int findLineUntilDifferentColor(BufferedImage image, int y, int curX, final Color colorToCheck) {
        int initialX = curX;

        for (; curX < image.getWidth(); curX++) {

            Color c = new Color(image.getRGB(curX, y));

            if (!Objects.equals(colorToCheck, c)) {
                return curX - 1;
            }
        }

        if (curX <= initialX)
            return -1;

        return curX;
    }

}
