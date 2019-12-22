package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
	
	// format d une url postgresql : jdbc:postgresql://[host]:[post]/[database]
	private static final String URL_DATABASE = "jdbc:postgresql://localhost:5432/Gestion_DB";
	private static Connection connection;

	private static final String user = "postgres";
	private static final String password = "";
	
	// Pour l'enregistrement du driver postgress
	static {
		try {
			
			DriverManager.registerDriver(new org.postgresql.Driver());
			
			connection = DriverManager.getConnection(URL_DATABASE, user, password);
			
			System.out.println("Connexion à la base données réussie !");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static Connection getConnection() {
		return connection;
	}

}
