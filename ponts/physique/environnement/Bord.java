package ponts.physique.environnement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.LinkedList;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import ponts.ihm.Box2D;
import ponts.niveau.Niveau;
import ponts.physique.ObjetPhysique;
import ponts.physique.barres.Barre;
import ponts.physique.liaisons.Liaison;
import ponts.physique.voiture.Voiture;

/**
 * Classe des bords du niveau, c'est à dire la berge sur laquelle il faut
 * construire le pont
 */
public class Bord extends ObjetPhysique {

    public static final int CATEGORY = 0b0001;
    public static final int MASK = Voiture.CATEGORY | Barre.CATEGORY | Liaison.CATEGORY;

    private Color couleurRemplissage = Color.decode("#49a03f");
    private Color couleurContour = Color.BLACK;

    private LinkedList<Vec2> posCoins;

    /**
     * Constructeur d'un objet bord
     * 
     * @param world
     * @param niveau
     */
    public Bord(World world, Niveau niveau) {
        posCoins = niveau.getPosCoins();
        creerObjetPhysique(world);

    }

    @Override
    protected void creerObjetPhysique(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        FixtureDef fixtureDef = creerFixtureDef(FRICTION, ELASTICITE, DENSITE, CATEGORY, MASK);

        Vec2 prevPosCoin = null;
        for (Vec2 posCoin : posCoins) {

            if (prevPosCoin != null) {
                Body body = world.createBody(bodyDef);
                EdgeShape shape = new EdgeShape();
                shape.set(prevPosCoin, posCoin);
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);

            }
            prevPosCoin = posCoin;

        }
    }

    /**
     * Détermine si un point est dans le polygone défini par le bord
     * 
     * @param pos
     * @return boolean
     */
    public boolean estDansBord(Vec2 pos) {
        Polygon polygon = new Polygon();
        for (Vec2 posCoin : posCoins) {
            int x = Math.round(posCoin.x * 1000);
            int y = Math.round(posCoin.y * 1000);
            polygon.addPoint(x, y);
        }
        int x = Math.round(pos.x * 1000);
        int y = Math.round(pos.y * 1000);
        return polygon.contains(x, y);
    }

    /**
     * Dessine la berge
     * 
     * @param g
     * @param box2d
     */
    public void dessiner(Graphics2D g, Box2D box2d) {
        Polygon polygon = new Polygon();
        for (Vec2 posCoin : posCoins) {
            int x = box2d.worldToPixelX(posCoin.x);
            int y = box2d.worldToPixelY(posCoin.y);
            polygon.addPoint(x, y);
        }
        g.setColor(couleurRemplissage);
        g.fillPolygon(polygon);
        g.setColor(couleurContour);
        g.drawPolygon(polygon);

    }
}