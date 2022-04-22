package ponts.physique;

import java.awt.Graphics2D;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import ponts.ihm.Box2D;
import ponts.niveau.Niveau;
import ponts.physique.barres.Materiau;
import ponts.physique.environnement.Bord;
import ponts.physique.voiture.Voiture;

public class Partie {

    World world;
    Pont pont;
    Bord bord;
    Voiture voiture;
    int budget;

    private boolean simulationPhysique = false;
    Materiau materiau = Materiau.GOUDRON;

    public Partie(Box2D box2d, Niveau niveau) {
        niveau.centrer(box2d);
        niveau.ajouterExtremitees(box2d);

        Vec2 gravity = new Vec2(0.0f, -9.81f);
        world = new World(gravity);

        bord = new Bord(world, niveau);
        pont = new Pont(world, niveau);
        voiture = new Voiture(world, niveau);
        budget = niveau.getBudget();

    }

    public boolean isSimulationPhysique() {
        return simulationPhysique;
    }

    public void toggleSimulationPhysique() {
        simulationPhysique = !simulationPhysique;
        pont.arreterCreation(world);
    }

    public void dessiner(Graphics2D g, Box2D box2d, Vec2 posSouris) {
        bord.dessiner(g, box2d);
        pont.dessiner(g, box2d, posSouris);
        voiture.dessiner(g, box2d);
    }

    public void tickPhysique(Vec2 posSouris, int boutonSouris, boolean clicSouris, float dt) {

        boolean sourisDansBord = bord.estDansBord(posSouris);
        pont.gererInput(world, posSouris, boutonSouris, clicSouris, materiau, sourisDansBord);

        if (isSimulationPhysique()) {
            pont.testCasse(world, dt);
            world.step(dt, 10, 8);

        }

    }

    public void changementMateriau(Materiau materiau) {
        this.materiau = materiau;
        pont.arreterCreation(world);

    }

    public int prix() {
        return pont.prix();
    }

    public int budget() {
        return budget;
    }

}
