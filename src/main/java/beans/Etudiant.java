package beans;

import java.util.ArrayList;
import java.util.List;

import annotations.DBAutoIncrement;
import annotations.DBColumn;
import annotations.DBJoin;
import annotations.DBPrimaryKey;
import annotations.DBTable;

@DBTable(tableName = "Etudiant", primaryKey = "etudiantid")
public class Etudiant {
	
	//---------------------------------------
    // Fields and Attributes
    //---------------------------------------
	@DBAutoIncrement
	@DBPrimaryKey
	@DBColumn(columnName = "etudiantid")
	private int etudiantid;
	
	@DBColumn(columnName = "age")
    private int age;
	
	@DBColumn(columnName = "fname")
    private String fname;
	
	@DBColumn(columnName = "lname")
    private String lname; 
    
    @DBJoin(internJKey = "etudiantid", externJKey = "etudiantid")
    private List<Inscription> inscriptions;

    //---------------------------------------
    // Constructor
    //---------------------------------------
    public Etudiant() {} // constructeur vide pour la construction de l'objet avec setters
    
    public Etudiant(int age, String fname, String lname) {
    	this.etudiantid = 0; // default
        this.age = age;
        this.fname = fname;
        this.lname = lname;
        this.inscriptions = new ArrayList<Inscription>();
    }
    
    //---------------------------------------
    // Getters and Setters
    //---------------------------------------
    public List<Inscription> getInscription() {
        return inscriptions;
    }

    public void setInscription(ArrayList<Inscription> inscription) {
        this.inscriptions = inscription;
    }
    
    public void addInscription(Inscription inscription) {
        this.inscriptions.add(inscription);
    }


    public int getEtudiantid() {
        return etudiantid;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantid = etudiantId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getfname() {
        return fname;
    }

    public void setfname(String fname) {
        this.fname = fname;
    }

    public String getlname() {
        return lname;
    }

    public void setlname(String lname) {
        this.lname = lname;
    }

    //---------------------------------------
    // toString Method
    //---------------------------------------
    @Override
    public String toString() {
        return "Etudiant ID = " + etudiantid +
                "\nAge = " + age +
                "\nFirstname = " + fname + 
                "\nLastname = " + lname;
    }

}