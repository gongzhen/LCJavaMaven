package annotations;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.*;

import java.lang.annotation.*;

/**
 * {@link Nullable} is a type annotation that indicates that the value is not known to be non-null
 * (see {@link NonNull}). Only if an expression has a {@link Nullable} type may it be assigned
 * {@code null}.
 *
 * <p>This annotation is associated with the {@link
 * org.checkerframework.checker.nullness.AbstractNullnessChecker}.
 *
 * @see NonNull
 * @see MonotonicNonNull
 * @see org.checkerframework.checker.nullness.AbstractNullnessChecker
 * @checker_framework.manual #nullness-checker Nullness Checker
 */
@GZSubtypeOf({})
@GZImplicitFor(literals = GZLiteralKind.NULL, typeNames = java.lang.Void.class)
@GZDefaultInUncheckedCodeFor({GZTypeUseLocation.RETURN, GZTypeUseLocation.UPPER_BOUND})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface GZNullable {
}
