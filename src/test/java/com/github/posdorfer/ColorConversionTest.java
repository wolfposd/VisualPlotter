package com.github.posdorfer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Color;

import org.junit.Test;

public class ColorConversionTest {

    @Test
    public void test() {

        Color[] colors = new BigBlueButton().colors;

        ColorConversion conv = new ColorConversion(null, colors);

//        assertThat(conv.colorDistanceHue(Color.WHITE, Color.decode("0xFFFFFF")), is(0.0));
//        
//        assertThat(conv.colorDistanceHue(Color.decode("0xc0c0c0"), Color.decode("0xFFFFFF")), is(0.2748355746108909));
//        
//        assertThat(conv.colorDistanceHue(Color.BLACK, Color.decode("0xFFFFFF")), is(1.1124297730643495));
        

        assertThat(conv.getClosestColor(Color.WHITE), is(Color.WHITE));
        
        assertThat(conv.getClosestColor(Color.decode("0xffffff")), is(Color.WHITE));
        
        assertThat(conv.getClosestColor(Color.WHITE), is(Color.WHITE));
       
        
    }

}
