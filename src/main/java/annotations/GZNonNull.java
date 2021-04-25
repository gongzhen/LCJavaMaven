package annotations;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultInUncheckedCodeFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.TypeUseLocation;

import java.lang.annotation.*;

@GZSubtypeOf(GZMonotonicNonNull.class)
@GZImplicitFor(
        literals = {GZLiteralKind.STRING},
        types = {
                GZTypeKind.PACKAGE,
                GZTypeKind.INT,
                GZTypeKind.BOOLEAN,
                GZTypeKind.DOUBLE,
                GZTypeKind.CHAR,
                GZTypeKind.FLOAT,
                GZTypeKind.LONG,
                GZTypeKind.SHORT,
                GZTypeKind.BYTE
        }
)
@DefaultQualifierInHierarchy
@DefaultFor({TypeUseLocation.EXCEPTION_PARAMETER})
@DefaultInUncheckedCodeFor({TypeUseLocation.PARAMETER, TypeUseLocation.LOWER_BOUND})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@GZTarget({GZElementType.TYPE_USE, GZElementType.TYPE_PARAMETER})
public @interface GZNonNull {}
