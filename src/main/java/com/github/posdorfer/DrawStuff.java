package com.github.posdorfer;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DrawStuff {

    private JFrame frame;
    private JLabel imagePreview;

    private BufferedImage image;

    private List<Line> allLines;
    private JCheckBox shuffle;
    private JCheckBox drawBox;
    private JProgressBar progress;
    private JTextField sleepfield;

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        new DrawStuff();
    }

    public DrawStuff() {
        setupUi();
    }

    private void setupUi() {
        frame = new JFrame("Visual Plotting");

        JButton selectimage = new JButton("Select Image");

        JButton startDraw = new JButton("Start Draw");

        sleepfield = new JTextField("5", 4);

        imagePreview = new JLabel(" ");

        frame.setLayout(new BorderLayout());

        progress = new JProgressBar(0, 100);

        progress.setStringPainted(true);
        progress.setString("0/100");

        shuffle = new JCheckBox("Shuffle?", false);
        
        drawBox = new JCheckBox("Draw Box?", true);

        JPanel panel = new JPanel(new BorderLayout());
        BoxLayout box = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(box);
        panel.add(selectimage);
        panel.add(startDraw);
        panel.add(shuffle);
        panel.add(drawBox);
        panel.add(sleepfield);

        frame.add(progress, BorderLayout.NORTH);
        frame.add(imagePreview, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imagePreview.setBorder(BorderFactory.createTitledBorder("Preview"));

        selectimage.addActionListener(e -> selectImageButton());
        startDraw.addActionListener(e -> {
            try {
                startDrawButton();
            } catch (InterruptedException | AWTException e1) {
                e1.printStackTrace();
            }
        });

    }

    public void selectImageButton() {

        JFileChooser fileChooser = new JFileChooser("/Users/wolf/Desktop/");

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    imagePreview.setIcon(new ImageIcon(image));
                    this.image = image;

                    this.allLines = makeList();

                    progress.setString(String.format("%d (%s)", allLines.size(), getDuration()));
                    //progress.setString("" + allLines.size() + "(~" + minutes + "min)");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    
    private String getDuration() {
      return getDuration(allLines.size());
    }
    
    private String getDuration(int size) {
        long duration = (size * (5 + 3 * Integer.parseInt(sleepfield.getText()))) / 1000;
        return getDuration(duration);
    }
    
    private String getDuration(long seconds) {
        if(seconds <= 60) {
            return "~" + seconds+" sec";
        }
        else {
            double minutes = seconds / 60.0;
            return String.format("~%.2f min", minutes);
        }
    }
    
    private void startDrawButton() throws InterruptedException, AWTException {
        Thread.sleep(3000);

        Point start = MouseInfo.getPointerInfo().getLocation();

        final int sleepTime = Integer.parseInt(sleepfield.getText());

        final int startX = (int) start.getX();
        final int startY = (int) start.getY();

        Robot r = new Robot();

        drawMyBox(startX, startY, r);

        int showConfirmDialog = JOptionPane.showConfirmDialog(frame, "CONTINUE?\nTakes " + getDuration());
        if (showConfirmDialog != JOptionPane.OK_OPTION) {
            return;
        }

        progress.setValue(0);
        progress.setMaximum(allLines.size());
        progress.setString("0/" + allLines.size());

        List<Line> lines = new ArrayList<>(this.allLines);
        if (shuffle.isSelected()) {
            Collections.shuffle(lines);
        }
        
        
        new Thread(() -> {
            
            if(isMac())  {
                r.mouseMove(startX, startY);
                sleep(10);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                sleep(5);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
            

            for (int i = 0; i < lines.size(); i++) {
                Line line = lines.get(i);
                r.mouseMove(startX + line.start.x, startY + line.start.y);

                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);

                sleep(sleepTime);

                r.mouseMove(startX + line.end.x, startY + line.end.y);

                sleep(sleepTime);

                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                sleep(sleepTime + 5);

                final int progressValue = i;
                SwingUtilities.invokeLater(() -> {
                    progress.setValue(progressValue);
                    progress.setString(String.format("%d/%d (%s)", progressValue, allLines.size(), getDuration(allLines.size()-progressValue)));
                });

            }
            SwingUtilities.invokeLater(() -> {
                progress.setString(String.format("%d/%d (%s)", allLines.size(), allLines.size(), "0 sec"));
                progress.setValue(allLines.size());
            });
        }).start();

        System.out.println("Done");
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e1) {
        }
    }

    public ArrayList<Line> makeList() {

        ArrayList<Line> lines = new ArrayList<>();
        for (int y = 0; y < image.getHeight(null); y++) {

            for (int x = 0; x < image.getWidth(null); x++) {
                Color c = new Color(image.getRGB(x, y), true);
                if (!isWhite(c)) {

                    Point start = new Point(x, y);
                    Point end = start;

                    int untoX = findLineUntilIsWhite(y, x);

                    if (untoX != -1) {
                        end = new Point(untoX, y);

                        x = untoX;
                    }
                    lines.add(new Line(start, end));

                } else {
                }
            }

        }
        

        return lines;
    }

    class Line {
        Point start;
        Point end;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }
    }

    int findLineUntilIsWhite(int y, int curX) {

        int initialX = curX;

        for (; curX < image.getWidth(); curX++) {
            Color c = new Color(image.getRGB(curX, y));

            if (isWhite(c)) {
                return curX - 1;
            }

        }

        if (curX <= initialX)
            return -1;

        return curX;
    }

    boolean isWhite(Color c) {
        return c.getBlue() >= 240 && c.getRed() >= 240 && c.getBlue() >= 240;
    }

    private void drawMyBox(int x, int y, Robot r) throws InterruptedException {
        final int sleep = 100;

        r.mouseMove(x, y);
        Thread.sleep(sleep);

        if (drawBox.isSelected()) {
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(sleep);
        }

        r.mouseMove(x + image.getWidth(), y);
        Thread.sleep(sleep);

        Thread.sleep(sleep);
        r.mouseMove(x + image.getWidth(), y);

        Thread.sleep(sleep);

        r.mouseMove(x + image.getWidth(), y + image.getHeight());
        Thread.sleep(sleep);

        r.mouseMove(x, y + image.getHeight());
        Thread.sleep(sleep);

        r.mouseMove(x, y);
        
        if (drawBox.isSelected()) {
            Thread.sleep(sleep);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }

        System.out.println("BOX DONE");
    }
    
    
    private boolean isMac() {
        
        return true;
    }

}
