package exceptions;

// Classe pour les exeptions du PersistantManager
public class PersistantManagerException extends Exception{

	private static final long serialVersionUID = 1L;

	
	public PersistantManagerException(String errorMessage) {
		super(errorMessage);
	}

}
