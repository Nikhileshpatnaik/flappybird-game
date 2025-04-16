import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class FlappyBurd extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 800, HEIGHT = 600;
    private Rectangle bird;
    private ArrayList<Rectangle> pipes;
    private int ticks, yMotion, score, highScore = 0;
    private boolean gameOver, started;

    private Timer timer;

    private Image birdImage, pipeImage, bgImage, groundImage;

    public FlappyBurd() {
        JFrame frame = new JFrame("Flappy Burd");
        timer = new Timer(20, this);

        frame.setSize(WIDTH, HEIGHT);
        frame.add(this);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addKeyListener(this);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
        pipes = new ArrayList<>();

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

        loadHighScore();
        loadImages();
        timer.start();
    }

    private void loadImages() {
        birdImage = new ImageIcon("bird.png").getImage();
        pipeImage = new ImageIcon("pipe.png").getImage();
        bgImage = new ImageIcon("background.png").getImage();
        groundImage = new ImageIcon("ground.png").getImage();
    }

    private void loadHighScore() {
        try {
            File file = new File("highscore.dat");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                highScore = Integer.parseInt(reader.readLine());
                reader.close();
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try {
            PrintWriter writer = new PrintWriter("highscore.dat");
            writer.println(highScore);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPipe(boolean start) {
        int space = 300;
        int width = 80;
        int height = 50 + new Random().nextInt(300);

        if (start) {
            pipes.add(new Rectangle(WIDTH + width + pipes.size() * 300, HEIGHT - height, width, height));
            pipes.add(new Rectangle(WIDTH + width + (pipes.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            Rectangle last = pipes.get(pipes.size() - 1);
            pipes.add(new Rectangle(last.x + 600, HEIGHT - height, width, height));
            pipes.add(new Rectangle(last.x + 600, 0, width, HEIGHT - height - space));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.drawImage(bgImage, 0, 0, WIDTH, HEIGHT, null);

        // Pipes
        for (Rectangle pipe : pipes) {
            g.drawImage(pipeImage, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Ground Image
        g.drawImage(groundImage, 0, HEIGHT - 150, WIDTH, 150, null);

        // Bird
        g.drawImage(birdImage, bird.x, bird.y, bird.width, bird.height, null);

        // Score and High Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        if (!gameOver && started) {
            g.drawString("Score: " + score, 20, 50);
        }

        g.drawString("High Score: " + highScore, 20, 100);

        if (!started) {
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("Press SPACE to Start", 100, HEIGHT / 2 - 50);
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("Game Over", 220, HEIGHT / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Press SPACE to Restart", 270, HEIGHT / 2 + 50);
        }
    }

    public void jump() {
        if (gameOver) {
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
            pipes.clear();
            yMotion = 0;
            score = 0;

            addPipe(true);
            addPipe(true);
            addPipe(true);
            addPipe(true);

            gameOver = false;
        }

        if (!started) {
            started = true;
        }

        if (yMotion > 0) {
            yMotion = 0;
        }

        yMotion -= 10;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 10;

        ticks++;

        if (started) {
            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= speed;
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                if (pipe.x + pipe.width < 0) {
                    pipes.remove(pipe);
                    if (pipe.y == 0) {
                        addPipe(false);
                    }
                }
            }

            bird.y += yMotion;

            for (Rectangle pipe : pipes) {
                if (pipe.intersects(bird)) {
                    gameOver = true;
                    bird.x = pipe.x - bird.width;
                }
            }

            if (bird.y > HEIGHT - 150 || bird.y < 0) {
                gameOver = true;
            }

            for (Rectangle pipe : pipes) {
                if (pipe.y == 0 &&
                        bird.x + bird.width / 2 > pipe.x + pipe.width / 2 - 10 &&
                        bird.x + bird.width / 2 < pipe.x + pipe.width / 2 + 10) {
                    score++;
                }
            }

            if (gameOver) {
                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }
            }
        }

        repaint();
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) jump();
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new FlappyBurd();
    }
}