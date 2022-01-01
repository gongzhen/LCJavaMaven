package annotations.gzannotations;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.MonotonicQualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@GZSubtypeOf(Nullable.class)
@GZTarget(GZElementType.TYPE_USE)
@MonotonicQualifier(GZNonNull.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface GZMonotonicNonNull {
}
