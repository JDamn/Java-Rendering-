package com.jackheorhiian.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas{
    private static final long serialVersionUID = 1L;

    private static int width = 300;
    private static int height = width/16 * 9;
    private static int scale = 3;
    private String title = "Game";
    private Thread thread;
    BufferStrategy bs = null;
    private boolean running  = false;
    private Renderer renderer;
    private JFrame frame = new JFrame();
    private BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);//Creating image
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();//Accessing to image
    private int x = 0, y = 0;

    public Game() {
        Dimension size = new Dimension(width*scale,height*scale);
        setPreferredSize(size);

        renderer = new Renderer(width, height, pixels);

        frame.addKeyListener(new Keyboard());

    }

    public synchronized void start() {
        running = true;
        init();

        new Thread(new Runnable() {
            public void run() {
                long jvmLastTime = System.nanoTime();
                long time = System.currentTimeMillis();
                double jvmPartTime = 1_000_000_000.0 / 60.0;
                double delta = 0;
                int updates = 0;
                int frames = 0;
                while (running) {
                    long jvmNow = System.nanoTime();
                    delta += (jvmNow - jvmLastTime);
                    jvmLastTime = jvmNow;
                    if(delta >= jvmPartTime) {
                        update();
                        updates++;
                        delta = 0;
                    }
                    render();
                    frames++;
                    if(System.currentTimeMillis() - time > 1000) {
                        time += 1000;
                        frame.setTitle(title + " | " + "Updates: " + updates + ", " + "Frames: " + frames);
                        updates = 0;
                        frames = 0;
                    }
                }
            }
        }).start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        frame.setResizable(false);
        frame.setTitle("Fury of the ancients");
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void render() {
        if(bs == null) {
            createBufferStrategy(3);
            bs = getBufferStrategy();
        }
        renderer.clear();
        renderer.Render(x,y);
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    private void update() {
        if(Keyboard.isKeyPressed(KeyEvent.VK_UP)) y--;
        if(Keyboard.isKeyPressed(KeyEvent.VK_DOWN)) y++;
        if(Keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) x++;
        if(Keyboard.isKeyPressed(KeyEvent.VK_LEFT)) x--;
    }

    public static void main(String[] args) {
        new Game().start();
    }

}
