package annotations.gzannotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@GZTarget({GZElementType.ANNOTATION_TYPE})
public @interface GZTarget {
    GZElementType[] value();
}





