import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // All sounds used
    String jumpSoundPath = "C:\\Users\\tdane\\Downloads\\751699__el_boss__game-jump-sound-boing-2-of-2.wav";
    String scoreSoundPath = "C:\\Users\\tdane\\Downloads\\514160__edwardszakal__score-beep.wav";
    String gameOverSoundPath = "C:\\Users\\tdane\\Downloads\\245807__markb258__dead_bird.wav";
    boolean gameStarted = false;
    int boardWidth = 360;
    int boardHeight = 640;
    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image ParrotImg;
    Image nyanCatImg;
    Image penguinImg;
    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

    }
// Pipe data to fit to board
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }

    }
// Bird Logic + pipe + random + timers
    Bird bird;
    float velocityX = -4;
    float velocityY = 0;
    float gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird(String selected) {

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();
// Image Information
        bottomPipeImg = new ImageIcon("C:\\Users\\tdane\\Downloads\\bottom.png").getImage();
        topPipeImg = new ImageIcon("C:\\Users\\tdane\\Downloads\\top.png").getImage();
        backgroundImg = new ImageIcon("C:\\Users\\tdane\\Downloads\\Background Flappy.jpg").getImage();
        birdImg = new ImageIcon("C:\\Users\\tdane\\Downloads\\flappy.jpg").getImage();
        ParrotImg = new ImageIcon("C:\\Users\\tdane\\Downloads\\parrot.jpg").getImage();
        nyanCatImg = new ImageIcon("C:\\Users\\tdane\\Downloads\\nyan.png").getImage();
        penguinImg = new ImageIcon("C:\\Users\\tdane\\OneDrive - McNeese State University\\Pictures\\penguin2.jpg").getImage();
      // Switch options
        switch (selected) {
            case "Parrot":
                bird = new Bird(ParrotImg);
                break;
            case "Nyan Cat":
                bird = new Bird(nyanCatImg);
                break;
            case "Penguin":
                bird = new Bird(penguinImg);
                break;
            default:
                bird = new Bird(birdImg);
                break;
        }
        {
        }

        pipes = new ArrayList<>();
        placePipeTimer = new Timer(1600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        gameLoop = new javax.swing.Timer(20, this);
        gameLoop.start();
        System.out.println("Game loop");
    }
// Pipe Placement + Gap between pipe
    void placePipes(){
        int openingSpace = boardHeight / 4 + random.nextInt(40)- 20;
        int minPipeY = -pipeHeight + 100;
        int maxPipeY = -100;
        int randomPipeY = random.nextInt(maxPipeY - minPipeY + 1) + minPipeY;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        topPipe.x = boardWidth;
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        bottomPipe.x = boardWidth;
        pipes.add(bottomPipe);
    }
// Drawing
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(gameOver ? "Game Over: " + (int) score : "Score: " + (int) score, 10, 35);
    }

// Movement of bird + Score + game over logic
    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);
        if (bird.y + bird.height > boardHeight) {
            bird.y = boardHeight - bird.height;
        }
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if (!pipe.passed && pipe.img == topPipeImg && bird.x > pipe.x + pipe.width) {
                score ++;
                pipe.passed = true;
                playSound(scoreSoundPath);
            }
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird bird, Pipe pipe) {
        int centerX = bird.x + bird.width / 2;
        int centerY = bird.y + bird.height / 2;
        int radius = Math.min(bird.width, bird.height) /2;
        Rectangle pipeRectangle = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
        return pipeRectangle.intersects(new Rectangle(centerX - radius, centerY - radius, radius * 2, radius * 2));
    }
// More game over logic
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }
    // Space bar reader + more sound + game started + more game over logic
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            playSound(jumpSoundPath);
            if(!gameStarted) {
                gameStarted = true;
                gameLoop.start();
                placePipeTimer.start();
            }
            // change the speed for more fun
            velocityY = -9 + random.nextInt(3);
            if(gameOver) {
                playSound(gameOverSoundPath);
                placePipeTimer.stop();
                gameLoop.stop();

            }
        }
    }

// Sound for the game, logic version
    public FlappyBird playSound(String soundPath) {
        try {
            File soundFile = new File(soundPath);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.setFramePosition(0);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.out.println();
        }
        return null;
    }


        }