package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation pour exclure une variable membre qui ne sera pas dans la BD
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBExclude {} 
