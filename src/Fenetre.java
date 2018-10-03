import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.image.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Classe contenant le menu principal, destinee a etre executee
 * @author Loubna
 * @author Colin
 */
public class Fenetre extends JFrame implements ActionListener {
	
	JButton bouton1;
	JButton bouton2;
	JButton bouton3;
	JPanel p;
	JLabel jFond, jInstr;//images
	boolean afficherInstructions;
	Image fond;
	Image instructions;
	Timer t;
	double tailleInstructions;//compteur pour l'ouverture du panneau lateral, entre 0 et 1
	final int largeur = 720, hauteur = 835, tailleInstructionsMax = 688;
	
	/**
	 * Constructeur de la classe
	 */
	public Fenetre(){
		super("The Hunt");
		tailleInstructions = 0;
		afficherInstructions = false;

		setLayout(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p= new JPanel();
		p.setLayout(null);
		p.setSize(largeur+tailleInstructionsMax,hauteur);
		
		jFond= new JLabel (new ImageIcon("images/fondHunt.jpg"));
		jFond.setBounds(0,0,largeur+tailleInstructionsMax,hauteur);
		jInstr= new JLabel (new ImageIcon("images/Instructions.png"));
		jInstr.setBounds(710,30,650,720);
		jInstr.setVisible(false);
		
		bouton1 = new JButton("Classique");
		bouton1.setBounds(100,550,150,75);
		bouton1.setFont(new Font("Constantia", 0, 20));
		bouton1.addActionListener(this);
		bouton2 = new JButton("Personnalis"+(char)233);
		bouton2.setBounds(270,550,150,75);
		bouton2.setFont(new Font("Constantia", 0, 20));
		bouton2.addActionListener(this);
		bouton3 = new JButton("Instructions");
		bouton3.setBounds(480,550,140,75);
		bouton3.setFont(new Font("Constantia", 0, 20));
		bouton3.addActionListener(this);
		
		p.add(bouton1);
		p.add(bouton2);
		p.add(bouton3);
		p.add(jInstr);
		p.add(jFond);
		
		setContentPane(p);
		
		t = new Timer (10, this);//pour le deroulement du panneau avec les instructions
		t.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (afficherInstructions) {//si on veut afficher on augmente la taille, sinon on la diminue
					tailleInstructions += 0.02;
				} else {
					tailleInstructions -= 0.04;
				}

				if (tailleInstructions<=0 || tailleInstructions>=1) {
					if(tailleInstructions<=0) {
						tailleInstructions=0;
					}
					if(tailleInstructions>1) {
						tailleInstructions=1;
						jInstr.setVisible(true);//on affiche les instructions quand le panneau est ouvert
					}
					t.stop();//on a fini de changer la taille de la fenetre
				}
				//on change la taille suivant la valeur du compteur, avec une fonction pour la fluidite
				setSize(largeur+getInsets().left+getInsets().right+(int)(tailleInstructionsMax*(1-Math.cos(Math.PI*tailleInstructions/2))), hauteur+getInsets().top+getInsets().bottom);
				repaint();
			}
		});
		
		setVisible(true);
		p.setLocation(getInsets().left, getInsets().top);
		setSize(largeur+getInsets().left+getInsets().right, hauteur+getInsets().top+getInsets().bottom);
		
		
	}	
	
	public static void main (String [] args) {
		new Fenetre();
	}
	
	/**
	 * Gestion des boutons
	 */
	public void actionPerformed (ActionEvent event) {
		if (bouton1==event.getSource()) {
			new Affichage(false);//creation d'un jeu avec carte aleatoire
			setVisible(false);
		} else if (bouton2==event.getSource()) {
			new Generateur(Map.NB_CASES);//creation de jeu avec une carte personnalisee
			setVisible(false);
		} else if (bouton3==event.getSource()) {//afficher/masquer les instructions
			afficherInstructions = !afficherInstructions;
			if (afficherInstructions==false) {//on masque l'image des qu'on commence a refermer le panneau
				jInstr.setVisible(false);
			}
			t.start();
		}
					
	}
			
		
}


	
		
			
