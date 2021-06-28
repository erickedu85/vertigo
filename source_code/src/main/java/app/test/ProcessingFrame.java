package app.test;

import javax.swing.JFrame;

import processing.core.PApplet;

public class ProcessingFrame extends PApplet{

    public void settings(){
        //size(200, 200);
//        fullScreen();
    }

    public void draw(){
        background(0);
        ellipse(mouseX, mouseY, 20, 20);
    }

    public static void main(String... args){

        //create your JFrame
        JFrame frame = new JFrame("JFrame Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create your sketch
        ProcessingFrame pt = new ProcessingFrame();
        
//        //get the PSurface from the sketch
////        PSurface ps = pt.initSurface();
//
//        //initialize the PSurface
////        ps.setSize(pt.displayWidth, pt.displayHeight);
//
//        //get the SmoothCanvas that holds the PSurface
////        SmoothCanvas smoothCanvas = (SmoothCanvas)ps.getNative();
//
//        //SmoothCanvas can be used as a Component
//        frame.add(smoothCanvas);
//
//        //make your JFrame visible
//        frame.setSize(pt.displayWidth, pt.displayHeight);
//        frame.setVisible(true);
//
//        //start your sketch
//        ps.startThread();
    }
}