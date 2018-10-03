import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.Rectangle;
 
/**
 * Cette classe gère la carte, donne des informations pour la gestion des déplacements des joueurs
 * 
 * @author Vincent
 */
public class Map {
	
	public static final int TAILLE_MUR= 30;
	public static final int NB_CASES= 23;
	private int height, width;
	private ArrayList<Rectangle> murs;
	private BufferedImage imageMurs, imageFond;
	private int[][] carteInt;
	
	/**
	 * Constructeur de la classe, initialise la taille de la carte, et place les murs selon un schéma contenu dans un fichier texte 
	 * 
	 */
	public Map (boolean custom) {
		
		height= TAILLE_MUR*NB_CASES;
		width= height;
		
		murs= new ArrayList<Rectangle>();
		
		imageMurs= Affichage.ouvrirImage("murs.png");
		imageFond= Affichage.ouvrirImage("fond.png");
		
		if (!custom) {
			Generateur.aleaGen();
		}
		genMurs();
        
	}
	
	/**
	 * Méthode informant sur la collision des personnages avec les murs
	 * 
	 * @param r 
	 * 		hitbox du personnage
	 * 
	 * @return 
	 * 		vrai si collision, faux sinon 
	 * 
	 */
	public boolean collision (Rectangle r) {
		for (int i= 0; i<murs.size(); i++) {
			if (murs.get(i).intersects(r)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Méthode informant l'ennemi si il peut voir ou non le joueur
	 * 
	 * @param x0, y0, x1, y1
	 * 		positions respectives de l'ennemi et du joueur
	 * 
	 * @return 
	 * 		vrai si vision, faux sinon
	 * 
	 */
	public boolean vision (int x0, int y0, int x1, int y1) {
		Line2D[] l= new Line2D[4];
		boolean[] b= new boolean[4];
		l[0]= new Line2D.Double(x0+1, y0+1, x1+15, y1+15);
		l[1]= new Line2D.Double(x0+1, y0+29, x1+15, y1+15);
		l[2]= new Line2D.Double(x0+29, y0+29, x1+15, y1+15);
		l[3]= new Line2D.Double(x0+29, y0+1, x1+15, y1+15);
		b[0]= true;
		b[1]= true;
		b[2]= true;
		b[3]= true;
		
		for (int i= 0; i<murs.size(); i++) {
			for (int j= 0; j<l.length; j++) {
				if (l[j].intersects(murs.get(i))) {
					b[j]= false;
				}
			}
		}
		
		return b[0] || b[1] || b[2] || b[3];
	}
	
	/**
	 * Méthode affichant la carte dans l'objet graphics fourni par la classe mère
	 * 
	 * @param gB, obs
	 * 		objet graphics et observer de la classe mère pour dessiner les images
	 * 
	 */
	public void affichage (Graphics gB, ImageObserver obs) {
		gB.drawImage(imageFond, 0, 0, obs);
		for (int i= 0; i < murs.size(); i++) {
			gB.drawImage(imageMurs, (int)murs.get(i).getX(), (int)murs.get(i).getY(), obs);
		}
	}
	
	/**
	 * Méthode générant les murs en fonction de plans fournis dans un fichier texte généré par la classe générateur
	 * 
	 */
	public void genMurs() {
		ArrayList<String> blueprint= new ArrayList<String>();
		try {

			Scanner sc = new Scanner(new File("carte.txt"));

			while (sc.hasNextLine()) {
				blueprint.add(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Probleme load carte");
			return;
		}
		carteInt= new int[blueprint.size()][blueprint.get(0).length()];
		for (int i= 0; i < blueprint.size(); i++) {
			for (int j= 0; j < blueprint.get(i).length(); j++) {
				if (blueprint.get(i).charAt(j) == '1') {
					carteInt[j][i] = -2;
					murs.add(new Rectangle(j*TAILLE_MUR, i*TAILLE_MUR, TAILLE_MUR, TAILLE_MUR));
				} else {
					carteInt[j][i] = -1;
				}
			}
		}
	}
	
	/**
	 * Méthode qui donne un endroit libre de la carte, ne prend pas en compte la position des personnages
	 * 
	 * @return 
	 * 		le point de l'espace libre
	 * 
	 */
	public Point espaceLibre () {
		Point p= new Point();
		boolean ok;
		do {
			ok= true;
			p.setLocation (TAILLE_MUR*((int)(Math.random()*(NB_CASES-2))+1), TAILLE_MUR*((int)(Math.random()*(NB_CASES-2))+1));
			for (int i= 0; i< murs.size(); i++) {	
				if (p.equals(murs.get(i).getLocation())) {
					ok= false;
				}
			}
		}while (!ok);
		return p;
	}
	
	/**
	 * Accesseur de la hauteur de la carte
	 * 
	 */
	public int getHeight () {
		return height;
	}
	
	/**
	 * Accesseur de la largeur de la carte
	 * 
	 */
	public int getWidth () {
		return width;
	}
	
	public int[][] toInt () {
		int[][] t = new int[carteInt.length][carteInt[0].length];
		for (int i=0; i<t.length; i++) {
			for (int j=0; j<t[0].length; j++) {
				t[i][j] = carteInt[i][j];
			}
		}
		return t;
	}
	
}
