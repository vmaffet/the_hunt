import java.awt.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Cette classe gere le jeu, 
 * 
 * @author Loubna
 * @author Vincent
 *
 */
public class Affichage extends JFrame implements KeyListener, ActionListener{
	
	private BufferedImage fond, imgsortie, ecranPause, ecranFin, ecranMort;
	private Graphics gFond;
	private Map carte;
	private Joueur jp;
	private Trucidator bp;
	private Timer tm;
	private Point sortie;
	private Objet[] items;
	private boolean custom, fin, mort;
	private int score, tempsBonusAffEnnemi, tempsBonusAffSortie, tempsTetanise, tempsBonusAffJoueur;
	private Clip music;
	private BufferedImage[] sonarRouge, sonarVert;
	private Point posSonarRouge, posSonarVert;
	public final int TPS_TIMER= 50;
	public final int NB_OBJETS= 4;
	
	public static void main (String[] args) {
		new Affichage(false);
	}
	
	/**
	 * Constructeur de la classe
	 * 
	 * @param cus
	 * 		permet de lancer une partie avec une map customisee ou non
	 */
	public Affichage (boolean cus) {
		super("THE HUNT");
		setSize(Map.TAILLE_MUR*Map.NB_CASES, Map.TAILLE_MUR*Map.NB_CASES);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fond= new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
		gFond= fond.getGraphics();
		addKeyListener(this);
		setResizable(false);
		
		// recuperer les differentes images
		imgsortie= ouvrirImage("Sortie.png");
        ecranPause= ouvrirImage("pause.png");
        ecranFin= ouvrirImage("fin.png");
        ecranMort= ouvrirImage("mort.png");
        sonarRouge= new BufferedImage[3];
        for (int i= 0; i<sonarRouge.length; i++) {
        	sonarRouge[i]= ouvrirImage("CR"+i+".gif");
        }
        sonarVert= new BufferedImage[3];
        for (int i= 0; i<sonarVert.length; i++) {
        	sonarVert[i]= ouvrirImage("CV"+i+".gif");
        }
        posSonarRouge = new Point(0,0);
        posSonarVert = new Point(0,0);
		
		custom= cus;
		mort= false;
		score= 0;
		
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("sons/fond.wav").getAbsoluteFile());
	        music = AudioSystem.getClip();
	        music.open(audioInputStream);
		} catch (Exception e) {
			System.out.println("Probleme musique de fond");
		}
		
		demarrage();
		
		setVisible(true);
		setSize(Map.TAILLE_MUR*Map.NB_CASES+getInsets().left+getInsets().right, Map.TAILLE_MUR*Map.NB_CASES+getInsets().top+getInsets().bottom);
	} 
	
	/**
	 * Methode permettant de lancer une nouvelle partie 
	 * 
	 */
	public void demarrage () {
		carte= new Map(custom);
		jp= new Joueur(carte.espaceLibre(), carte);
		do {
			sortie= carte.espaceLibre();
			bp= new Trucidator(sortie, score, carte);
		} while (sortie.distance(jp.getPosition()) < 300);
		items= new Objet[NB_OBJETS];
		for (int i= 0; i<items.length; i++) {
			do {
				items[i]= new Objet(carte.espaceLibre());
			} while (items[i].getPosition().equals(sortie) || items[i].getPosition().equals(jp.getPosition()) || pasSurAutres(i));
		}
		fin= false;
		tempsBonusAffEnnemi= 0;
		tempsBonusAffSortie= 0;
		tempsBonusAffJoueur= 0;
		tempsTetanise= 0;
		tm = new Timer(TPS_TIMER, this);
		tm.start();
		musique(true);
	}
	
	/**
	 * Methode gerant tout l'affichage de la classe
	 * 
	 */
	public void paint (Graphics g) {
		carte.affichage(gFond, this);
		gFond.drawImage(imgsortie, (int)sortie.getX(), (int)sortie.getY(), this);
		bp.afficher(gFond, this);
		for (int i= 0; i<items.length; i++) {
			items[i].afficher(gFond, this);
		}
		
		jp.affichage(gFond, this);
		
		if (tempsBonusAffJoueur > 0) {
			tempsBonusAffJoueur-= TPS_TIMER;
			gFond.drawImage(sonarVert[((5000-tempsBonusAffJoueur)/15)%3], posSonarVert.x, posSonarVert.y, this);
		}
		
		if (tempsBonusAffEnnemi > 0) {
			tempsBonusAffEnnemi-= TPS_TIMER;
			gFond.drawImage(sonarRouge[((5000-tempsBonusAffEnnemi)/15)%3], posSonarRouge.x, posSonarRouge.y, this);
		}
		if (tempsBonusAffSortie > 0) {
			tempsBonusAffSortie-= TPS_TIMER;
			gFond.drawImage(imgsortie, (int)sortie.getX(), (int)sortie.getY(), this);
		}
		
		gFond.setColor(Color.white);
		gFond.setFont(new Font("Arial", 0, 20));
		gFond.drawString(String.format("Score : %d", score), 10, 25);
		if (!tm.isRunning()) {
			if (fin) {
				gFond.drawImage(ecranFin, 115, 245, this);
				jouerSon("victoire.wav");
			} else {
				if (mort) {
					gFond.drawImage(ecranMort, 115, 245, this);
					jouerSon("mort.wav");
				} else {
					gFond.drawImage(ecranPause, 115, 245, this);
					jouerSon("pause.wav");
				}
			}
			musique(false);
		}
		
		g.drawImage(fond, getInsets().left, getInsets().top, this);
	}
	
	/**
	 * Methode implementee de l'interface KeyListener, gerant les deplacements du joueur 
	 * 
	 */
	public void keyReleased (KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				jp.supprimerDir(2);
				break;
			case KeyEvent.VK_RIGHT:
				jp.supprimerDir(0);
				break;
			case KeyEvent.VK_DOWN:
				jp.supprimerDir(3);
				break;
			case KeyEvent.VK_UP:
				jp.supprimerDir(1);
				break;
			default:
				return;
		}
	}
	
	/**
	 * Methode implementee de l'interface KeyListener, gerant les deplacements du joueur 
	 * 
	 */
	public void keyPressed (KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				jp.ajouterDir(2);
				break;
			case KeyEvent.VK_RIGHT:
				jp.ajouterDir(0);
				break;
			case KeyEvent.VK_DOWN:
				jp.ajouterDir(3);
				break;
			case KeyEvent.VK_UP:
				jp.ajouterDir(1);
				break;
			case KeyEvent.VK_ENTER:
				if (tm.isRunning()) {
					tm.stop();
					repaint();
				} else {
					if (fin) {
						score++;
						demarrage();
					} else {
						if (mort) {
							score= 0;
							mort= false;
							demarrage();
						}
						tm.start();
						musique(true);
					}
				}
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			default:
				return;
		}
	}
	
	/**
	 * Methode implementee de l'interface Keylistenner, non utilisee dans ce programme
	 * 
	 */
	public void keyTyped (KeyEvent e) {}
	
	/**
	 * Methode implementee de l'interface ActionListener, gere les deplacements, appelee par le timer
	 * 
	 */
	public void actionPerformed (ActionEvent e) {
		if (tempsTetanise <= 0) {
			jp.deplacer();
		} else {
			tempsTetanise-= TPS_TIMER;
		}	
		bp.move();
		if (bp.getHitbox().intersects(jp.getHitbox())) {
			tm.stop();
			mort= true;
		}
		for (int i= 0; i<items.length; i++) {
			if (items[i].getHitbox().intersects(jp.getHitbox())) {
				items[i].appliquer(jp, bp, carte, this);
			}
		}
		if (new Rectangle(sortie.x, sortie.y, Map.TAILLE_MUR, Map.TAILLE_MUR).intersects((jp.getHitbox()))) {
			tm.stop();
			fin= true;
		}
		repaint();
		if (carte.vision(bp.box.x, bp.box.y, jp.getX(), jp.getY())) {
			bp.creerChemin(jp.getPosition());
		}
	}
	
	/**
	 * Methode permettant d'ouvrir des images
	 * 
	 * @param nom
	 * 		nom de l'image desiree
	 * @return
	 * 		L'image demandee
	 */
	public static BufferedImage ouvrirImage (String nom) {
		try {
			return ImageIO.read(new File("images/" + nom));
		} 
		catch(Exception err) {
			System.out.println(nom+" introuvable !");
			System.exit(0);
			return new BufferedImage(0,0,0);
		}
	}
	
	/**
	 * Methode indicant si les objets ne sont pas places les uns sur les autres
	 * 
	 * @param index
	 * 		index de l'objet a tester dans le tableau
	 * @return
	 * 		booleen sur le bon placement de l'objet
	 */
	public boolean pasSurAutres (int index) {
		boolean b= false;
		for (int i= 0; i<items.length; i++) {
			if (i != index && items[i] != null) {
				b= b || items[index].getPosition().equals(items[i].getPosition());
			}
		}
		return b;
	}
	
	/**
	 * Mutateur  du temps pendant lequel le joueur ne peut pas bouger 
	 * 
	 * @param t
	 * 		le temps en millisecondes pendant lequel le joueur ne peut pas bouger
	 */
	public void setTempsTetanise (int t) {
		tempsTetanise= t;
	}
	
	/**
	 * Mutateur  du temps pendant lequel la sortie est montree au joueur 
	 * 
	 * @param t
	 * 		le temps en millisecondes pendant lequel la sortie est montree au joueur
	 */
	public void setTempsAffSortie (int t) {
		tempsBonusAffSortie= t;
	}
	
	/**
	 * Mutateur  du temps pendant lequel la position de l'ennemi est montree au joueur 
	 * 
	 * @param t
	 * 		le temps en millisecondes pendant lequel la position de l'ennemi est montree au joueur
	 */
	public void setTempsAffEnnemi (int t) {
		posSonarRouge = bp.getPosition();
		tempsBonusAffEnnemi= t;
	}
	
	/**
	 * Mutateur  du temps pendant lequel on notifie le joueur que l'ennemi vient la ou il se trouve 
	 * 
	 * @param t
	 * 		le temps en millisecondes pendant lequel on notifie le joueur que l'ennemi vient la ou il se trouve
	 */
	public void setTempsAffJoueur (int t) {
		posSonarVert = jp.getPosition();
		tempsBonusAffJoueur= t;
	}
	
	/**
	 * Methode permettant de joueur un petit son
	 * 
	 * @param s
	 * 		nom du fichier du son que l'on veut jouer
	 */
	public void jouerSon (String s) {
		try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("sons/"+s).getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	
	/**
	 * Methode permettant de faire jouer ou non la musique de fond
	 * 
	 * @param jouer
	 * 		booleen qui informe si l'on veut jouer ou arreter la musique
	 */
	public void musique (boolean jouer) {
		if (jouer) {
			music.loop(Clip.LOOP_CONTINUOUSLY);
			music.start();
		} else {
			music.stop();
		}
	}
}
