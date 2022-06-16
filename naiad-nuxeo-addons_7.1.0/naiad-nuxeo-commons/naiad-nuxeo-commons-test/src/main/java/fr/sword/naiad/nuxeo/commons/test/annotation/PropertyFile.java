package fr.sword.naiad.nuxeo.commons.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation permettant de spécifier une liste de fichiers .properties à charger avant les tests.
 * 
 * @author fmh
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PropertyFile {
	String[] value();
}
