package annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GZLiteralKind {
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#NULL_LITERAL} trees */
    NULL,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#INT_LITERAL} trees */
    INT,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#LONG_LITERAL} trees */
    LONG,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#FLOAT_LITERAL} trees */
    FLOAT,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#DOUBLE_LITERAL} trees */
    DOUBLE,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#BOOLEAN_LITERAL} trees */
    BOOLEAN,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#CHAR_LITERAL} trees */
    CHAR,
    /** Corresponds to {@link com.sun.source.tree.Tree.Kind#STRING_LITERAL} trees */
    STRING,
    /** Shorthand for all other LiteralKind constants, other than PRIMITIVE */
    ALL,
    /**
     * Shorthand for all primitive LiteralKind constants: INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR
     */
    PRIMITIVE;

    /**
     * Returns all LiteralKinds except for ALL and PRIMITIVE (which are shorthands for groups of
     * other LiteralKinds).
     *
     * @return list of LiteralKinds except for ALL and PRIMITIVE
     */
    public static List<GZLiteralKind> allLiteralKinds() {
        List<GZLiteralKind> list = new ArrayList<>(Arrays.asList(values()));
        list.remove(ALL);
        list.remove(PRIMITIVE);
        return list;
    }

    /**
     * Returns the primitive {@code LiteralKind}s: INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR. This is
     * all LiteralKinds except for NULL, STRING, and ones that are shorthands for groups of other
     * LiteralKinds.
     *
     * @return list of LiteralKinds except for NULL and STRING
     */
    public static List<GZLiteralKind> primitiveLiteralKinds() {
        return new ArrayList<>(Arrays.asList(INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR));
    }
}
