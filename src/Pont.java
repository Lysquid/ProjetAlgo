import java.awt.Graphics;
import java.util.LinkedList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Pont {

    LinkedList<Barre> barres;
    LinkedList<Liaison> liaisons;
    Barre barreEnCreation;
    Liaison liaisonEnCreation;

    public Pont(World world, Box2D box2d) {

        barres = new LinkedList<Barre>();
        liaisons = new LinkedList<Liaison>();

        liaisons.add(new LiaisonFixe(world, new Vec2(box2d.largeur * 0.5f, box2d.hauteur * 0.05f)));
        liaisons.add(new LiaisonFixe(world, new Vec2(box2d.largeur * 0.15f, box2d.hauteur * 0.4f)));
        liaisons.add(new LiaisonFixe(world, new Vec2(box2d.largeur * 0.86f, box2d.hauteur * 0.43f)));

    }

    public void dessiner(Graphics g, Box2D box2d) {
        LinkedList<Barre> barresDesinees = new LinkedList<Barre>();
        for (Liaison liaison : liaisons) {
            for (Barre barre : liaison.barresLiees) {
                if (!barresDesinees.contains(barre)) {
                    barre.dessiner(g, box2d);
                    barresDesinees.add(barre);
                }
            }
            liaison.dessiner(g, box2d);
        }
    }

    public void gererInput(World world, Vec2 posSouris, String boutonSouris, boolean clicSouris, Materiau materiau) {

        Liaison liaisonProche = testLiaisonProche(posSouris);

        boolean barreValide = barreValide(barreEnCreation, liaisonProche);

        if (barreEnCreation != null) {
            if (liaisonProche == null || !barreValide) {
                majPreview(posSouris);
            } else {
                majPreview(liaisonProche.getPos());
            }
        }

        if (clicSouris) {

            switch (boutonSouris) {

                case "gauche":

                    // Test si la barre vérifie des conditions
                    if (barreValide) {
                        if (liaisonProche != null) {
                            Liaison liaisonASupprimer = barreEnCreation.accrocher(world, liaisonProche);
                            liaisons.remove(liaisonASupprimer);
                        } else {
                            liaisonProche = liaisonEnCreation;
                        }
                        lacherBarre(world);
                    }

                    // Cas ou on doit creer une nouvelle barre
                    if (barreEnCreation == null && liaisonProche != null) {
                        creerBarre(world, posSouris, liaisonProche, materiau);
                    }

                    break;

                case "droite":
                    if (barreEnCreation != null) {
                        arreterCreation(world);
                    } else {
                        supprimerBarresCliquees(world, posSouris);
                    }

                    break;

                default:

                    break;
            }
        }

    }

    private boolean barreValide(Barre barre, Liaison liaisonProche) {
        // barre n'existe pas
        if (barre == null)
            return false;
        // taille minimum
        if (!barre.tailleMinimum())
            return false;
        // si elle est proche d'une liaison existante
        if (liaisonProche != null) {
            // la barre reliant les deux liaisons n'existe pas deja
            if (barreExisteDeja(barre, liaisonProche))
                return false;
            // la barre ne boucle pas sur elle même
            if (barre.liaisonsLiees.contains(liaisonProche))
                return false;
        }
        return true;
    }

    private boolean barreExisteDeja(Barre barreATester, Liaison liaison2) {
        if (liaison2 == null) {
            return false;
        }
        Liaison liaison1 = barreATester.liaisonsLiees.get(0);
        for (Barre barre : liaison1.barresLiees) {
            if (barre.liaisonsLiees.contains(liaison2)) {
                return true;
            }
        }
        return false;
    }

    public void arreterCreation(World world) {
        supprimerBarre(world, barreEnCreation);
        barreEnCreation = null;
        liaisonEnCreation = null;
    }

    private void supprimerBarre(World world, Barre barre) {
        LinkedList<LiaisonMobile> liaisonASupprimer = barre.supprimer(world);
        barres.remove(barreEnCreation);
        liaisons.removeAll(liaisonASupprimer);
    }

    private void lacherBarre(World world) {
        barreEnCreation.activerPhysique(world);

        for (Liaison liaison : barreEnCreation.liaisonsLiees) {
            liaison.activerPhysique();
            barreEnCreation.lier(world, liaison);
        }

        liaisonEnCreation = null;
        barreEnCreation = null;

    }

    private void majPreview(Vec2 posSouris) {
        liaisonEnCreation.setPos(posSouris);
        barreEnCreation.ajusterPos();
    }

    public Liaison testLiaisonProche(Vec2 posClic) {
        Liaison liaisonPlusProche = null;
        float distanceMin = Float.POSITIVE_INFINITY;
        for (Liaison liaison : liaisons) {
            if (liaison.testLiaisonCliquee(posClic) && liaison != liaisonEnCreation) {
                float distance = liaison.distancePoint(posClic);
                if (distance < distanceMin) {
                    distanceMin = distance;
                    liaisonPlusProche = liaison;
                }
            }
        }
        return liaisonPlusProche;
    }

    public void creerBarre(World world, Vec2 posClic, Liaison liaisonCliquee, Materiau materiau) {

        liaisonEnCreation = new LiaisonMobile(world, posClic);
        liaisons.add(liaisonEnCreation);

        Liaison liaison1 = liaisonCliquee;
        Liaison liaison2 = liaisonEnCreation;

        switch (materiau) {
            case BOIS:
                barreEnCreation = new BarreBois(world, liaison1, liaison2);
                break;
            case GOUDRON:
                barreEnCreation = new BarreGoudron(world, liaison1, liaison2);
                break;
        }

        barres.add(barreEnCreation);

    }

    public void testCasse(World world, float dt) {
        for (Barre barre : barres) {
            if (barre != barreEnCreation) {
                LinkedList<Liaison> liaisonsCrees = barre.testCasse(world, dt);
                liaisons.addAll(liaisonsCrees);
            }
        }

    }

    public void supprimerBarresCliquees(World world, Vec2 posClic) {
        for (Barre barre : barres) {
            if (barre.testBarreCliquee(posClic)) {
                supprimerBarre(world, barre);
            }
        }
    }

}
