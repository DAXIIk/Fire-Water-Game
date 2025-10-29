package com.mycompany.firewatergame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class FireWaterGame extends JPanel implements ActionListener, KeyListener {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private FirePlayer fire;
    private WaterPlayer water;
    private Image background;
    private Image background2;
    private Image background3;
    private Timer timer;

    // ปุ่มกด
    private boolean leftPressed, rightPressed, fireJumpPressed;
    private boolean waterLeftPressed, waterRightPressed, waterJumpPressed;

    // เวลาเกม
    private int ticks = 0;
    private int secondsLeft = 90;

    // ด่าน
    private int level = 1;

    // สถานะเกม
    private boolean gameStarted = false;

    // Platform, Hitbox, WarpGate, LevelGate
    private List<Platform> platforms = new ArrayList<>();
    private List<HitBox> hitboxes = new ArrayList<>();
    private List<WarpGate> warpGates = new ArrayList<>();
    private List<LevelGate> levelGates = new ArrayList<>();

    public FireWaterGame() {
        loadImages();

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private void loadImages() {
        Image fireImg = new ImageIcon("fire.png").getImage();
        Image waterImg = new ImageIcon("water.png").getImage();
        background = new ImageIcon("background.png").getImage();
        background2 = new ImageIcon("background2.png").getImage();
        background3 = new ImageIcon("background3.png").getImage();

        fire = new FirePlayer(100, 500, fireImg);
        water = new WaterPlayer(200, 500, waterImg);
    }

    private void initLevel(int lvl) {
        platforms.clear();
        hitboxes.clear();
        warpGates.clear();
        levelGates.clear();

        switch (lvl) {
            case 1:
                platforms.add(new Platform(0, 380, 350, 20, false));
                platforms.add(new Platform(530,490,150,30,false));
                platforms.add(new Platform(400,370,150,30,false));
                platforms.add(new Platform(60,490,120,20,false));
                platforms.add(new Platform(275,300,120,20,false));
                platforms.add(new Platform(490,260,120,20,false));
                platforms.add(new Platform(650,170,120,30,false));
                platforms.add(new Platform(620,100,170,30,false));
                platforms.add(new Platform(0,590,800,30,false));

                hitboxes.add(new HitBox(200, 350, 20, 20, "fire" , false));
                hitboxes.add(new HitBox(215, 580, 100, 30, "water" , false));
                hitboxes.add(new HitBox(530,485,150,30, "water" , false));
                hitboxes.add(new HitBox(400,365,150,30, "water" , false));
                hitboxes.add(new HitBox(60,485,120,20,"fire",false));

                levelGates.add(new LevelGate(715, 25, 100, 80 , false));
                break;

            case 2:
                platforms.add(new Platform(35, 500, 175, 20,false));
                platforms.add(new Platform(275, 425, 260, 20,false));
                platforms.add(new Platform(600, 500, 175, 20,false));
                platforms.add(new Platform(535, 160, 250, 20,false));
                platforms.add(new Platform(270,560,250,40,false));
                platforms.add(new Platform(240,255,175,20,false));
                platforms.add(new Platform(0,210,175,20,false));
                platforms.add(new Platform(240,145,175,20,false));

                hitboxes.add(new HitBox(35, 495, 175, 20, "fire", false));
                hitboxes.add(new HitBox(600, 495, 175, 20, "water", false));
                hitboxes.add(new HitBox(240, 140, 175, 20, "fire", false));
                hitboxes.add(new HitBox(240, 250, 175, 20, "water", false));

                warpGates.add(new WarpGate(650, 325, 70, 70, 20, 50 , false));
                levelGates.add(new LevelGate(670, 50, 120, 120, false));
                break;

            case 3:
                platforms.add(new Platform(0, 570, 300, 20,false));
                platforms.add(new Platform(275, 200, 150, 20,false));
                platforms.add(new Platform(425, 300, 150, 20,false));
                platforms.add(new Platform(130, 400, 150, 20,false));
                platforms.add(new Platform(600, 250, 120, 20,false));
                platforms.add(new Platform(700, 170, 120, 20,false));
                platforms.add(new Platform(0, 200, 180, 20,false));

                hitboxes.add(new HitBox(275, 195, 150, 20, "water", false));
                hitboxes.add(new HitBox(425, 295, 150, 20, "fire", false));

                warpGates.add(new WarpGate(330, 575, 200, 30, 20, 50 , false));
                warpGates.add(new WarpGate(130, 310, 70, 70, 425, 240 , false));
                levelGates.add(new LevelGate(700, 70, 100, 100, false));
                break;
        }

        resetPlayers();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // หน้าเมนูเริ่มเกม
        if (!gameStarted) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("🔥 Fire & Water 🔵", 180, 200);

            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Press ENTER to Start", 250, 350);
            g.drawString("Press ESC to Exit", 270, 400);
            return;
        }

        // ฉากหลังแต่ละด่าน
        switch (level) {
            case 1: g.drawImage(background, 0, 0, WIDTH, HEIGHT, this); break;
            case 2: g.drawImage(background2, 0, 0, WIDTH, HEIGHT, this); break;
            case 3: g.drawImage(background3, 0, 0, WIDTH, HEIGHT, this); break;
        }

        for (Platform p : platforms) p.draw(g);
        for (HitBox h : hitboxes) h.draw(g);
        for (WarpGate w : warpGates) w.draw(g);
        for (LevelGate l : levelGates) l.draw(g);

        fire.draw(g, this);
        water.draw(g, this);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(String.format("Time: %02d:%02d", secondsLeft/60, secondsLeft%60), 10, 20);
        g.drawString("Level: " + level, 10, 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameStarted) {
            repaint();
            return;
        }

        ticks++;
        if (ticks % 60 == 0) {
            secondsLeft--;
            if (secondsLeft <= 0) resetGame();
        }

        // การเคลื่อนไหว
        if (leftPressed) fire.moveLeft();
        if (rightPressed) fire.moveRight();
        if (fireJumpPressed) fire.jump(getPlatformRects());

        if (waterLeftPressed) water.moveLeft();
        if (waterRightPressed) water.moveRight();
        if (waterJumpPressed) water.jump(getPlatformRects());

        fire.update(getPlatformRects());
        water.update(getPlatformRects());

        checkCollision();
        checkHazards();
        checkWarp();
        checkFall();
        checkLevelChange();

        repaint();
    }

    private List<Rectangle> getPlatformRects() {
        List<Rectangle> rects = new ArrayList<>();
        for (Platform p : platforms) rects.add(p.rect);
        return rects;
    }

    private void resetPlayers() {
        switch (level) {
            case 1:
                fire.x = 100; fire.y = 500;
                water.x = 200; water.y = 500;
                break;
            case 2:
                fire.x = 350; fire.y = 500;
                water.x = 450; water.y = 500;
                break;
            case 3:
                fire.x = 100; fire.y = 500;
                water.x = 200; water.y = 500;
                break;
        }
        fire.velY = 0;
        water.velY = 0;
    }

    private void resetGame() {
        level = 1;
        initLevel(level);
        secondsLeft = 90;
        ticks = 0;
    }

    private void timeDecreased() {
        secondsLeft = Math.max(0, secondsLeft - 10);
    }

    private void nextLevel() {
        level++;
        if (level > 3) {
            JOptionPane.showMessageDialog(this, "🎉 You Win!");
            level = 1;
        }
        initLevel(level);
        secondsLeft = 90;
        ticks = 0;
    }

    private void checkCollision() {
        Rectangle fireRect = new Rectangle(fire.x, fire.y, fire.width, fire.height);
        Rectangle waterRect = new Rectangle(water.x, water.y, water.width, water.height);

        if (fireRect.intersects(waterRect)) resetPlayers();
    }

    private void checkHazards() {
        Rectangle fireRect = new Rectangle(fire.x, fire.y, fire.width, fire.height);
        Rectangle waterRect = new Rectangle(water.x, water.y, water.width, water.height);

        for (HitBox h : hitboxes) {
            if (h.type.equals("fire") && waterRect.intersects(h.rect)) {
                resetPlayers();
                timeDecreased();
            }
            if (h.type.equals("water") && fireRect.intersects(h.rect)) {
                resetPlayers();
                timeDecreased();
            }
        }
    }

    private void checkWarp() {
        Rectangle fireRect = new Rectangle(fire.x, fire.y, fire.width, fire.height);
        Rectangle waterRect = new Rectangle(water.x, water.y, water.width, water.height);

        for (WarpGate w : warpGates) {
            if (fireRect.intersects(w.rect)) {
                fire.x = w.targetX;
                fire.y = w.targetY;
            }
            if (waterRect.intersects(w.rect)) {
                water.x = w.targetX + 50;
                water.y = w.targetY;
            }
        }
    }

    private void checkFall() {
        if (fire.y > 580) {
            resetFireOnly();
            timeDecreased();
        }
        if (water.y > 580) {
            resetWaterOnly();
            timeDecreased();
        }
    }

    private void resetFireOnly() {
        switch (level) {
            case 1: fire.x = 100; fire.y = 500; break;
            case 2: fire.x = 350; fire.y = 500; break;
            case 3: fire.x = 100; fire.y = 500; break;
        }
        fire.velY = 0;
    }

    private void resetWaterOnly() {
        switch (level) {
            case 1: water.x = 200; water.y = 500; break;
            case 2: water.x = 450; water.y = 500; break;
            case 3: water.x = 200; water.y = 500; break;
        }
        water.velY = 0;
    }

    private void checkLevelChange() {
        Rectangle fireRect = new Rectangle(fire.x, fire.y, fire.width, fire.height);
        Rectangle waterRect = new Rectangle(water.x, water.y, water.width, water.height);

        boolean bothInGate = false;
        for (LevelGate g : levelGates) {
            if (fireRect.intersects(g.rect) && waterRect.intersects(g.rect)) {
                bothInGate = true;
                break;
            }
        }

        if (bothInGate) nextLevel();
    }

    // ================== KEYBOARD ==================
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (!gameStarted) {
            if (code == KeyEvent.VK_ENTER) {
                gameStarted = true;
                initLevel(level);
            } else if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            return;
        }

        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_W) fireJumpPressed = true;

        if (code == KeyEvent.VK_LEFT) waterLeftPressed = true;
        if (code == KeyEvent.VK_RIGHT) waterRightPressed = true;
        if (code == KeyEvent.VK_UP) waterJumpPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_W) fireJumpPressed = false;

        if (code == KeyEvent.VK_LEFT) waterLeftPressed = false;
        if (code == KeyEvent.VK_RIGHT) waterRightPressed = false;
        if (code == KeyEvent.VK_UP) waterJumpPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fire & Water Game");
        FireWaterGame gamePanel = new FireWaterGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}

// ====== Classes ======
class Platform {
    Rectangle rect;
    Color color;
    boolean visible;

    public Platform(int x, int y, int w, int h, boolean visible) {
        rect = new Rectangle(x, y, w, h);
        this.visible = visible;
        this.color = new Color(128,128,128,180);
    }

    public void draw(Graphics g) {
        if (visible) {
            g.setColor(color);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
    }
}

class HitBox {
    Rectangle rect;
    String type;
    boolean visibility;
    Color color;

    public HitBox(int x, int y, int w, int h, String type, boolean visibility) {
        rect = new Rectangle(x, y, w, h);
        this.type = type;
        this.visibility = visibility;
        this.color = type.equals("fire") ? new Color(255,69,0,180) : new Color(30,144,255,180);
    }

    public void draw(Graphics g) {
        if (visibility) {
            g.setColor(color);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
    }
}

class WarpGate {
    Rectangle rect;
    int targetX, targetY;
    Color color = new Color(148,0,211,180);
    boolean visibility;

    public WarpGate(int x, int y, int w, int h, int targetX, int targetY , boolean visibility) {
        rect = new Rectangle(x, y, w, h);
        this.targetX = targetX;
        this.targetY = targetY;
        this.visibility = visibility;
    }

    public void draw(Graphics g) {
        if (!visibility) return;
        g.setColor(color);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }
}

class LevelGate {
    Rectangle rect;
    Color color = new Color(255,215,0,200);
    boolean visibility;

    public LevelGate(int x, int y, int w, int h, boolean visibility) {
        rect = new Rectangle(x, y, w, h);
        this.visibility = visibility;
    }

    public void draw(Graphics g) {
        if (visibility) {
            g.setColor(color);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
    }
}

abstract class Player {
    protected int x, y;
    protected int velY = 0;
    protected final int GRAVITY = 1;
    protected Image image;
    protected int width = 50, height = 50;

    public Player(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void moveLeft() { x -= 5; }
    public void moveRight() { x += 5; }

    public void jump(List<Rectangle> platforms) {
        if (isOnGround(platforms)) velY = -14;
    }

    public boolean isOnGround(List<Rectangle> platforms) {
        Rectangle feet = new Rectangle(x, y + height, width, 2);
        for (Rectangle p : platforms) if (feet.intersects(p)) return true;
        return y >= 550;
    }

    public void update(List<Rectangle> platforms) {
        velY += GRAVITY;
        y += velY;

        for (Rectangle p : platforms) {
            Rectangle feet = new Rectangle(x, y + height, width, 2);
            if (feet.intersects(p) && velY >= 0) {
                y = p.y - height;
                velY = 0;
            }
        }

        if (y > 580) velY = 0;
    }

    public void draw(Graphics g, Component c) {
        g.drawImage(image, x, y, width, height, c);
    }
}

class FirePlayer extends Player {
    public FirePlayer(int x, int y, Image img) { super(x, y, img); }
}

class WaterPlayer extends Player {
    public WaterPlayer(int x, int y, Image img) { super(x, y, img); }
}
