import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

public class Main extends JFrame {

    final int WIDTH = 600;
    final int HEIGHT = 600;
    int refreshRate;

    public Main() {
        setTitle("Project");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        refreshRate = getRefreshRate();

        Jeu jeu = new Jeu();
        add(jeu);

        setVisible(true);
        jeu.init(refreshRate);

    }

    public int getRefreshRate() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            DisplayMode dm = gs[0].getDisplayMode();
            return dm.getRefreshRate();
        } catch (Exception e) {
            e.printStackTrace();
            return 60;
        }

    }

    public static void main(String[] args) {
        new Main();
    }

}
