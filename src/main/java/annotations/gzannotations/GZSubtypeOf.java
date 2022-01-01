package annotations.gzannotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface GZSubtypeOf {
    /** An array of the supertype qualifiers of the annotated qualifier */
    Class<? extends Annotation>[] value();
}
