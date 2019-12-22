package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation pour indiquer que la variable membre sera autoincrementer
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBAutoIncrement {
	boolean isAutoIncrement() default true;
}
