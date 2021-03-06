package ponts.physique.liaisons;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import ponts.ihm.Box2D;
import ponts.physique.ObjetPhysique;
import ponts.physique.barres.Barre;
import ponts.physique.environnement.Bord;

/**
 * Classe abstraite d'une liaison
 */
public abstract class Liaison extends ObjetPhysique {

    public static final int CATEGORY = 0b0100;
    public static final int MASK = Bord.CATEGORY;

    public static final float RAYON = 0.5f;
    private static final float RAYON_CLICK = RAYON * 3;

    protected Color couleurRemplissage;
    private Color couleurSurvolee;
    private Color couleurContour;

    private LinkedList<Barre> barresLiees;
    protected boolean cliquee;
    private CircleShape shape;

    protected boolean apercu;

    /**
     * Constructeur d'une liaison
     * 
     * @param world
     * @param pos
     */
    protected Liaison(World world, Vec2 pos) {

        couleurContour = Color.BLACK;
        couleurSurvolee = Color.decode("#e86933");

        barresLiees = new LinkedList<Barre>();

        cliquee = false;

        creerObjetPhysique(world);
        setPos(pos);

    }

    protected void creerObjetPhysique(World world, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;

        body = world.createBody(bodyDef);
        shape = new CircleShape();
        shape.setRadius(RAYON);

        FixtureDef fixtureDef = creerFixtureDef(FRICTION, ELASTICITE, DENSITE, CATEGORY, MASK);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }

    /**
     * Active la physique de liaison une fois la création de la barre associée
     * terminée
     */
    public abstract void activerPhysique();

    /**
     * Dessine la liaison
     * 
     * @param g
     * @param box2d
     * @param estSurvolee
     */
    public void dessiner(Graphics g, Box2D box2d, boolean estSurvolee) {

        int x = box2d.worldToPixelX(getX());
        int y = box2d.worldToPixelY(getY());
        int r = box2d.worldToPixel(RAYON);

        int alpha = apercu ? 100 : 255;
        couleurRemplissage = ObjetPhysique.setColorAlpha(couleurRemplissage, alpha);
        couleurContour = ObjetPhysique.setColorAlpha(couleurContour, alpha);
        couleurSurvolee = ObjetPhysique.setColorAlpha(couleurSurvolee, alpha);

        g.setColor(estSurvolee ? couleurSurvolee : couleurRemplissage);
        g.fillOval(x - r, y - r, r * 2, r * 2);
        g.setColor(couleurContour);
        g.drawOval(x - r, y - r, r * 2, r * 2);
    }

    /**
     * Calcule la distance de la liaison à un point
     * 
     * @param pos
     * @return
     */
    public float distancePoint(Vec2 pos) {
        Vec2 centre = getPos();
        return (centre.sub(pos).length());
    }

    /**
     * Teste si la liaison à été cliquée
     * 
     * @param pos
     * @return
     */
    public boolean testLiaisonCliquee(Vec2 pos) {
        return (distancePoint(pos) <= RAYON_CLICK);
    }

    public LinkedList<Barre> getBarresLiees() {
        return barresLiees;
    }

}
