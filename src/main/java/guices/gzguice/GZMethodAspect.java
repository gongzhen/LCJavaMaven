package guices.gzguice;

import com.google.common.base.Preconditions;
import com.google.inject.matcher.Matcher;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

final class GZMethodAspect {
    private final Matcher<? super Class<?>> classMatcher;
    private final Matcher<? super Method> methodMatcher;
    private final List<MethodInterceptor> interceptors;

    GZMethodAspect(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, List<MethodInterceptor> interceptors) {
        this.classMatcher = (Matcher) Preconditions.checkNotNull(classMatcher, "class matcher");
        this.methodMatcher = (Matcher)Preconditions.checkNotNull(methodMatcher, "method matcher");
        this.interceptors = (List)Preconditions.checkNotNull(interceptors, "interceptors");
    }

    GZMethodAspect(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {
        this(classMatcher, methodMatcher, Arrays.asList(interceptors));
    }

    boolean matches(Class<?> clazz) {
        return this.classMatcher.matches(clazz);
    }

    boolean matches(Method method) {
        return this.methodMatcher.matches(method);
    }

    List<MethodInterceptor> interceptors() {
        return this.interceptors;
    }
}
