import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Cette classe represente les objets/bonus que le joueur peut ramasser pendant le jeu;
 * 
 * @author Vincent
 * @author Colin
 */
public class Objet {
	
	private final static int NB_OBJETS = 5;
	private static Image[] images = initImages();
	private int id;
	private Rectangle box;
	private boolean consome;
	
	/**
	 * Constructeur de la classe
	 * 
	 * @param p
	 * 		Point ou faire apparaitre l'objet
	 * 
	 */
	public Objet (Point p) {
		id = (int)(Math.random()*NB_OBJETS);
		box = new Rectangle (p.x, p.y, Map.TAILLE_MUR, Map.TAILLE_MUR);
		consome= false;
	}
	
	/**
	 * Methode qui recupere les differentes images des objets
	 * 
	 * @return 
	 * 		Tableau d'images 
	 */
	private static Image[] initImages() {
		Image[] t = new Image[NB_OBJETS];
		for (int i=0; i<t.length; i++) {
			t[i] = Affichage.ouvrirImage("objet" + i + ".png");
		}
		return t;
	}
	
	/**
	 * Methode qui affiche l'objet
	 * 
	 * @param g, imgObs
	 * 		Objet graphics et Image observer pour dessiner des images
	 */
	public void afficher (Graphics g, ImageObserver imgObs) {
		if (!consome) {
			g.drawImage(images[id], box.x, box.y, imgObs);
		}
	}
	
	/**
	 * Methode qui applique l'effet de l'objet 
	 * 
	 * @param j
	 * 		Joueur
	 * @param t
	 * 		Ennemi
	 * @param c
	 * 		Carte actuelle
	 * @param a
	 * 		Classe de gestion
	 */
	public void appliquer (Joueur j, Trucidator t, Map c, Affichage a) {
		if (!consome) {
			consome= true;
			switch (id) {
				case 0:
					j.bonusVision= true;
					a.jouerSon("positif.wav");
					break;
				case 1:
					if (Math.random()*10 > 3) {
						a.setTempsAffEnnemi(2000);
						a.jouerSon("positif.wav");
					} else {
						t.creerChemin(j.getPosition());
						a.setTempsAffJoueur(2000);
						a.jouerSon("negatif.wav");
					}
					break;
				case 2:
					if (Math.random()*10 > 2) {
						t.influencerVitesse(-1);
						a.jouerSon("positif.wav");
					} else {
						j.influencerVitesse(-1);
						a.jouerSon("negatif.wav");
					}
					break;
				case 3: 
					if (Math.random()*10 > 3) {
						a.setTempsAffSortie(2000);
						a.jouerSon("positif.wav");
					} else {
						a.setTempsTetanise(2000);
						a.jouerSon("negatif.wav");
					}
					break;
				case 4: 
					if (Math.random()*10 > 2) {
						j.influencerVitesse(2);
						a.jouerSon("positif.wav");
					} else {
						t.influencerVitesse(2);
						a.jouerSon("negatif.wav");
					}
					break;
				default:
					return;
			}
		}
	}
	
	/**
	 * Accesseur de la position de l'objet
	 * 
	 * @return
	 * 		La position sous forme d'un point
	 */
	public Point getPosition () {
		return new Point(box.x, box.y);
	}
	
	/**
	 * Accesseur de la hitbox de l'objet
	 * 
	 * @return
	 * 		Un rectangle representant la hitbox de l'objet
	 */
	public Rectangle getHitbox () {
		return box;
	}
	
}
