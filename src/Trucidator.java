import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;


/**
 * Classe representant le personnage dote d'une IA qui poursuit notre heros.
 * @author Colin Scarato 
 */
public class Trucidator {
	
	private static Point[] d = new Point[] {new Point(1,0), new Point(0,-1), new Point(-1,0), new Point(0,1)};//directions
	private int direction;
	private int vitesse;
	public Rectangle box;//position et hitbox du perso
	private Rectangle box2;//pour simuler les collisions
	private ArrayList<Integer> chemin;//chemin que suit l'ennemi si en mode poursuite
	private int cptChemin;//vaut -1 si en mode normal, sinon donne le nombre de pixels restant a parcourir sur la case actuelle
	private BufferedImage[] stillImages;//sprites de l'ennemi
	private Map car;//la carte de jeu
	
	
	/**
	 * Renvoie une instance de Trucidator.
	 * @param p La position initiale
	 * @param vit Le bonus de vitesse (determine par le niveau)
	 * @param c Un pointeur vers la carte de jeu
	 */
	public Trucidator (Point p, int vit, Map c) {
		
		direction = (int)(Math.random()*4);
		vitesse = 3+vit;
		box = new Rectangle (p.x, p.y, 30, 30);
		box2 = new Rectangle (0, 0, 30, 30);
		chemin = new ArrayList<Integer>();
		cptChemin = -1;
		car= c;
		stillImages= new BufferedImage[4];
        stillImages[1]= Affichage.ouvrirImage("/EnemyImages/Back.png");
        stillImages[0]= Affichage.ouvrirImage("/EnemyImages/Right.png");
        stillImages[3]= Affichage.ouvrirImage("/EnemyImages/Front.png");
        stillImages[2]= Affichage.ouvrirImage("/EnemyImages/Left.png");
	}
	
	/**
	 * Affiche l'ennemi sur la fenetre.
	 * @param g Graphiques de l'image de fond
	 * @param imgObs Pointeur vers la fenetre
	 */
	public void afficher (Graphics g, ImageObserver imgObs) {
		g.drawImage(stillImages[direction], box.x+3, box.y, imgObs);
	}
	
	/**
	 * Deplace l'ennemi d'un nombre de pixels correspondant a sa vitesse,
	 * avec gestion des collisions avec le labyrinthe.
	 * Deux modes:
	 * aleatoire - deplacements aleatoires automatiques sur la carte
	 * poursuite - deplacement vers une direction donnee (la position du joueur quand il est repere)
	 */
	public void move () {
		for (int i=0; i<vitesse; i++) {//deplacement d'un nombre variable de pixels par tour avec affichage seulement a la fin
			deplR(d[direction], box2);
			if (cptChemin==-1) {//si mode aleatoire
				if (car.collision(box2)) {//si collision dans la direction actuelle
					direction = (direction+2)%4;//on se prepare a repartir dans l'autre sens
				}
				if ((int)Math.random()==0) {//Une chance sur deux d'essayer de changer de direction
					if (box.x%Map.TAILLE_MUR==0 && box.y%Map.TAILLE_MUR==0) {//et si on est pile sur une case (pas de retour en arriere donc pas d'autre direction) - condition separee de la precedente pour reduire les calculs
						ArrayList<Integer> dPossibles = dPossibles();//listage des directions possibles
						direction = dPossibles.get((int)(Math.random()*dPossibles.size()));
					}
				}
			} else {
				if (cptChemin==0) {//si en mode poursuite et exactement sur une case
					if (chemin.size()>0) {//s'il reste du chemin a parcourir
						cptChemin = Map.TAILLE_MUR-1;//nombre de pixels avant la prochaine case
						direction = chemin.get(0);//direction suivante dans la liste
						chemin.remove(0);
					} else {//si on est arrive
						cptChemin = -1;//on repasse en mode auto
						i--;
						continue;//et on recommence cette iteration de la boucle avec le mode auto
					}
				} else {//si en mode poursuite entre deux cases
					cptChemin--;//on decremente le compte de pixels restants avant la nouvelle case
				}
			}
			deplR(d[direction], box);//deplacement
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
	 * Listage des differentes directions accessibles (ne permet pas le retour en arriere)
	 * @return dPossibles Une liste de directions
	 */
	public ArrayList<Integer> dPossibles () {
		ArrayList<Integer> dPossibles = new ArrayList<Integer>();
		for (int j=0; j<4; j++) {
			if (j==(direction+2)%4) {//si j correspond a un retour en arriere on ne le compte pas
				continue;
			}
			deplR(d[j], box2);
			if (car.collision(box2)==false) {
				dPossibles.add(new Integer(j));//si pas de collision dans la direction j on ajoute j a la liste
			}
		}
		return dPossibles;
	}
	/**
	 * Initialise une liste de directions a suivre pour atteindre une position donnee,
	 * en evitant les murs (plus de calcul de collision necessaire en mode poursuite dans la methode move).
	 * @param arrivee La position cible
	 */
	public void creerChemin (Point arrivee) {
		
		int[][] carteInt = car.toInt();//representation simplifiee de la carte sous forme de tableau d'entiers
		carteInt[(box.x+Map.TAILLE_MUR/2)/Map.TAILLE_MUR][(box.y+Map.TAILLE_MUR/2)/Map.TAILLE_MUR] = -3;//la position de l'ennemi prend la valeur -3
		if (carteInt[arrivee.x/Map.TAILLE_MUR][arrivee.y/Map.TAILLE_MUR]==-1) {
			carteInt[arrivee.x/Map.TAILLE_MUR][arrivee.y/Map.TAILLE_MUR] = 0;//la position d'arrivee prend la valeur 0
		} else {
			return;
		}
		//les espaces libres sont notes -1 et les murs -2
		
		chemin.clear();//on reinitialise le chemin actuel
		int i2=0, j2=0;
		int n=0, i=0, j=0, k=0;
		chemin://cette boucle initialise les cases libres avec un entier n representant leur distance en nombre de cases par rapport a l'arrivee
		for (n=0; n<carteInt.length*carteInt[0].length; n++) {//boucle sur n, avec un maximum pour le nombre de cases de la carte (atteint si pas de chemin possible)
			for (i=0; i<carteInt.length; i++) {//on parcourt la carte
				for (j=0; j<carteInt[0].length; j++) {
					if (carteInt[i][j]==n) {//si on est sur une case a la distance n de l'arrivee
						for (k=0; k<4; k++) {
							i2 = i+d[k].x; j2=j+d[k].y;//on regarde les  cases adjacentes
							if (i2>=0 && i2<carteInt.length && j2>=0 && j2<carteInt[0].length) {
								if (carteInt[i2][j2]==-3) {//si on a atteint l'ennemi alors on a assez d'informations et on arrete
									i=i2; j=j2;
									break chemin;
								}
								if (carteInt[i2][j2]==-1) {//si la case adjacente est libre et pas encore traitee
									carteInt[i2][j2]=n+1;//alors sa distance vaut n+1
								}
								
							}
						}
					}
				}
			}
		}
		
		if (n==carteInt.length*carteInt[0].length) {
			return;//si pas de chemin possible on abandonne
		}
		
		//on determine la direction vers laquelle se deplacer et la distance pour atteindre la premiere case (la plus proche)
		i2 = Map.TAILLE_MUR*i2-box.x; j2 = Map.TAILLE_MUR*j2-box.y;
		if (i2==0) {
			direction = (j2<0)?1:3;
			cptChemin = Math.abs(j2);
		} else {
			direction = (i2<0)?2:0;
			cptChemin = Math.abs(i2);
		}
		
		for (int m=n; m>=0; m--) {//on repart du joueur pour trouver un chemin en descendant les valeurs de n jusqu'a 0 (arrivee)
			for (k=(int)(Math.random()*4);k<7;k++) {//pour pouvoir choisir tous les chemins possibles
				i2 = i+d[k%4].x; j2=j+d[k%4].y;
				if (i2>=0 && i2<carteInt.length && j2>=0 && j2<carteInt[0].length) {
					if (carteInt[i2][j2]==m) {//on trouve une case adjacente pour continuer le chamin
						break;
					}
				}
			}
			i=i2; j=j2;
			chemin.add(k%4);//on ajoute la distance correspondant a la liste
		}
		
		//on evite de revenir en arriere si la case suivante a atteindre est dans l'autre direction
		//pour eviter l'effet de tremblement de l'ennemi qui voit en permancence le joueur
		//et revient sur sa case a chaque nouveau chemin
		if ((direction+2)%4==chemin.get(0)) {
			direction = (direction+2)%4;
			cptChemin = 30 - cptChemin;
			chemin.remove(0);
			
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
