package com.github.posdorfer;

import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseHelp {

    public static void clickMouse(Robot r) {
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        sleep(5);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void performClickInWindowIfMac(final int startX, final int startY, Robot r) {
        if (isMac()) {
            r.mouseMove(startX, startY);
            sleep(5);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            sleep(5);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public static void sleep(int ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
            }
        }
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
    }
}
