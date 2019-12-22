package beans;

import java.util.List;

import annotations.DBAutoIncrement;
import annotations.DBColumn;
import annotations.DBExclude;
import annotations.DBPrimaryKey;
import annotations.DBTable;

@DBTable(tableName = "Cours", primaryKey = "coursid")
public class Cours {
	
	   //---------------------------------------
       // Fields and Attributes
       //---------------------------------------
		@DBAutoIncrement
		@DBPrimaryKey
		@DBColumn(columnName = "coursid")
	    private int coursID;
		
		@DBColumn(columnName = "name")
	    private String name;
		
		@DBColumn(columnName = "sigle")
	    private String sigle;
		
		@DBColumn(columnName = "description")
	    private String description;	
	    
	    @DBExclude()
	    private List<Inscription> inscription;

	    //---------------------------------------
	    // Constructor
	    //---------------------------------------
	    public Cours() {} // constructeur vide pour la création d'objet avec setters
	    
	    public Cours(String name, String sigle, String description) {
	    	this.coursID = 0; // default
	        this.name = name;
	        this.sigle = sigle;
	        this.description = description;
	    }
	
	    //---------------------------------------
	    // Getters and Setters
	    //---------------------------------------
	    public int getCoursID() {
	        return coursID;
	    }

	    public void setCoursID(int coursID) {
	        this.coursID = coursID;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getSigle() {
	        return sigle;
	    }

	    public void setSigle(String sigle) {
	        this.sigle = sigle;
	    }

	    public String getDescription() {
	        return description;
	    }

	    public void setDescription(String description) {
	        this.description = description;
	    }

	    public List<Inscription> getInscription() {
	        return inscription;
	    }

	    public void setInscription(List<Inscription> inscription) {
	        this.inscription = inscription;
	    }

	    //---------------------------------------
	    // toString Method
	    //---------------------------------------
	    @Override
	    public String toString() {
	        return  "ID = " + coursID +
	                "\nName = " + name +
	                "\nSigle = " + sigle +
	                "\nDescription = " + description;
	    }
	}
