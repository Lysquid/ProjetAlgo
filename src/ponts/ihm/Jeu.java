package ponts.ihm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import org.jbox2d.common.Vec2;

import ponts.niveau.Niveau;
import ponts.physique.Partie;
import ponts.physique.barres.Materiau;

public class Jeu extends JPanel implements ActionListener, MouseInputListener {

    Timer timerGraphique;
    Timer timerPhysique;
    static final int TICK_PHYSIQUE = 16;
    long tempsPhysique;

    JComboBox<String> comboBoxNiveaux;
    JButton boutonLancer;
    JButton boutonRecommencer;
    JButton boutonMateriauBois;
    JButton boutonMateriauGoudron;
    JLabel textPrix;
    JLabel textBudget;

    Box2D box2d;
    Partie partie;

    int boutonSouris;
    boolean clicSouris = false;
    Vec2 posSouris = new Vec2();

    long tempsPrecedent = 0;

    public Jeu(JFrame fenetre, int refreshRate) {

        ihm(fenetre);

        box2d = new Box2D(getWidth(), getHeight());
        partie = new Partie(box2d, recupererNiveau());

        timerPhysique = new Timer(TICK_PHYSIQUE, this);
        timerPhysique.start();
        tempsPhysique = System.currentTimeMillis();

        int fps = (int) (1.0 / refreshRate * 1000.0);
        timerGraphique = new Timer(fps, this);
        timerGraphique.start();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void ihm(JFrame fenetre) {
        String[] nomsNiveaux = new File(Niveau.CHEMIN.toString()).list();
        comboBoxNiveaux = new JComboBox<String>(nomsNiveaux);
        comboBoxNiveaux.addActionListener(this);
        add(comboBoxNiveaux);

        boutonLancer = new JButton("Lancer");
        boutonLancer.addActionListener(this);
        add(boutonLancer);

        boutonRecommencer = new JButton("Recommencer");
        boutonRecommencer.addActionListener(this);
        add(boutonRecommencer);

        boutonMateriauBois = new JButton("Bois");
        boutonMateriauBois.addActionListener(this);
        add(boutonMateriauBois);

        boutonMateriauGoudron = new JButton("Goudron");
        boutonMateriauGoudron.addActionListener(this);
        add(boutonMateriauGoudron);

        textPrix = new JLabel();
        add(textPrix);

        textBudget = new JLabel();
        add(textBudget);

        // Pour une raison extremement obscure, sur Linux, le planel
        // se place bien seulement si on l'ajoute deux fois
        fenetre.add(this);
        fenetre.remove(this);
        fenetre.add(this);
        fenetre.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g0) {

        // Activer l'anti-alias
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fonction pour améliorer l'affichage sur Mac/Linux
        Toolkit.getDefaultToolkit().sync();

        g.setColor(Color.decode("#55a3d4"));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (partie != null) {
            majTextLabels();
            partie.dessiner(g, box2d, posSouris);
        }
    }

    private void majTextLabels() {
        textPrix.setText("Prix : " + Integer.toString(partie.prix()) + " $");
        textBudget.setText("Budget : " + Integer.toString(partie.budget()) + " $");
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == timerPhysique) {

            long nouveauTempsPhysique = System.currentTimeMillis();
            float dt = (nouveauTempsPhysique - tempsPhysique) / 1000f;
            tempsPhysique = nouveauTempsPhysique;

            partie.tickPhysique(posSouris, boutonSouris, clicSouris, dt);
            clicSouris = false;

        }

        if (e.getSource() == timerGraphique) {
            repaint();
        }

        if (e.getSource() == boutonLancer) {
            partie.toggleSimulationPhysique();
            if (partie.isSimulationPhysique()) {
                boutonLancer.setText("Arreter");
            } else {
                boutonLancer.setText("Lancer");
            }
        }

        if (e.getSource() == boutonRecommencer) {
            partie = new Partie(box2d, recupererNiveau());
        }

        if (e.getSource() == comboBoxNiveaux) {
            partie = new Partie(box2d, recupererNiveau());
        }

        Materiau materiau = null;
        if (e.getSource() == boutonMateriauBois) {
            materiau = Materiau.BOIS;
        }
        if (e.getSource() == boutonMateriauGoudron) {
            materiau = Materiau.GOUDRON;
        }
        if (materiau != null) {
            partie.changementMateriau(materiau);
        }

    }

    private Niveau recupererNiveau() {
        boutonLancer.setText("Lancer");
        String nomNiveau = (String) comboBoxNiveaux.getSelectedItem();
        return Niveau.charger(nomNiveau);

    }

    public void majPosSouris(MouseEvent e) {
        posSouris = box2d.pixelToWorld(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        majPosSouris(e);
        boutonSouris = e.getButton();
        clicSouris = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        majPosSouris(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        majPosSouris(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        majPosSouris(e);
    }
}
