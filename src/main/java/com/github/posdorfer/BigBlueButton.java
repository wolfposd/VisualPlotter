package com.github.posdorfer;

import static com.github.posdorfer.MouseHelp.clickMouse;
import static com.github.posdorfer.MouseHelp.sleep;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class BigBlueButton implements ConferenceRoom {

    Color[] colors = new Color[12];

    Point colorSelectionPoint = new Point(0, 0);

    boolean isSetup = false;

    public BigBlueButton() {
        colors[0] = Color.decode("0x000000"); // Black
        colors[1] = Color.decode("0xFFFFFF"); // White
        colors[2] = Color.decode("0xFF0000"); // Red
        colors[3] = Color.decode("0xFF8800"); // Orange
        colors[4] = Color.decode("0xCCFF00"); // lime
        colors[5] = Color.decode("0x00FF00"); // Green
        colors[6] = Color.decode("0x00FFFF"); // Cyan
        colors[7] = Color.decode("0x0088FF"); // lightblue
        colors[8] = Color.decode("0x0000FF"); // blue
        colors[9] = Color.decode("0x8800FF"); // purple
        colors[10] = Color.decode("0xFF00FF"); // pink
        colors[11] = Color.decode("0xC0C0C0"); // silver

    }

    @Override
    public void performRequiredStartup() {
        if (!isSetup) {
            JOptionPane.showMessageDialog(null, "Move Mouse on Color selection box and press enter");
            colorSelectionPoint = MouseInfo.getPointerInfo().getLocation();
            isSetup = true;
        }
    }

    public Color[] getColors() {
        return Arrays.copyOf(colors, colors.length);
    }

    @Override
    public void performColorSelection(int color, Robot r) {
        Point loc = getLocationOfColor(color, colorSelectionPoint);

        r.mouseMove(colorSelectionPoint.x, colorSelectionPoint.y);

        clickMouse(r);

        sleep(100);
        r.mouseMove(loc.x, loc.y);

        clickMouse(r);
    }

    public Point getLocationOfColor(int col, Point colorSelectButton) {
        final int width = 42;
        int xpos = colorSelectButton.x - (colors.length - col) * width;
        return new Point(xpos, colorSelectButton.y);
    }
    
    @Override
    public int getGray() {
        return 11;
    }
    
    @Override
    public int getBlack() {
        return 0;
    }

}
