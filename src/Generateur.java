import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.*;

/**
 * Classe permettant de générer facilement diférentes cartes
 * 
 * @author Vincent
 */
public class Generateur extends JFrame implements ActionListener {
	
	private JPanel p;
	private JButton[] extra;
	private ImageIcon img;
	private JButton fin;
	
	/**
	 * Démarrage de l'application
	 * 
	 */
	public static void main (String args[]) {
		new Generateur(Map.NB_CASES);
	}
	
	/**
	 * Constructeur de la classe initialise tout les boutons et labels et met en forme la fenetre
	 * 
	 * @param t
	 * 		taille en nombre de carrés de la fenêtre
	 * 
	 */
	public Generateur (int t) {
		super("Generateur de carte");
		setSize(30*t, 30*t);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		p= new JPanel(new GridLayout(t, t));
		extra= new JButton[(t-2)*(t-2-(t-3)/2)+(t-2-(t-3)/2)*(t-3)/2];
		img= new ImageIcon("images/murs.png");
		fin= new JButton("exporter la carte");
		fin.addActionListener(this);
		fin.setActionCommand("-1");
		
		int m= 0;
		for (int i= 0; i<Map.NB_CASES; i++) {
			p.add(new JLabel(img));
		}
		for (int j= 0; j<Map.NB_CASES-2; j++) {
			for (int k= 0; k<Map.NB_CASES; k++) {
				if (j%2 == 0) {
					if (k== 0 || k == Map.NB_CASES-1) {
						p.add(new JLabel(img));
					} else {
						extra[m]= new JButton();
						extra[m].addActionListener(this);
						extra[m].setActionCommand(String.format("%d", m));
						p.add(extra[m]);
						m++;
					}
				} else {
					if (k%2 == 0) {
						p.add(new JLabel(img));
					} else {
						extra[m]= new JButton();
						extra[m].addActionListener(this);
						extra[m].setActionCommand(String.format("%d", m));
						p.add(extra[m]);
						m++;
					}
				}
			}
		}
		for (int l= 0; l<Map.NB_CASES; l++) {
			p.add(new JLabel(img));
		}
		add(p);
		add(fin, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	/**
	 * Méthode permettant d'exporter la carte dans un fichier texte
	 * 
	 */
	public void export () {
		File f = new File ("carte.txt");
		String s= "";
		int m= 0;
		try {
			FileWriter fw = new FileWriter (f);
			
			for (int i= 0; i<Map.NB_CASES; i++) {
				s+= "1";
			}
			s+= "\n";
			for (int j= 0; j<Map.NB_CASES-2; j++) {
				for (int k= 0; k<Map.NB_CASES; k++) {
					if (j%2 == 0) {
						if (k== 0 || k == Map.NB_CASES-1) {
							s+= "1";
						} else {
							if (extra[m].getIcon() == null) {
								s+= "0";
							} else {
								s+= "1";
							}
							m++;
						}
					} else {
						if (k%2 == 0) {
							s+= "1";
						} else {
							if (extra[m].getIcon() == null) {
								s+= "0";
							} else {
								s+= "1";
							}
							m++;
						}
					}
				}
				s+= "\n";
			}
			for (int l= 0; l<Map.NB_CASES; l++) {
				s+= "1";
			}
			
			fw.write(s);
		 
			fw.close();
		} catch (IOException exception) {
			System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		}
		new Affichage(true);
	}
	
	/**
	 * Méthode implémentée de l'interface pour gérer les clics sur les boutons
	 * 
	 */
	public void actionPerformed (ActionEvent e) {
		int n= Integer.parseInt(e.getActionCommand());
		if (n == -1) {
			export();
			return;
		}
		if (extra[n].getIcon() == null) {
			extra[n].setIcon(img);
		} else {
			extra[n].setIcon(null);
		}
		
	}
	
	/**
	 * 
	 * 
	 */
	public static void aleaGen () {
		String[] tab= new String[Map.NB_CASES];
		String s= "";
		String t= "";
		String r= "";
		for (int i= 0; i<Map.NB_CASES; i++) {
			t+= "1";
			if (i%(Map.NB_CASES - 1) == 0) {
				r+= "1";
			} else {
				r+= "0";
			}
			if (i%2 == 0) {
				s+= "1";
			} else {
				s+= "0";
			}
		}
		for (int j= 0; j<tab.length; j++) {
			switch (j) {
				case 0 : 
					tab[j]= t;
					break;
				case Map.NB_CASES-1 :
					tab[j]= t;
					break;
				default :
					if (j%2 == 0) {
						tab[j]= s;
					} else {
						tab[j]= r;
					}
					break;
			}
		}
		for (int m= 2; m<tab.length-2; m+= 2) {
			for (int n= 2; n<tab.length-2; n+= 2) {
				switch((int)(Math.random()*4)) {
					case 0 :
						tab[m-1]= tab[m-1].substring(0,n) + '1' + tab[m-1].substring(n+1);
						break;
					case 1 :
						tab[m]= tab[m].substring(0, n+1) + '1' + tab[m].substring(n+2);
						break;
					case 2 :
						tab[m+1]= tab[m+1].substring(0,n) + '1' + tab[m+1].substring(n+1);
						break;
					case 3 :
						tab[m]= tab[m].substring(0, n-1) + '1' + tab[m].substring(n);
						break;
					default :
						return;
				}
			}
		}
		conditionnement(tab);
		s= "";
		for (int k= 0; k<tab.length; k++) {
			s+= tab[k]+"\n";
		}
		File f = new File ("carte.txt");
		try {
			FileWriter fw = new FileWriter (f);
			fw.write(s);
			fw.close();
		} catch (IOException exception) {
			System.out.println ("Erreur lors de l'ecriture : " + exception.getMessage());
		}
	}
	
	public static void conditionnement(String[] tab) {
		String[] test= new String[tab.length];
		for (int i= 0; i< test.length; i++) {
			test[i]= tab[i];
		}
		
		contamination(test, 1, 1);
			
		for (int k= 0; k<test.length; k++) {
			for (int l= 0; l<test.length; l++) {
				if (test[k].charAt(l) == '0') {
					tab[k]= tab[k].substring(0, l) + '1' + tab[k].substring(l+1);
				}
			}
		}
	}
	
	public static void contamination (String[] t, int x, int y) {
		t[y]= t[y].substring(0,  x) + '2' + t[y].substring(x+1);
		if (t[y].charAt(x+1) == '0') {
			contamination(t, x+1, y);
		}
		if (t[y].charAt(x-1) == '0') {
			contamination(t, x-1, y);
		}
		if (t[y+1].charAt(x) == '0') {
			contamination(t, x, y+1);
		}
		if (t[y-1].charAt(x) == '0') {
			contamination(t, x, y-1);
		}
	}
	
}
