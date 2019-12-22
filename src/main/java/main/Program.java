package main;


import java.util.List;

import beans.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import database.PersistentManager;
import transaction.BinderTransactional;

public class Program {
	public static void main(String[] args) {
		
		Injector injector = Guice.createInjector(new BinderTransactional());  
		
		try {

	    	  // Connexion à la Database
	    	  PersistentManager pm = PersistentManager.getInstance(); // Singleton
	    	  
	    	  List<Etudiant> LE = pm.retrieveSet(Etudiant.class, "SELECT * FROM ETUDIANT");
	    	  List<Inscription> LI = pm.retrieveSet(Inscription.class, "SELECT * FROM Inscription");
	    	  List<Cours> LC = pm.retrieveSet(Cours.class, "SELECT * FROM Cours");
	    	  
	    	  
	    	  
	    	  //---------------------------------
	    	  // TEST INSERT
	    	  //---------------------------------
	    	  
	    	  Cours coursTest = new Cours("CoursX", "SigleX", "DescriptionX");
	    	  
	    	  Etudiant etudiantTest = new Etudiant(25, "PrénomX", "NomX");
	    	 
	    	  System.out.print("\n\n" + pm.insert(coursTest));
	    	  
	    	  System.out.print("\n\n" + pm.insert(etudiantTest));
	    	  
	    	  Inscription inscriptionTest = new Inscription(etudiantTest, coursTest);
	    	  etudiantTest.addInscription(inscriptionTest);
	    	  
	    	  System.out.print("\n\n" + pm.insert(inscriptionTest));
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  //---------------------------------
	    	  // TEST RETRIEVE
	    	  //---------------------------------
	    	  
	    	  
	    	  // TEST AVEC 2 ÉTUDIANTS
	    	  /*
	    	  List<Etudiant> LE = pm.retrieveSet(Etudiant.class, "SELECT * FROM ETUDIANT Where etudiantid = 2 OR etudiantid = 3");

	    	  System.out.println("\nLISTE DE TOUS LES ÉTUDIANTS : ");
	    	  for(Etudiant e : LE) {
	    		  System.out.println("==================================\n"+ e);
		    	  for(Inscription i : e.getInscription()) {
		    		  System.out.println("\nINSCRIPTION DE " + e.getfname() + "\n" + i);
		    		  System.out.println("\nCOURS DE " + e.getfname() + "\n"+ i.getCours());
		    	  }
		    	  System.out.println("==================================");
	    	  }
	    	  
	    	  */
	    	  
	    	  /*
	    	  // TEST AVEC INSCRIPTIONS
	    	  List<Inscription> LI = pm.retrieveSet(Inscription.class, "SELECT * FROM Inscription");
	    	  
	    	  System.out.println("\n\nLISTE DE TOUTES LES INSCRIPTION : ");
	    	  for(Inscription i : LI) {
	    		  System.out.println("\n==================================");
		    		System.out.println("INSCRIPTION " + i);
		    		System.out.println("\nCOURS ASSOCIÉ\n" + i.getCours());
		    	  System.out.println("==================================");
	    	  }
	    	  */
	    	  
	    	  /*
	    	  
	    	  // TEST AVEC COURS *
	    	  List<Cours> LC = pm.retrieveSet(Cours.class, "SELECT * FROM Cours");
	    	  
	    	  System.out.println("\n\nLISTE DE TOUS LES COURS : ");
	    	  for(Cours c : LC) {
	    		  System.out.println("\n==================================");
		    		System.out.println(c);
		    	  System.out.println("==================================");
	    	  }
	    	  
	    	  */
	    	  
	    	  
	    	  
	    	  
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	      
		

		
	}

}
