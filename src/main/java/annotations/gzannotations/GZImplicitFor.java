package annotations.gzannotations;

public @interface GZImplicitFor {

    /**
     * @return {@link LiteralKind}s for which an annotation should be implicitly added. For example,
     *     if {@code @MyAnno} is meta-annotated with
     *     {@code @ImplicitFor(literals={LiteralKind.STRING})}, then a literal {@code String}
     *     constant such as {@code "hello world"} has type {@code @MyAnno String}, but other
     *     occurrences of {@code String} in the source code are not affected. For String literals,
     *     also see the {@link #stringPatterns} annotation field.
     */
    GZLiteralKind[] literals() default {};

    /** @return {@link TypeKind}s of types for which an annotation should be implicitly added */
    GZTypeKind[] types() default {};

    /**
     * @return {@link Class}es (in the actual program) for which an annotation should be implicitly
     *     added. For example, if {@code @MyAnno} is meta-annotated with
     *     {@code @ImplicitFor(typeNames=String.class)}, then every occurrence of {@code String} is
     *     actually {@code @MyAnno String}. This has the same effect as writing the annotation on
     *     the class definition (possibly in an annotated library):
     *     <pre>
     *   class @MyAnno String {...}
     * </pre>
     *     As another example, {code java.lang.Void.class} should receive the same annotation as the
     *     {@code null} literal.
     */
    Class<?>[] typeNames() default {};

    /**
     * @return regular expressions of string literals, the types of which an annotation should be
     *     implicitly added. If multiple patterns match, then the string literal is given the
     *     greatest lower bound of all the matches.
     */
    String[] stringPatterns() default {};
}
