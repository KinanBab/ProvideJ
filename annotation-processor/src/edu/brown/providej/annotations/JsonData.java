package edu.brown.providej.annotations;

import edu.brown.providej.annotations.enums.Visibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface JsonData {
    String className();

    Visibility visibility() default Visibility.PACAKGE;

    String data();
}