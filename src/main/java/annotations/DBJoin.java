package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation pour identifier les clés de jointure de la table incription
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBJoin {
	String internJKey(); // Clé de jointure du bean (Étudiant ou Cours)
	String externJKey(); // Clé lointaine de jointure dans Inscription
}
