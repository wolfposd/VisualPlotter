package com.github.posdorfer;

import java.awt.Color;
import java.awt.Robot;

public interface ConferenceRoom {

    public void performRequiredStartup();

    public Color[] getColors();

    public void performColorSelection(int color, Robot r);
    
    public int getBlack();
    
    public int getGray();

}