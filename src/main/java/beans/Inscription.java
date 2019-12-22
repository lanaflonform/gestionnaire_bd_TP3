package beans;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import annotations.DBAutoIncrement;
import annotations.DBColumn;
import annotations.DBExclude;
import annotations.DBJoin;
import annotations.DBPrimaryKey;
import annotations.DBTable;

@DBTable(tableName = "Inscription", primaryKey = "inscriptionid")
public class Inscription {
	
	    //---------------------------------------
        // Fields and Attributes
        //---------------------------------------
		@DBAutoIncrement
		@DBPrimaryKey
		@DBColumn(columnName = "inscriptionid")
	    private int inscriptionID;
		
		@DBColumn(columnName = "etudiantid")
	    private int etudiantID;
		
		@DBColumn(columnName = "coursid")
	    private int coursID;
		
		@DBColumn(columnName = "date")
		private Timestamp date; 
	    
	    @DBJoin(internJKey = "coursid", externJKey = "coursid")
	    private Cours cours;
	    
	    @DBExclude()
	    private Etudiant etudiant;
        
	    //---------------------------------------
	    // Constructor
	    //---------------------------------------
	    public Inscription() {} // constructeur vide pour construction avec setters
	    
	    public Inscription(Etudiant etudiant, Cours cours) {
	    		this.inscriptionID = 0; // default
		        this.coursID = cours.getCoursID();
		        this.etudiantID = etudiant.getEtudiantid();
		        this.date = Timestamp.valueOf(LocalDateTime.now());
		        this.setEtudiant(etudiant);
		        this.setCours(cours);
		        
	    }
	    
	    //---------------------------------------
	    // Getters and Setters
	    //---------------------------------------
	    public int getInscriptionID() {
	        return inscriptionID;
	    }

	    public void setInscriptionID(int inscriptionID) {
	        this.inscriptionID = inscriptionID;
	    }
	    
	    public int getEtudiantID() {
	        return etudiantID;
	    }

	    public void setEtudiantID(int etudiantID) {
	        this.etudiantID = etudiantID;
	    }

	    public int getCoursID() {
	        return coursID;
	    }

	    public void setCoursID(int coursID) {
	        this.coursID = coursID;
	    }
	    
	    public Timestamp getDate() {
	        return date;
	    }

	    public void setDate(Timestamp date) {
	        this.date = date;
	    }
	    
		public Etudiant getEtudiant() {
			return etudiant;
		}

		public void setEtudiant(Etudiant etudiant) {
			this.etudiant = etudiant;
		}

		public Cours getCours() {
			return cours;
		}

		public void setCours(Cours cours) {
			this.cours = cours;
		}

	    //---------------------------------------
	    // toString Method
	    //---------------------------------------
	    @Override
	    public String toString() {
	        return "Etudiant ID = " + etudiantID +
	        		"\n" + "Cours ID = " + coursID + 
	        		"\n" + "Date = " + date; 
	    }


	}
