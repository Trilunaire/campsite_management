import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 * Classe qui défini le calendrier qui permet de gérer les différentes réservations d'un emplacement
 * @author trilunaire
 * @see JFrame
 */
@SuppressWarnings("serial")
public class Calendrier extends JFrame{
	static private String[] libelle={"L","M","M","J","V","S","D"};
	static private String[] mois={"Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre"};
	
	private int anneeCourante;
	
	private Calendar calendarUser;
	
	private Vector<String> jourReserve;
	private Vector<JButton> boutonGrise;
	private Vector<JButton> boutonReserve;
	
	private JPanel pPrincipal;
	private JPanel pJours;
	private JPanel pMois;
	
	private JLabel lMois;
	
	private JButton flecheDroite;
	private JButton flecheGauche;
	
	private Emplacement emp;
	
	public Calendrier(Emplacement e){
		this.setTitle("planing");
		this.setMinimumSize(new Dimension(400,400));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.emp=e;
		this.calendarUser=GestionTemp.get_CalendarToday();
		this.jourReserve=new Vector<String>();
		this.boutonGrise=new Vector<JButton>();
		this.boutonReserve=new Vector<JButton>();
		this.anneeCourante=calendarUser.get(Calendar.YEAR);
		
		this.createGUI();
	}
	
	/**
	 * Méthode qui permet de créer toute l'interface du calendrier
	 */
	public void createGUI(){
		pPrincipal=new JPanel();
		pPrincipal.setLayout(new BoxLayout(pPrincipal,BoxLayout.Y_AXIS));
		pPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		pJours=new JPanel();
		pJours.setLayout(new GridLayout(7,7,2,2));
		
		this.majPanel();
		
		pPrincipal.add(this.selectionMois());
		pPrincipal.add(pJours);
		this.setContentPane(pPrincipal);
	}
	
	
	/**
	 * Méthode de classe qui permet de mettre à jour la disposition des jours du calendrier lors du changement de mois
	 */
	public void majPanel(){
		boutonGrise.clear();
		boutonReserve.clear();
		pJours.removeAll();
		
		int nbreDeJours=GestionTemp.nbreJourDansMois(calendarUser);
		int i=0;
		int j=0;
		int nbreJoursRAfficher=0;
		
		jourReserve=emp.reservDaysInMonth(calendarUser);
		System.out.println("Nombre de jours du mois "+nbreDeJours);
		Calendar copie=(Calendar) calendarUser.clone();
		
		for(i=0;i<libelle.length;i++){
			pJours.add(new JLabel(libelle[i], JLabel.CENTER));
		}

		copie.set(copie.get(Calendar.YEAR), copie.get(Calendar.MONTH), 1);//on prends le premier jour du mois
		
		//dans le cas ou le premier jour du mois est un lundi, la répartition des jours sera plus homogène avec des jours grisé avant
		if(GestionTemp.numeroJourSemaineEurope(copie)==1){
			copie.set(Calendar.DAY_OF_MONTH, copie.get(Calendar.DAY_OF_MONTH)-7);
			for(i=0;i<7;i++){//on ajoute une semaine de boutons gris pour une répartition homogène des boutons
				System.out.println("Valeur de copie "+GestionTemp.calendarToString(copie));
				boutonGrise.add(new JButton(copie.get(Calendar.DAY_OF_MONTH)+""));
				boutonGrise.lastElement().setEnabled(false);
				System.out.println("Ajout de 1 bouton grisé");
				pJours.add(boutonGrise.lastElement());
				copie.set(Calendar.DAY_OF_MONTH, copie.get(Calendar.DAY_OF_MONTH)+1);
			}
		}else{
			while(GestionTemp.numeroJourSemaineEurope(copie)!=1){
				copie.set(Calendar.DAY_OF_MONTH, copie.get(Calendar.DAY_OF_MONTH)-1);//on réduit jusqu'a ce qu'on atteigne un lundi
				System.out.println("Valeur de copie "+GestionTemp.calendarToString(copie));
			}
			
			while(copie.get(Calendar.DAY_OF_MONTH)!=1){//on ne peut pas ajouter deux fois le même élément ou il sera écrasé, du coup on les stocke dans un vecteur
				System.out.println("Valeur de copie "+GestionTemp.calendarToString(copie));
				boutonGrise.add(new JButton(copie.get(Calendar.DAY_OF_MONTH)+""));
				boutonGrise.lastElement().setEnabled(false);
				System.out.println("Ajout de 1 bouton grisé");
				pJours.add(boutonGrise.lastElement());
				copie.set(Calendar.DAY_OF_MONTH, copie.get(Calendar.DAY_OF_MONTH)+1);
				i++;
			}	
		}
		
		for(i=1; i<=nbreDeJours;i++){
			if(!jourReserve.isEmpty()){
				if(j<jourReserve.size() && String.valueOf(i).compareTo(jourReserve.get(j))==0){
					boutonReserve.add(new JButton(i+""));
					boutonReserve.get(j).setBackground(Color.RED);
					boutonReserve.get(j).addActionListener(new BoutonJour());
					pJours.add(boutonReserve.get(j));
					j++;
				}else{
					pJours.add(new JButton(i+""){
									{addActionListener(new BoutonJour());}
									});
				}
			}else{
				pJours.add(new JButton(i+""){
					{addActionListener(new BoutonJour());}
					});
			}

		}
		
		//on doit afficher un certain nombre de jours après la fin du mois, sinon le gridLayout est déformé (6 colones au lieu de sept)
		nbreJoursRAfficher=42-(nbreDeJours+boutonGrise.size());
		for(i=0; i<nbreJoursRAfficher; i++){
			boutonGrise.add(new JButton(copie.get(Calendar.DAY_OF_MONTH)+""));
			boutonGrise.lastElement().setEnabled(false);
			pJours.add(boutonGrise.lastElement());
			copie.set(Calendar.DAY_OF_MONTH, copie.get(Calendar.DAY_OF_MONTH)+1);
		}
	}
	
	/**
	 * Méthode qui permet de créer le Panel de séléction du mois
	 * @return un JPanel composé de deux JButtons permettant le changement du mois
	 */
	public JPanel selectionMois(){
		pMois=new JPanel();
		lMois=new JLabel();
		flecheGauche=new JButton("◀");
		flecheDroite=new JButton("▶");
		
		pMois.setLayout(new BoxLayout(pMois,BoxLayout.X_AXIS));
		pMois.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
		flecheGauche.addActionListener(new SelectionMois());
		flecheDroite.addActionListener(new SelectionMois());
		
		
		pMois.add(flecheGauche);
		updateMonthName();
		pMois.add(Box.createHorizontalGlue());
		pMois.add(lMois);
		pMois.add(Box.createHorizontalGlue());
		pMois.add(flecheDroite);
		return pMois;
	}
	
	/**
	 * Méthode d'instance qui permet de mettre à jour le JLabel correspondant au nom du mois
	 */
	public void updateMonthName(){
		if(calendarUser.get(Calendar.YEAR)==anneeCourante){
			lMois.setText(mois[calendarUser.get(Calendar.MONTH)]);
		}else{
			lMois.setText(mois[calendarUser.get(Calendar.MONTH)]+" "+calendarUser.get(Calendar.YEAR));
		}
		lMois.setAlignmentX(Box.CENTER_ALIGNMENT);
	}	
	
	/**
	 * Classe interne definissant un listener qui sera déclenché lors d'un appuis sur un bouton pour changer de mois
	 * @author trilunaire
	 * @see ActionListener
	 */
	public class SelectionMois implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==flecheGauche){
				calendarUser.set(Calendar.MONTH,calendarUser.get(Calendar.MONTH)-1);
				updateMonthName();//test
				majPanel();
			}else if(e.getSource()==flecheDroite){
				calendarUser.set(Calendar.MONTH,calendarUser.get(Calendar.MONTH)+1);
				updateMonthName();//test
				majPanel();
			}
		}
	}
	
	
	
	public class BoutonJour implements ActionListener{
		int jourSelection;
		
		Calendar calTmp=GestionTemp.get_CalendarToday();
		JButton btTmp;
		
		FAjoutReserv fajout;
		
		public void actionPerformed(ActionEvent e){
			//prendre le bouton, puis la valeur du bouton, et faire une fenetre d'ajout de reservation
			btTmp= (JButton) e.getSource();
			
			if(btTmp.getBackground()!=Color.RED){//si la couleur du bouton est autre que rouge (si on a pas séléctionné un jour réservé)
				jourSelection= Integer.parseInt(btTmp.getText());
				System.out.println("Jour séléctionné: "+jourSelection);
				calTmp.set(calendarUser.get(Calendar.YEAR), calendarUser.get(Calendar.MONTH), jourSelection);
				//instancier une fenetre de ajout de reservation à partir d'un calendar
				
				fajout= new FAjoutReserv(emp, calTmp);
				fajout.setVisible(true);
			}
		}
	}
	
	/*
	public static void main(String[] args){
		Calendrier test=new Calendrier();
		test.setVisible(true);
	}*/
}