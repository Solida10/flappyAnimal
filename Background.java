import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public static void main(String[] args)  {
    // Board Information
    int boardWidth = 360;
    int boardHeight = 640;
    // Image of Screen
    File imageFile = new File("C:\\Users\\tdane\\Downloads\\game screen.jpg");
    BufferedImage originalImage = null;
    try {
        originalImage = ImageIO.read(imageFile);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    // Resizing Option Screen
    int newWidth = 100;
    int newHeight = 100;
    Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

    ImageIcon icon = new ImageIcon(resizedImage);
    // Characters that can be chosen
    String[] options = {"Faby (Bird)", "Nyan Cat", "Parrot", "Penguin"};
    int choice = JOptionPane.showOptionDialog(null, "Welcome to Flappy Animal! Play by " +
            "using your space bar " +  "\n " + " Choose an option to begin:", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);

    String selectedCharacter = (choice >= 0) ? options[choice] : options[0];

    JFrame frame = new JFrame("Flappy Animal");
    FlappyBird flappyBird = new FlappyBird(selectedCharacter);
    frame.setVisible(true);
    frame.setLocation(100,100);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(flappyBird);
    frame.pack();

}