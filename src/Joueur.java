import java.awt.*;
import java.awt.image.*;

/**
 * Classe representant le joueur
 * @author Karim Gharoual
 * @author Colin Scarato
 */
public class Joueur {
	
	private static Point[] d = new Point[] {new Point(1,0), new Point(0,-1), new Point(-1,0), new Point(0,1)};//directions
	private int direction, dir1, dir2, vitesse;
	private Rectangle box, box2; // hitbox
	private BufferedImage vision, grandeVision;
	boolean bonusVision;
	private BufferedImage[] stillImages;
	private Map carte;
	
	/**
	 * Constructeur du joueur
	 * @param a position
	 * @param c carte
	 */
	public Joueur (Point a, Map c) {
		box = new Rectangle (a.x, a.y, Map.TAILLE_MUR, Map.TAILLE_MUR);
		box2 = new Rectangle (0, 0, Map.TAILLE_MUR, Map.TAILLE_MUR);
		direction= 0;
		dir1=-1; 
		dir2=-1;
		vitesse= 9;
		bonusVision= false;
		carte= c;
        vision= Affichage.ouvrirImage("vision.png");
        grandeVision= Affichage.ouvrirImage("extraVision.png");
        stillImages= new BufferedImage[4];
        stillImages[1]= Affichage.ouvrirImage("/PlayerImages/Back.png");
        stillImages[0]= Affichage.ouvrirImage("/PlayerImages/Right.png");
        stillImages[3]= Affichage.ouvrirImage("/PlayerImages/Front.png");
        stillImages[2]= Affichage.ouvrirImage("/PlayerImages/Left.png");
	}
	
	/**
	 * Affichage
	 * @param gB objet graphics de l'image a afficher
	 * @param obs pointeur vers la fenetre
	 */
	public void affichage (Graphics gB, ImageObserver obs) {
		gB.drawImage(stillImages[direction], box.x+8, box.y+7, obs);
		
		if (bonusVision) {
			gB.drawImage(grandeVision, box.x-Map.NB_CASES*Map.TAILLE_MUR+Map.TAILLE_MUR/2, box.y-Map.NB_CASES*Map.TAILLE_MUR+Map.TAILLE_MUR/2, obs);
		} else {
			gB.drawImage(vision, box.x-Map.NB_CASES*Map.TAILLE_MUR+Map.TAILLE_MUR/2, box.y-Map.NB_CASES*Map.TAILLE_MUR+Map.TAILLE_MUR/2, obs);
		}
	}

	/**
	 * Mouvement du joueur
	 */	
	public void deplacer () {
		for (int i=0; i<vitesse; i++) {
			if (dir1!=-1) { // si il y  a une direction principale souhaitee
				deplR(d[dir1], box2);
				if (carte.collision(box2)) { // si il y a une collision dans cette direction
					if (dir2==-1) { // on regarde si il n'y a pas de direction secondaire
						box2.x = Map.TAILLE_MUR*((box.x+Map.TAILLE_MUR/2)/Map.TAILLE_MUR)-box.x;//on regarde la position de la case entiere la plus proche
						box2.y = Map.TAILLE_MUR*((box.y+Map.TAILLE_MUR/2)/Map.TAILLE_MUR)-box.y;
						if (box2.x==0) {//on enregistre sa direction comme direction de deplacement
							direction = (box2.y<0)?1:3;
						} else {
							direction = (box2.x<0)?2:0;
						}
						box2.x += box.x+d[dir1].x; box2.y += box.y+d[dir1].y;//on regarde si il y a collision en partant de cette case
						if (carte.collision(box2)) {//si collision on essaye la case de l'autre cote
							box2.x = box.x - d[direction].x*Map.TAILLE_MUR + d[dir1].x;
							box2.y = box.y - d[direction].y*Map.TAILLE_MUR + d[dir1].y;
							if (carte.collision(box2)) {//si encore collision on reste immobile dans la dir principale (dans le mur)
								direction = dir1;
							}
						}
					} else {//si il y a une direction secondaire (en sachant que collision dans la dir principale)
						direction = dir2;
					}
				} else {//si pas de collision dans la direction principale on avance
					direction = dir1;
				}
				deplR(d[direction], box2); // on essaie de se deplacer dans la direction choisie
				if (carte.collision(box2)==false) {
					deplR(d[direction], box); // deplacement
				}
			}
		}
	}
	
	/**
	 * Deplace un rectangle dans une direction par rapport a la position de la hitbox
	 * @param p position
	 * @param b2 boite que l'on deplace
	 */	
	public void deplR (Point p, Rectangle b2) {
		b2.x = box.x + p.x;
		b2.y = box.y + p.y;
	}
	
	/**
	 * Ajoute une direction principale ou secondaire suite a l'appui sur une touche
	 * @param D direction correspondante
	 */	
	public void ajouterDir(int D) {
		if (!(dir1==-1 || dir1%2==D%2)) {
			dir2=dir1;
		}
		dir1=D;
	}
	
	/**
	 * Supprime une direction si elle existe et adapte en consequence les directions de deplacement
	 * @param D direction correspondante
	 */	
	public void supprimerDir(int D) {
		if (dir2==D) {
			dir2=-1;
		} else {
			if (dir1==D) {
				dir1=dir2;
				dir2=-1;
			}
		}
	}
	
	/**
	 * Renvoie la position
	 */	
	public Point getPosition () {
		return new Point(box.x, box.y);
	}
	
	/**
	 * Renvoie un pointeur vers la hitbox
	 */
	public Rectangle getHitbox () {
		return box;
	}
	
	/**
	 * Modifie la vitesse
	 * @param i Increment de vitesse
	 */
	public void influencerVitesse (int i) {
		vitesse+= i;
		if (vitesse < 0) {
			vitesse= 0;
		}
	}
	
	/**
	 * Renvoie la position x de la hitbox
	 */
	public int getX () {
		return box.x;
	}
	
	/**
	 * Renvoie la position y de la hitbox
	 */
	public int getY () {
		return box.y;
	}
}

